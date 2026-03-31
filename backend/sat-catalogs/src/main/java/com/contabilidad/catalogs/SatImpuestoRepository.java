package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatImpuestoRepository extends JpaRepository<SatImpuesto, UUID> {

    Optional<SatImpuesto> findByCodeAndActiveTrue(String code);

    List<SatImpuesto> findAllByActiveTrue();
}
