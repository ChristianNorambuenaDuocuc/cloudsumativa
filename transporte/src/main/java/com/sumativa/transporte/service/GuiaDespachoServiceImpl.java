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
        return GuiaDespachoMapper.toDTO(repository.save(guia));
    }

    @Override
    public GuiaDespachoResponseDTO subirGuiaAS3(Long id) {
        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new GuiaNoEncontradaException(id));

        String nombreArchivo = "guia-" + guia.getNumeroGuia() + ".pdf";
        String urlS3 = s3Service.subirArchivo(nombreArchivo);

        guia.setS3Url(urlS3);
        guia.setEstado("SUBIDA_A_S3");

        return GuiaDespachoMapper.toDTO(repository.save(guia));
    }

    @Override
    public String descargarGuia(Long id, String usuario) {
        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new GuiaNoEncontradaException(id));

        if (!usuario.equals("admin")) {
            throw new SecurityException("No tiene permisos para descargar esta guía");
        }

        return s3Service.descargarArchivo(guia.getS3Url());
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

        return GuiaDespachoMapper.toDTO(repository.save(guia));
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
}
