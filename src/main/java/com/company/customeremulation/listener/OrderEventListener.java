package com.company.customeremulation.listener;

import com.company.customeremulation.entity.CustomerSetting;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.event.OrderProcessedEvent;
import com.company.customeremulation.infoboard.InfoService;
import com.company.customeremulation.rabbit.MessageProducer;
import io.jmix.appsettings.AppSettings;
import io.jmix.flowui.UiEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private InfoService infoService;
    @Autowired
    private UiEventPublisher uiEventPublisher;
    @Autowired
    private AppSettings appSettings;

    @EventListener
    public void handleOrderEvent(OrderGeneratedEvent event) {
        String queueName = appSettings.load(CustomerSetting.class).getQueueName();
        OrderDto order = event.getOrderDto();
        messageProducer.sendMessage(queueName, order);
        infoService.countOrder(order);

        uiEventPublisher.publishEvent(new OrderProcessedEvent(this, order));
    }

}
