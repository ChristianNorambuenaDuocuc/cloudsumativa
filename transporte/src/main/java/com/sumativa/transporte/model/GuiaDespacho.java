package com.sumativa.transporte.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroGuia;
    private String transportista;
    private LocalDate fecha;
    private String destinatario;
    private String direccionDestino;
    private String estado;
    private String s3Url;

    public GuiaDespacho() {}

    // getters y setters
}
