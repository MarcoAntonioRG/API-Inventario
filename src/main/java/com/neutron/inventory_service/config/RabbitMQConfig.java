package com.neutron.inventory_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue stockLowQueue() {
        return new Queue("stock.low.queue", false);
    }

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange("stockExchange");
    }

    @Bean
    public Binding binding(Queue stockLowQueue, TopicExchange stockExchange) {
        return BindingBuilder.bind(stockLowQueue).to(stockExchange).with("stock.low");
    }
}
