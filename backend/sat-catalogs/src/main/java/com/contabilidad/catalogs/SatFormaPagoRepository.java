package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatFormaPagoRepository extends JpaRepository<SatFormaPago, UUID> {

    Optional<SatFormaPago> findByCodeAndActiveTrue(String code);

    List<SatFormaPago> findAllByActiveTrue();
}
