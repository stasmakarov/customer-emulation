package com.company.customeremulation.listener;

import io.jmix.restds.exception.RestDataStoreAccessException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RestDataStoreAccessExceptionListener {

    @EventListener
    public void catchException(RestDataStoreAccessException exception) {
        System.out.println("REST DS ERROR");
    }
}
