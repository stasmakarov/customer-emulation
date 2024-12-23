package com.company.customeremulation.listener;

import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.infoboard.InfoService;
import com.company.customeremulation.rabbit.MessageProducer;
import com.vaadin.flow.component.UI;
import io.jmix.flowui.Notifications;
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
    private Notifications notifications;

    @EventListener
    public void handleOrderEvent(OrderGeneratedEvent event) {
        OrderDto order = event.getOrderDto();
        messageProducer.sendMessage("orders", order);
        infoService.countOrder(order);

        UI currentUI = UI.getCurrent();
        if (currentUI != null) {
            handleOrderEventAsync(event, currentUI);
        }
    }

    @Async
    public void handleOrderEventAsync(OrderGeneratedEvent event, UI ui) {
        ui.access(() -> {
            notifications.create("Order generated").show();
        });
    }
}
