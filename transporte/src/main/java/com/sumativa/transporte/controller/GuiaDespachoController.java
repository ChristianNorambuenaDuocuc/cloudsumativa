package com.sumativa.transporte.controller;

import com.sumativa.transporte.dto.GuiaDespachoRequestDTO;
import com.sumativa.transporte.dto.GuiaDespachoResponseDTO;
import com.sumativa.transporte.service.GuiaDespachoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/guias")
public class GuiaDespachoController {

    private final GuiaDespachoService service;

    public GuiaDespachoController(GuiaDespachoService service) {
        this.service = service;
    }

    @PostMapping
    public GuiaDespachoResponseDTO crearGuia(@RequestBody GuiaDespachoRequestDTO dto) {
        return service.crearGuia(dto);
    }

    @PostMapping("/{id}/upload-s3")
    public GuiaDespachoResponseDTO subirGuiaAS3(@PathVariable Long id) {
        return service.subirGuiaAS3(id);
    }

    @GetMapping("/{id}/download")
    public String descargarGuia(
            @PathVariable Long id,
            @RequestParam String usuario
    ) {
        return service.descargarGuia(id, usuario);
    }

    @PutMapping("/{id}")
    public GuiaDespachoResponseDTO actualizarGuia(
            @PathVariable Long id,
            @RequestBody GuiaDespachoRequestDTO dto
    ) {
        return service.actualizarGuia(id, dto);
    }

    @DeleteMapping("/{id}")
    public String eliminarGuia(@PathVariable Long id) {
        service.eliminarGuia(id);
        return "Guía eliminada correctamente";
    }

    @GetMapping("/buscar")
    public List<GuiaDespachoResponseDTO> buscarPorTransportistaYFecha(
            @RequestParam String transportista,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return service.buscarPorTransportistaYFecha(transportista, fecha);
    }
}