package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatMonedaRepository extends JpaRepository<SatMoneda, UUID> {

    Optional<SatMoneda> findByCodeAndActiveTrue(String code);

    List<SatMoneda> findAllByActiveTrue();
}
