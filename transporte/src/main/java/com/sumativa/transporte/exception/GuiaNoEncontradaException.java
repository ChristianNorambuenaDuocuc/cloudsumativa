package com.sumativa.transporte.exception;


public class GuiaNoEncontradaException extends RuntimeException {

    public GuiaNoEncontradaException(Long id) {
        super("No se encontró la guía con ID: " + id);
    }
}