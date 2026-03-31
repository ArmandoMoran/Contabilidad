package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatClaveProdServRepository extends JpaRepository<SatClaveProdServ, UUID> {

    Optional<SatClaveProdServ> findByCodeAndActiveTrue(String code);

    List<SatClaveProdServ> findAllByActiveTrue();
}
