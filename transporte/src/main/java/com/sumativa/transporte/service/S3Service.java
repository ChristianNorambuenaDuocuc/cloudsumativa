package com.sumativa.transporte.service;


import org.springframework.stereotype.Service;

@Service
public class S3Service {

    public String subirArchivo(String nombreArchivo) {
        return "https://s3.amazonaws.com/cursos12/" + nombreArchivo;
    }

    public String descargarArchivo(String s3Url) {
        return "Descargando archivo desde: " + s3Url;
    }
}