package com.company.customeremulation.event;

import org.springframework.context.ApplicationEvent;

public class StoppingOrdersGenerationEvent extends ApplicationEvent {
    private final String name;
    public StoppingOrdersGenerationEvent(Object source, String name) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
