package com.company.customeremulation.rabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue writeQueue() {
        return new Queue("orders");
    }

//    @Bean
//    public Queue readQueue() {
//        return new Queue("completedTasks");
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("completedTasks");

        // Set your message listener here
//        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new MyMessageListener());
//        container.setMessageListener(listenerAdapter);

        return container;
    }
}
