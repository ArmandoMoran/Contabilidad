package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatRegimenFiscalRepository extends JpaRepository<SatRegimenFiscal, UUID> {

    Optional<SatRegimenFiscal> findByCodeAndActiveTrue(String code);

    List<SatRegimenFiscal> findAllByActiveTrue();
}
