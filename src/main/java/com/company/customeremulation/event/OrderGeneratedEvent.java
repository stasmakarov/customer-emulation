package com.company.customeremulation.event;

import com.company.customeremulation.entity.OrderDto;
import org.springframework.context.ApplicationEvent;

public class OrderGeneratedEvent extends ApplicationEvent {
    private final OrderDto orderDto;

    public OrderGeneratedEvent(Object source, OrderDto orderDto) {
        super(source);
        this.orderDto = orderDto;
    }

    public OrderDto getOrderDto() {
        return orderDto;
    }
}
