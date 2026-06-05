package com.sumativa.transporte.repository;


import com.sumativa.transporte.model.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

    List<GuiaDespacho> findByTransportistaAndFecha(String transportista, LocalDate fecha);
}