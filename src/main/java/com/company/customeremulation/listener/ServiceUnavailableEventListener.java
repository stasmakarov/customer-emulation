package com.company.customeremulation.listener;

import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.event.ServiceUnavailableEvent;
import com.company.customeremulation.event.StoppingOrdersGenerationEvent;
import io.jmix.flowui.UiEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ServiceUnavailableEventListener {

    @Autowired
    private UiEventPublisher uiEventPublisher;

    @EventListener
    public void handleServiceUnavailableEvent(ServiceUnavailableEvent event) {
        String name = event.getName();
        uiEventPublisher.publishEvent(new StoppingOrdersGenerationEvent(this, name));
    }

}
