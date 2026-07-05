package com.sumativa.transporte.service;

import com.sumativa.transporte.config.RabbitMQConfig;
import com.sumativa.transporte.dto.ResumenInscripcionDTO;
import com.sumativa.transporte.model.ResumenCompra;
import com.sumativa.transporte.repository.ResumenCompraRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ResumenConsumidorService {

    private final ResumenCompraRepository resumenCompraRepository;

    public ResumenConsumidorService(ResumenCompraRepository resumenCompraRepository) {
        this.resumenCompraRepository = resumenCompraRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RESUMEN)
    public void consumirResumen(ResumenInscripcionDTO resumenDTO) {

        ResumenCompra resumenCompra = new ResumenCompra();
        resumenCompra.setGuiaId(resumenDTO.getGuiaId());
        resumenCompra.setNumeroGuia(resumenDTO.getNumeroGuia());
        resumenCompra.setTransportista(resumenDTO.getTransportista());
        resumenCompra.setFecha(resumenDTO.getFecha());
        resumenCompra.setDestinatario(resumenDTO.getDestinatario());
        resumenCompra.setDireccionDestino(resumenDTO.getDireccionDestino());
        resumenCompra.setEstado(resumenDTO.getEstado());
        resumenCompra.setFechaResumen(resumenDTO.getFechaResumen());

        resumenCompraRepository.save(resumenCompra);

        System.out.println("Resumen guardado en Oracle para guía ID: " + resumenDTO.getGuiaId());
    }
}
