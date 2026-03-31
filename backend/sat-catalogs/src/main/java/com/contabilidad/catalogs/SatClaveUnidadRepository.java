package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatClaveUnidadRepository extends JpaRepository<SatClaveUnidad, UUID> {

    Optional<SatClaveUnidad> findByCodeAndActiveTrue(String code);

    List<SatClaveUnidad> findAllByActiveTrue();
}
