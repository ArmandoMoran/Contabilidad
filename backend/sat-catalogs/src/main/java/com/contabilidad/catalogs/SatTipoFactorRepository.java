package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatTipoFactorRepository extends JpaRepository<SatTipoFactor, UUID> {

    Optional<SatTipoFactor> findByCodeAndActiveTrue(String code);

    List<SatTipoFactor> findAllByActiveTrue();
}
