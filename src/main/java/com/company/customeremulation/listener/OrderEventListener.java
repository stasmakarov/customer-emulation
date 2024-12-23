package com.company.customeremulation.listener;

import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.event.OrderProcessedEvent;
import com.company.customeremulation.infoboard.InfoService;
import com.company.customeremulation.rabbit.MessageProducer;
import com.vaadin.flow.component.UI;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.backgroundtask.UIAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private InfoService infoService;
    @Autowired
    private UiEventPublisher uiEventPublisher;
    @EventListener
    public void handleOrderEvent(OrderGeneratedEvent event) {
        OrderDto order = event.getOrderDto();
        messageProducer.sendMessage("orders", order);
        infoService.countOrder(order);

        uiEventPublisher.publishEvent(new OrderProcessedEvent(this, order));
    }


}
