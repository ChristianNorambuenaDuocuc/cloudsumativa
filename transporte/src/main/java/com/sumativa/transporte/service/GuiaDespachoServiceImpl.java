package com.sumativa.transporte.service;

import com.sumativa.transporte.dto.GuiaDespachoRequestDTO;
import com.sumativa.transporte.dto.GuiaDespachoResponseDTO;
import com.sumativa.transporte.exception.GuiaNoEncontradaException;
import com.sumativa.transporte.mapper.GuiaDespachoMapper;
import com.sumativa.transporte.model.GuiaDespacho;
import com.sumativa.transporte.repository.GuiaDespachoRepository;
import java.nio.file.Path;


import org.springframework.beans.factory.annotation.Value;
import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Service
public class GuiaDespachoServiceImpl implements GuiaDespachoService {

@Value("${efs.path}")
private String efsPath;

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

Path rutaEfs = Paths.get(efsPath, "guia-" + guia.getNumeroGuia() + ".pdf");
try {
    Files.write(rutaEfs, contenidoPdf);
} catch (IOException e) {
    throw new RuntimeException("Error al guardar la guía temporalmente en EFS", e);
}

String urlS3 = s3Service.subirArchivo(rutaEfs.toFile(), rutaS3);

        guia.setS3Url(urlS3);
        guia.setEstado("SUBIDA_A_S3");

        GuiaDespacho guardada = repository.save(guia);
        return GuiaDespachoMapper.toDTO(guardada);
    }

    @Override
public byte[] descargarGuia(Long id, String usuario) {

    GuiaDespacho guia = repository.findById(id)
            .orElseThrow(() -> new GuiaNoEncontradaException(id));

    if (!usuario.equals("admin")) {
        throw new SecurityException(
                "No tiene permisos para descargar esta guía");
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

    GuiaDespacho guardada = repository.save(guia);

    byte[] contenidoPdf = generarPdfSimulado(guardada);

    Path rutaEfs = Paths.get(
            efsPath,
            "guia-" + guardada.getNumeroGuia() + ".pdf"
    );

    try {
        Files.write(rutaEfs, contenidoPdf);
    } catch (IOException e) {
        throw new RuntimeException("Error al actualizar PDF en EFS", e);
    }

    String rutaS3 = generarRutaS3(guardada);
    String urlS3 = s3Service.subirArchivo(rutaEfs.toFile(), rutaS3);

    guardada.setS3Url(urlS3);
    guardada.setEstado("ACTUALIZADA_Y_SUBIDA_A_S3");

    return GuiaDespachoMapper.toDTO(repository.save(guardada));
}

   @Override
public void eliminarGuia(Long id) {
    GuiaDespacho guia = repository.findById(id)
            .orElseThrow(() -> new GuiaNoEncontradaException(id));

    if (guia.getS3Url() != null) {
        s3Service.eliminarArchivo(guia.getS3Url());
    }

    Path rutaEfs = Paths.get(efsPath, "guia-" + guia.getNumeroGuia() + ".pdf");

    try {
        Files.deleteIfExists(rutaEfs);
    } catch (IOException e) {
        throw new RuntimeException("Error al eliminar la guía temporal desde EFS", e);
    }

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
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         PDDocument document = new PDDocument()) {

        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.setLeading(16);
            contentStream.newLineAtOffset(50, 750);

            contentStream.showText("Guía de despacho");
            contentStream.newLine();
            contentStream.showText("Número: " + guia.getNumeroGuia());
            contentStream.newLine();
            contentStream.showText("Transportista: " + guia.getTransportista());
            contentStream.newLine();
            contentStream.showText("Fecha: " + guia.getFecha());
            contentStream.newLine();
            contentStream.showText("Destinatario: " + guia.getDestinatario());
            contentStream.newLine();
            contentStream.showText("Dirección destino: " + guia.getDireccionDestino());

            contentStream.endText();
        }

        document.save(outputStream);
        return outputStream.toByteArray();

    } catch (IOException e) {
        throw new RuntimeException("Error al generar PDF", e);
    }
}
}