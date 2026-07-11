package com.sumativa.transporte.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitMQConfig {

    /*
     * COLA PRINCIPAL
     * Aquí llegan las guías enviadas por el productor.
     */
    public static final String QUEUE_GUIAS =
            "guias.despacho.queue";

    public static final String EXCHANGE_GUIAS =
            "guias.despacho.exchange";

    public static final String ROUTING_KEY_GUIAS =
            "guias.despacho.key";

    /*
     * COLA DE ERRORES
     * Aquí llegan las guías que no pudieron procesarse
     * después de 3 intentos.
     */
    public static final String QUEUE_GUIAS_ERROR =
            "guias.despacho.error.queue";

    public static final String EXCHANGE_GUIAS_ERROR =
            "guias.despacho.error.exchange";

    public static final String ROUTING_KEY_GUIAS_ERROR =
            "guias.despacho.error.key";

    // ----------------------------------------------------
    // Cola principal
    // ----------------------------------------------------

    @Bean
    public Queue guiasQueue() {
        return QueueBuilder
                .durable(QUEUE_GUIAS)
                .build();
    }

    @Bean
    public DirectExchange guiasExchange() {
        return new DirectExchange(EXCHANGE_GUIAS);
    }

    @Bean
    public Binding guiasBinding(
            Queue guiasQueue,
            DirectExchange guiasExchange
    ) {
        return BindingBuilder
                .bind(guiasQueue)
                .to(guiasExchange)
                .with(ROUTING_KEY_GUIAS);
    }

    // ----------------------------------------------------
    // Cola de errores
    // ----------------------------------------------------

    @Bean
    public Queue guiasErrorQueue() {
        return QueueBuilder
                .durable(QUEUE_GUIAS_ERROR)
                .build();
    }

    @Bean
    public DirectExchange guiasErrorExchange() {
        return new DirectExchange(EXCHANGE_GUIAS_ERROR);
    }

    @Bean
    public Binding guiasErrorBinding(
            Queue guiasErrorQueue,
            DirectExchange guiasErrorExchange
    ) {
        return BindingBuilder
                .bind(guiasErrorQueue)
                .to(guiasErrorExchange)
                .with(ROUTING_KEY_GUIAS_ERROR);
    }

    // ----------------------------------------------------
    // Conversión Java ↔ JSON
    // ----------------------------------------------------

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return new JacksonJsonMessageConverter(objectMapper);
    }

    // ----------------------------------------------------
    // Productor
    // ----------------------------------------------------

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(
                jsonMessageConverter
        );

        return rabbitTemplate;
    }

    // ----------------------------------------------------
    // Recuperación después de errores
    // ----------------------------------------------------

    @Bean
    public RepublishMessageRecoverer republishMessageRecoverer(
            RabbitTemplate rabbitTemplate
    ) {
        return new RepublishMessageRecoverer(
                rabbitTemplate,
                EXCHANGE_GUIAS_ERROR,
                ROUTING_KEY_GUIAS_ERROR
        );
    }

    // ----------------------------------------------------
    // Tres intentos antes de enviar a errores
    // ----------------------------------------------------

    @Bean
    public RetryOperationsInterceptor retryInterceptor(
            RepublishMessageRecoverer recoverer
    ) {
        return RetryInterceptorBuilder
                .stateless()
                .maxAttempts(3)
                .backOffOptions(
                        1000,
                        2.0,
                        5000
                )
                .recoverer(recoverer)
                .build();
    }

    // ----------------------------------------------------
    // Configuración del consumidor @RabbitListener
    // ----------------------------------------------------

    @Bean
    public SimpleRabbitListenerContainerFactory
    rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter,
            RetryOperationsInterceptor retryInterceptor
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);

        /*
         * Aplica los tres reintentos.
         */
        factory.setAdviceChain(retryInterceptor);

        /*
         * Evita que el mensaje vuelva infinitamente
         * a la cola principal.
         */
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}