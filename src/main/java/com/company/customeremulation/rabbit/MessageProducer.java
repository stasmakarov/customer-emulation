package com.company.customeremulation.rabbit;

import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.service.record.OrderRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String queue, OrderDto order) {
        String json = serialize(order);
        rabbitTemplate.convertAndSend(queue, json);
        log.info("Order JSON: {}", json);
    }


    private String serialize(OrderDto order) {
        OrderRecord orderRecord = new OrderRecord(order.getCustomer(),
                order.getAddress(),
                order.getItemDto().getName(),
                order.getQuantity());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(orderRecord);
        } catch (JsonProcessingException e) {
            //noinspection JmixRuntimeException
            throw new RuntimeException(e);
        }
    }
}
