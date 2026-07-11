package com.sumativa.transporte.config;


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

    // ====================================================
    // CONFIGURACIÓN DE GUÍAS DE DESPACHO
    // ====================================================

    public static final String QUEUE_GUIAS =
            "guias.despacho.queue";

    public static final String EXCHANGE_GUIAS =
            "guias.despacho.exchange";

    public static final String ROUTING_KEY_GUIAS =
            "guias.despacho.key";

    // ====================================================
    // CONFIGURACIÓN DE COLA DE ERRORES
    // ====================================================

    public static final String QUEUE_GUIAS_ERROR =
            "guias.despacho.error.queue";

    public static final String EXCHANGE_GUIAS_ERROR =
            "guias.despacho.error.exchange";

    public static final String ROUTING_KEY_GUIAS_ERROR =
            "guias.despacho.error.key";

    // ====================================================
    // CONFIGURACIÓN DEL RESUMEN
    // ====================================================

    public static final String QUEUE_RESUMEN =
            "resumen.inscripcion.queue";

    public static final String EXCHANGE_RESUMEN =
            "resumen.inscripcion.exchange";

    public static final String ROUTING_KEY_RESUMEN =
            "resumen.inscripcion.key";

    // ====================================================
    // COLA PRINCIPAL DE GUÍAS
    // ====================================================

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

    // ====================================================
    // COLA DE ERRORES
    // ====================================================

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

    // ====================================================
    // COLA DE RESUMEN
    // ====================================================

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
    public Binding resumenBinding(
            Queue resumenQueue,
            DirectExchange resumenExchange
    ) {
        return BindingBuilder
                .bind(resumenQueue)
                .to(resumenExchange)
                .with(ROUTING_KEY_RESUMEN);
    }

    // ====================================================
    // CONVERSIÓN JAVA ↔ JSON
    // ====================================================

    @Bean
    public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter(
            "com.sumativa.transporte.dto",
            "com.sumativa.transporte.model"
    );
    }

    // ====================================================
    // CONFIGURACIÓN DEL PRODUCTOR
    // ====================================================

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

    // ====================================================
    // RECUPERACIÓN DE MENSAJES CON ERROR
    // ====================================================

    @Bean
    public RepublishMessageRecoverer republishMessageRecoverer(
            RabbitTemplate rabbitTemplate
    ) {
        /*
         * Después de agotar los reintentos,
         * el mensaje se envía al exchange de errores.
         */
        return new RepublishMessageRecoverer(
                rabbitTemplate,
                EXCHANGE_GUIAS_ERROR,
                ROUTING_KEY_GUIAS_ERROR
        );
    }

    // ====================================================
    // REINTENTOS
    // ====================================================

  @Bean
public RetryOperationsInterceptor retryInterceptor(
        RepublishMessageRecoverer recoverer
) {
    return RetryInterceptorBuilder
            .stateless()
            .maxRetries(2)
            .backOffOptions(
                    1000,
                    2.0,
                    5000
            )
            .recoverer(recoverer)
            .build();
}

    // ====================================================
    // CONFIGURACIÓN DEL CONSUMIDOR
    // ====================================================

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

        factory.setMessageConverter(
                jsonMessageConverter
        );

        /*
         * Aplica hasta tres intentos cuando
         * el consumidor genera un error.
         */
        factory.setAdviceChain(
                retryInterceptor
        );

        /*
         * Evita que el mensaje vuelva infinitamente
         * a la cola principal.
         */
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}