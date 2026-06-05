package com.sumativa.transporte.service;

import com.sumativa.transporte.dto.GuiaDespachoRequestDTO;
import com.sumativa.transporte.dto.GuiaDespachoResponseDTO;
import com.sumativa.transporte.exception.GuiaNoEncontradaException;
import com.sumativa.transporte.mapper.GuiaDespachoMapper;
import com.sumativa.transporte.model.GuiaDespacho;
import com.sumativa.transporte.repository.GuiaDespachoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GuiaDespachoServiceImpl implements GuiaDespachoService {

    private final GuiaDespachoRepository repository;
    private final S3Service s3Service;

    public GuiaDespachoServiceImpl(GuiaDespachoRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
    }

    @Override
    public GuiaDespachoResponseDTO crearGuia(GuiaDespachoRequestDTO dto) {
        GuiaDespacho guia = GuiaDespachoMapper.toEntity(dto);
        guia.setEstado("CREADA");

        GuiaDespacho guardada = repository.save(guia);
        return GuiaDespachoMapper.toDTO(guardada);
    }

    @Override
    public GuiaDespachoResponseDTO subirGuiaAS3(Long id) {
        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new GuiaNoEncontradaException(id));

        String rutaS3 = generarRutaS3(guia);
        byte[] contenidoPdf = generarPdfSimulado(guia);

        String urlS3 = s3Service.subirArchivo(contenidoPdf, rutaS3);

        guia.setS3Url(urlS3);
        guia.setEstado("SUBIDA_A_S3");

        GuiaDespacho guardada = repository.save(guia);
        return GuiaDespachoMapper.toDTO(guardada);
    }

    @Override
    public String descargarGuia(Long id, String usuario) {
        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new GuiaNoEncontradaException(id));

        if (!"admin".equals(usuario)) {
            throw new SecurityException("No tiene permisos para descargar esta guía");
        }

        return guia.getS3Url();
    }

    @Override
    public GuiaDespachoResponseDTO actualizarGuia(Long id, GuiaDespachoRequestDTO dto) {
        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new GuiaNoEncontradaException(id));

        guia.setNumeroGuia(dto.getNumeroGuia());
        guia.setTransportista(dto.getTransportista());
        guia.setFecha(dto.getFecha());
        guia.setDestinatario(dto.getDestinatario());
        guia.setDireccionDestino(dto.getDireccionDestino());
        guia.setEstado("ACTUALIZADA");

        GuiaDespacho guardada = repository.save(guia);
        return GuiaDespachoMapper.toDTO(guardada);
    }

    @Override
    public void eliminarGuia(Long id) {
        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new GuiaNoEncontradaException(id));

        repository.delete(guia);
    }

    @Override
    public List<GuiaDespachoResponseDTO> buscarPorTransportistaYFecha(String transportista, LocalDate fecha) {
        return repository.findByTransportistaAndFecha(transportista, fecha)
                .stream()
                .map(GuiaDespachoMapper::toDTO)
                .toList();
    }

    private String generarRutaS3(GuiaDespacho guia) {
        String anio = String.valueOf(guia.getFecha().getYear());
        String transportista = guia.getTransportista()
                .replaceAll("\\s+", "_")
                .toLowerCase();

        return anio + "/" + transportista + "/guia-" + guia.getNumeroGuia() + ".pdf";
    }

    private byte[] generarPdfSimulado(GuiaDespacho guia) {
        String contenido = "Guía de despacho\n"
                + "Número: " + guia.getNumeroGuia() + "\n"
                + "Transportista: " + guia.getTransportista() + "\n"
                + "Fecha: " + guia.getFecha() + "\n"
                + "Destinatario: " + guia.getDestinatario() + "\n"
                + "Dirección destino: " + guia.getDireccionDestino() + "\n";

        return contenido.getBytes();
    }
}