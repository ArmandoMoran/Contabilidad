package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatMetodoPagoRepository extends JpaRepository<SatMetodoPago, UUID> {

    Optional<SatMetodoPago> findByCodeAndActiveTrue(String code);

    List<SatMetodoPago> findAllByActiveTrue();
}
