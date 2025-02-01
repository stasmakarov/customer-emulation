package com.company.customeremulation.rabbit;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQService {
    private final ConnectionFactory connectionFactory;

    public RabbitMQService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public boolean isRabbitMqAvailable() {
        try {
            connectionFactory.createConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
