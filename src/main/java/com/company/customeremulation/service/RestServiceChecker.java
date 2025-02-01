package com.company.customeremulation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestServiceChecker {

    @Value("${ordersapp.baseUrl}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public RestServiceChecker(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isServiceAvailable() {
        try {
            restTemplate.getForObject(baseUrl, String.class);
            return true;
        } catch (ResourceAccessException e) {
            return false;
        }
    }
}
