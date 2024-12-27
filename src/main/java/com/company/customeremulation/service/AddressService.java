package com.company.customeremulation.service;
import com.company.customeremulation.event.ServiceUnavailableEvent;
import com.company.customeremulation.service.record.FeatureCollection;
import com.company.customeremulation.service.record.NominatimReverseResponse;
import com.company.customeremulation.service.record.MapPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import static java.lang.Integer.parseInt;

@Service
public class AddressService {
    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public String findAddress(MapPoint point) {
        String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse?lat={lat}&lon={lon}&format=geojson&addressdetails=1";

        ResponseEntity<String> response = restTemplate.getForEntity(NOMINATIM_URL,
                String.class,
                point.lat(), point.lon());
        ObjectMapper objectMapper = new ObjectMapper();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String body = response.getBody();
            FeatureCollection results;
            try {
                results = objectMapper.readValue(body, FeatureCollection.class);
            } catch (JsonProcessingException e) {
                log.error("Cannot parse address: {}", body);
                return null;
            }
            if (results != null) {
                String house_number = results.features()[0].properties().address().house_number();
                try {
                    int parsed = parseInt(house_number);
                    return results.features()[0].properties().display_name();
                } catch (NumberFormatException ignored) {}
            }
        }
        log.error("Nominatim error");
        applicationEventPublisher.publishEvent(new ServiceUnavailableEvent(this,
                "Nominatim"));
        return null;
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
        Random random = new Random();
        return minLat + (maxLat - minLat) * random.nextDouble();
    }

    private static double getRandomLongitude(double minLon, double maxLon) {
        Random random = new Random();
        return minLon + (maxLon - minLon) * random.nextDouble();
    }
}






