package com.sumativa.transporte.controller;

import com.sumativa.transporte.model.ResumenCompra;
import com.sumativa.transporte.repository.ResumenCompraRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumenes")
public class ResumenCompraController {

    private final ResumenCompraRepository resumenCompraRepository;

    public ResumenCompraController(ResumenCompraRepository resumenCompraRepository) {
        this.resumenCompraRepository = resumenCompraRepository;
    }

    @GetMapping
    public List<ResumenCompra> listarResumenes() {
        return resumenCompraRepository.findAll();
    }
}
