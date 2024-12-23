package com.company.customeremulation.event;

import com.company.customeremulation.entity.OrderDto;
import org.springframework.context.ApplicationEvent;

public class OrderProcessedEvent extends ApplicationEvent {
    private final OrderDto order;
    public OrderProcessedEvent(Object source, OrderDto order) {
        super(source);
        this.order = order;
    }

    public OrderDto getOrder() {
        return order;
    }
}
