package com.sumativa.transporte.dto;


import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespachoRequestDTO {

    private String numeroGuia;
    private String transportista;
    private LocalDate fecha;
    private String destinatario;
    private String direccionDestino;

    // getters y setters
}