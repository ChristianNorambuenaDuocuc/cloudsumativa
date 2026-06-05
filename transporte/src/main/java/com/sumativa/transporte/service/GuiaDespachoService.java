package com.sumativa.transporte.service;

import com.sumativa.transporte.dto.GuiaDespachoRequestDTO;
import com.sumativa.transporte.dto.GuiaDespachoResponseDTO;
import com.sumativa.transporte.model.GuiaDespacho;

import java.time.LocalDate;
import java.util.List;

public interface GuiaDespachoService {

    GuiaDespachoResponseDTO crearGuia(GuiaDespachoRequestDTO dto);

    GuiaDespachoResponseDTO subirGuiaAS3(Long id);

    String descargarGuia(Long id, String usuario);

    GuiaDespachoResponseDTO actualizarGuia(Long id, GuiaDespachoRequestDTO dto);

    void eliminarGuia(Long id);

    List<GuiaDespachoResponseDTO> buscarPorTransportistaYFecha(String transportista, LocalDate fecha);

private String generarRutaS3(GuiaDespacho guia) {
    String anio = String.valueOf(guia.getFecha().getYear());
    String transportista = guia.getTransportista().replaceAll("\\s+", "_").toLowerCase();

    return anio + "/" + transportista + "/guia-" + guia.getNumeroGuia() + ".pdf";
}


}