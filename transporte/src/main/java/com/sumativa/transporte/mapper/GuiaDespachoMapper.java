package com.sumativa.transporte.mapper;


import com.sumativa.transporte.dto.GuiaDespachoRequestDTO;
import com.sumativa.transporte.dto.GuiaDespachoResponseDTO;
import com.sumativa.transporte.model.GuiaDespacho;

public class GuiaDespachoMapper {

    public static GuiaDespacho toEntity(GuiaDespachoRequestDTO dto) {
        GuiaDespacho guia = new GuiaDespacho();
        guia.setNumeroGuia(dto.getNumeroGuia());
        guia.setTransportista(dto.getTransportista());
        guia.setFecha(dto.getFecha());
        guia.setDestinatario(dto.getDestinatario());
        guia.setDireccionDestino(dto.getDireccionDestino());
        guia.setEstado("CREADA");
        return guia;
    }

    public static GuiaDespachoResponseDTO toDTO(GuiaDespacho guia) {
        GuiaDespachoResponseDTO dto = new GuiaDespachoResponseDTO();
        dto.setId(guia.getId());
        dto.setNumeroGuia(guia.getNumeroGuia());
        dto.setTransportista(guia.getTransportista());
        dto.setFecha(guia.getFecha());
        dto.setDestinatario(guia.getDestinatario());
        dto.setDireccionDestino(guia.getDireccionDestino());
        dto.setEstado(guia.getEstado());
        dto.setS3Url(guia.getS3Url());
        return dto;
    }
}