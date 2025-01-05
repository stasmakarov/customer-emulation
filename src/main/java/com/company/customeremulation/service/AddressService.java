package com.company.customeremulation.service;
import com.company.customeremulation.event.ServiceUnavailableEvent;
import com.company.customeremulation.service.record.FeatureCollection;
import com.company.customeremulation.service.record.MapPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Random;

@Service
public class AddressService {
    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse?lat={lat}&lon={lon}&format=geojson&addressdetails=1";
    private static final Random random = new Random();
    private static boolean isNominatimServiceAvailable = true;

    public Optional<String> findAddress(MapPoint point) {
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(NOMINATIM_URL,
                    String.class,
                    point.lat(), point.lon());
            isNominatimServiceAvailable = true;
        } catch (RestClientException e) {
            log.error("Nominatim unavailable");
            if (isNominatimServiceAvailable) {
                isNominatimServiceAvailable = false;
                applicationEventPublisher.publishEvent(new ServiceUnavailableEvent(this, "Nominatim"));
            }
            return Optional.empty();
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Nominatim error: Status code {} - Response body: {}", response.getStatusCode(), response.getBody());
            applicationEventPublisher.publishEvent(new ServiceUnavailableEvent(this, "Nominatim"));
            return Optional.empty();
        }

        String body = response.getBody();
        try {
            FeatureCollection results = objectMapper.readValue(body, FeatureCollection.class);
            if (results.features().length > 0) {
                String house_number = results.features()[0].properties().address().house_number();
                if (house_number != null)
                    return Optional.of(results.features()[0].properties().display_name());
                else
                    return Optional.empty();
            }
        } catch (JsonProcessingException e) {
            log.error("Cannot parse address for coordinates lat: {}, lon: {} - Response body: {}", point.lat(), point.lon(), body);
        }

        return Optional.empty(); // Return empty if no results found or parsing fails
    }


    public MapPoint randomPoint () {
// Define the boundaries for Moscow
        double minLat = 55.4092;
        double maxLat = 55.9021;
        double minLon = 37.3219;
        double maxLon = 37.8925;

        // Generate random coordinates
        double randomLat = getRandomLatitude(minLat, maxLat);
        double randomLon = getRandomLongitude(minLon, maxLon);

        return new MapPoint(randomLat, randomLon);
    }

    private static double getRandomLatitude(double minLat, double maxLat) {
        return minLat + (maxLat - minLat) * random.nextDouble();
    }

    private static double getRandomLongitude(double minLon, double maxLon) {
        return minLon + (maxLon - minLon) * random.nextDouble();
    }
}