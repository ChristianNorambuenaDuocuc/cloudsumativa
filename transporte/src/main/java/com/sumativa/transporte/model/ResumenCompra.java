package com.sumativa.transporte.model;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESUMEN_COMPRA")
public class ResumenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resumen_compra_seq")
    @SequenceGenerator(
            name = "resumen_compra_seq",
            sequenceName = "RESUMEN_COMPRA_SEQ",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "GUIA_ID")
    private Long guiaId;

    @Column(name = "NUMERO_GUIA")
    private String numeroGuia;

    @Column(name = "TRANSPORTISTA")
    private String transportista;

    @Column(name = "FECHA")
    private LocalDate fecha;

    @Column(name = "DESTINATARIO")
    private String destinatario;

    @Column(name = "DIRECCION_DESTINO")
    private String direccionDestino;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "FECHA_RESUMEN")
    private LocalDateTime fechaResumen;

    public ResumenCompra() {
    }

    public Long getId() {
        return id;
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