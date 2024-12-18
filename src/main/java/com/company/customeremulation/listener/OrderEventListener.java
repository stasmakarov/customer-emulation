package com.company.customeremulation.listener;

import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.infoboard.InfoService;
import com.company.customeremulation.rabbit.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private InfoService infoService;

    @EventListener
    public void handleOrderEvent(OrderGeneratedEvent event) {
        OrderDto order = event.getOrderDto();
        messageProducer.sendMessage("orders", order);

        infoService.countOrder(order);
    }
}
