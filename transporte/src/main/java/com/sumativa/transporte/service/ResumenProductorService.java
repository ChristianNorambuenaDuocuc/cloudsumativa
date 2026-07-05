package com.sumativa.transporte.service;


import com.sumativa.transporte.config.RabbitMQConfig;
import com.sumativa.transporte.dto.ResumenInscripcionDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResumenProductorService {

    private final RabbitTemplate rabbitTemplate;

    public ResumenProductorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarResumen(ResumenInscripcionDTO resumen) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_RESUMEN,
                RabbitMQConfig.ROUTING_KEY_RESUMEN,
                resumen
        );

        System.out.println("Resumen enviado a RabbitMQ para guía ID: " + resumen.getGuiaId());
    }
}