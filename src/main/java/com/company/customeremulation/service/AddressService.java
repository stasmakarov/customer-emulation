package com.company.customeremulation.service;

import com.company.customeremulation.configuration.WebinarConfig;
import com.company.customeremulation.event.ServiceUnavailableEvent;
import com.company.customeremulation.service.record.MapPoint;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AddressService {
    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    // --- Dependencies -------------------------------------------------------
    // Prefer constructor injection to avoid nulls when bean is created in tests without Spring context
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WebinarConfig webinarConfig;
    private final ApplicationEventPublisher applicationEventPublisher;

    // --- Config -------------------------------------------------------------
    // Nominatim requires a proper User-Agent (see their usage policy).
    private static final String USER_AGENT = "customer-emulation/1.0 (contact: admin@example.com)"; // TODO: externalize
    private static final String NOMINATIM_URL =
            "https://nominatim.openstreetmap.org/reverse?lat={lat}&lon={lon}&format=geojson&addressdetails=1";

    // Flapping detector: switched to instance field; make it cluster-aware if needed
    private volatile boolean isNominatimServiceAvailable = true;

    public AddressService(RestTemplate restTemplate,
                          ObjectMapper objectMapper,
                          WebinarConfig webinarConfig,
                          ApplicationEventPublisher applicationEventPublisher) {
        this.restTemplate = Objects.requireNonNull(restTemplate, "RestTemplate is required");
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper is required");
        this.webinarConfig = Objects.requireNonNull(webinarConfig, "WebinarConfig is required");
        this.applicationEventPublisher = Objects.requireNonNull(applicationEventPublisher, "ApplicationEventPublisher is required");
    }

    /**
     * Reverse geocoding via Nominatim.
     * Strategy:
     * 1) Try original point, require house number.
     * 2) Try up to 3 jittered points (~40m) accepting road-level address.
     * 3) As a last resort, accept road-level address on original point.
     */
    public Optional<String> findAddress(MapPoint point) {
        // Defensive checks: MapPoint must be non-null
        if (point == null) {
            log.warn("findAddress called with null point");
            return Optional.empty();
        }

        // First attempt: require house number (most precise)
        Optional<String> strict = reverseOnce(point.lat(), point.lon(), true);
        if (strict.isPresent()) return strict;

        // Second attempt(s): jitter around original point within ~40 m and accept road-level address
        for (int i = 0; i < 3; i++) {
            double[] jitter = jitterMeters(point.lat(), point.lon(), 40.0); // ~40m radius
            Optional<String> loose = reverseOnce(jitter[0], jitter[1], false);
            if (loose.isPresent()) return loose;
        }

        // Last resort: accept display_name even if no house_number at the original point
        return reverseOnce(point.lat(), point.lon(), false);
    }

    /**
     * Single reverse geocoding call with optional house-number requirement.
     */
    private Optional<String> reverseOnce(double lat, double lon, boolean requireHouseNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    NOMINATIM_URL + "&zoom=18&namedetails=0",
                    HttpMethod.GET,
                    httpEntity,
                    String.class,
                    lat,
                    lon
            );
            isNominatimServiceAvailable = true;
        } catch (RestClientException e) {
            log.error("Nominatim unavailable: {}", e.getMessage());
            onFirstDowntime();
            return Optional.empty();
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Nominatim error: HTTP {} - {}", response.getStatusCode(), response.getBody());
            onFirstDowntime();
            return Optional.empty();
        }

        String body = response.getBody();
        if (body == null || body.isBlank()) {
            log.warn("Nominatim returned empty body for lat={}, lon={}", lat, lon);
            return Optional.empty();
        }

        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode features = root.path("features");
            if (!features.isArray() || features.size() == 0) return Optional.empty();

            JsonNode firstProps = features.get(0).path("properties");
            String displayName = optText(firstProps, "display_name");
            JsonNode address = firstProps.path("address");
            String houseNumber = optText(address, "house_number");

            if (requireHouseNumber) {
                if (houseNumber == null || houseNumber.isBlank()) return Optional.empty();
                if (displayName != null && !displayName.isBlank()) return Optional.of(displayName);
                String built = buildAddressFromFields(address);
                return built.isBlank() ? Optional.empty() : Optional.of(built);
            } else {
                if (displayName != null && !displayName.isBlank()) return Optional.of(displayName);
                String built = buildAddressFromFields(address);
                return built.isBlank() ? Optional.empty() : Optional.of(built);
            }
        } catch (Exception e) {
            log.error("Cannot parse address for lat={}, lon={}: {}", lat, lon, e.getMessage());
            return Optional.empty();
        }
    }

    private void onFirstDowntime() {
        // Fire single event on first observed downtime to avoid event storms
        if (isNominatimServiceAvailable) {
            isNominatimServiceAvailable = false;
            applicationEventPublisher.publishEvent(new ServiceUnavailableEvent(this, "Nominatim"));
        }
    }

    // --- Random points ------------------------------------------------------

    /**
     * Generate a random point either inside the bounding box or outside it within a buffer.
     * @param outside if true – point will be out of the box using 100 kilometers buffer.
     */
    public MapPoint randomPoint(boolean outside) {
        if (outside) {
            return randomPointOutside(100_000); // 100 km buffer in meters
        } else {
            double randomLat = getRandomLatitude(webinarConfig.getMinLat(), webinarConfig.getMaxLat());
            double randomLon = getRandomLongitude(webinarConfig.getMinLon(), webinarConfig.getMaxLon());
            return new MapPoint(randomLat, randomLon);
        }
    }

    private MapPoint randomPointOutside(double bufferMeters) {
        double minLat = webinarConfig.getMinLat();
        double maxLat = webinarConfig.getMaxLat();
        double minLon = webinarConfig.getMinLon();
        double maxLon = webinarConfig.getMaxLon();

        ThreadLocalRandom r = ThreadLocalRandom.current();
        int side = r.nextInt(4); // 0=TOP,1=BOTTOM,2=LEFT,3=RIGHT
        double dMeters = r.nextDouble(1.0, bufferMeters);
        final double metersPerDegLat = 111_320.0; // avg meters per degree latitude

        switch (side) {
            case 0: { // TOP: latitude > maxLat
                double lon = r.nextDouble(minLon, maxLon);
                double dLat = dMeters / metersPerDegLat;
                double lat = maxLat + dLat;
                return new MapPoint(lat, lon);
            }
            case 1: { // BOTTOM: latitude < minLat
                double lon = r.nextDouble(minLon, maxLon);
                double dLat = dMeters / metersPerDegLat;
                double lat = minLat - dLat;
                return new MapPoint(lat, lon);
            }
            case 2: { // LEFT: longitude < minLon
                double lat = r.nextDouble(minLat, maxLat);
                double metersPerDegLon = metersPerDegLonAt(lat);
                double dLon = dMeters / metersPerDegLon;
                double lon = minLon - dLon;
                return new MapPoint(lat, lon);
            }
            default: { // RIGHT: longitude > maxLon
                double lat = r.nextDouble(minLat, maxLat);
                double metersPerDegLon = metersPerDegLonAt(lat);
                double dLon = dMeters / metersPerDegLon;
                double lon = maxLon + dLon;
                return new MapPoint(lat, lon);
            }
        }
    }

    private static double getRandomLatitude(double minLat, double maxLat) {
        return minLat + (maxLat - minLat) * ThreadLocalRandom.current().nextDouble();
    }

    private static double getRandomLongitude(double minLon, double maxLon) {
        return minLon + (maxLon - minLon) * ThreadLocalRandom.current().nextDouble();
    }

    /**
     * Converts degrees of longitude into meters depending on latitude.
     * At the equator, 1° longitude ≈ 111.32 km, decreasing by cos(latitude).
     */
    private static double metersPerDegLonAt(double latDeg) {
        return 111_320.0 * Math.cos(Math.toRadians(latDeg));
    }

    // --- Helpers ------------------------------------------------------------
    private static String optText(JsonNode node, String field) {
        if (node == null || node.isMissingNode()) return null;
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText(null);
    }

    /**
     * Tiny jitter around a point in meters -> lat/lon degrees.
     */
    private static double[] jitterMeters(double lat, double lon, double radiusMeters) {
        // Random bearing and distance (0..radius)
        double bearing = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
        double dist = ThreadLocalRandom.current().nextDouble(0, radiusMeters);

        double dLat = dist / 111_320.0; // meters per deg latitude
        double metersPerDegLon = metersPerDegLonAt(lat);
        double dLon = (metersPerDegLon == 0 ? 0 : dist / metersPerDegLon);

        // Apply bearing (project onto N/E axes)
        double latOffset = dLat * Math.cos(bearing);
        double lonOffset = dLon * Math.sin(bearing);
        return new double[]{lat + latOffset, lon + lonOffset};
    }

    /** Compose address from common fields when display_name is absent. */
    private static String buildAddressFromFields(JsonNode addr) {
        if (addr == null || addr.isMissingNode() || addr.isNull()) return "";
        String[] parts = new String[] {
                text(addr, "house_number"),
                text(addr, "road"),
                text(addr, "neighbourhood"),
                text(addr, "suburb"),
                coalesce(text(addr, "city"), text(addr, "town"), text(addr, "village")),
                text(addr, "state"),
                text(addr, "postcode"),
                text(addr, "country")
        };
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.isBlank()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(p);
            }
        }
        return sb.toString();
    }

    private static String text(JsonNode n, String f) {
        if (n == null || n.isMissingNode()) return null;
        JsonNode v = n.get(f);
        return (v == null || v.isNull()) ? null : v.asText(null);
    }

    private static String coalesce(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
