package com.company.customeremulation.rabbit;

import com.company.customeremulation.entity.OrderDto;
import io.jmix.core.EntitySerialization;
import io.jmix.core.EntitySerializationOption;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
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
    @Autowired
    private EntitySerialization entitySerialization;
    @Autowired
    private FetchPlans fetchPlans;

    public void sendMessage(String queue, OrderDto order) {
        FetchPlan fetchPlan = fetchPlans.builder(OrderDto.class)
                .addFetchPlan(FetchPlan.BASE)
                .add("itemDto")
                .build();
        String json = entitySerialization.toJson(order,
                fetchPlan,
                EntitySerializationOption.IGNORE_ENTITY_NAME,
                EntitySerializationOption.PRETTY_PRINT,
                EntitySerializationOption.DO_NOT_SERIALIZE_DENIED_PROPERTY);
        rabbitTemplate.convertAndSend(queue, json);
        log.info("Order JSON: {}", json);
    }
}
