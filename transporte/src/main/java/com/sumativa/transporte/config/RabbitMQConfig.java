package com.sumativa.transporte.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_RESUMEN = "resumen.inscripcion.queue";
    public static final String EXCHANGE_RESUMEN = "resumen.inscripcion.exchange";
    public static final String ROUTING_KEY_RESUMEN = "resumen.inscripcion.key";

    @Bean
    public Queue resumenQueue() {
        return QueueBuilder
                .durable(QUEUE_RESUMEN)
                .build();
    }

    @Bean
    public DirectExchange resumenExchange() {
        return new DirectExchange(EXCHANGE_RESUMEN);
    }

    @Bean
    public Binding resumenBinding(Queue resumenQueue, DirectExchange resumenExchange) {
        return BindingBuilder
                .bind(resumenQueue)
                .to(resumenExchange)
                .with(ROUTING_KEY_RESUMEN);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}