package com.sumativa.transporte.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ResumenInscripcionDTO {

    private Long guiaId;
    private String numeroGuia;
    private String transportista;
    private LocalDate fecha;
    private String destinatario;
    private String direccionDestino;
    private String estado;
    private LocalDateTime fechaResumen;

    public ResumenInscripcionDTO() {
    }

    public Long getGuiaId() {
        return guiaId;
    }

    public void setGuiaId(Long guiaId) {
        this.guiaId = guiaId;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public void setNumeroGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaResumen() {
        return fechaResumen;
    }

    public void setFechaResumen(LocalDateTime fechaResumen) {
        this.fechaResumen = fechaResumen;
    }
}