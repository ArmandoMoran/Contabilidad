package com.contabilidad.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SatMotivoCancelacionRepository extends JpaRepository<SatMotivoCancelacion, UUID> {

    Optional<SatMotivoCancelacion> findByCodeAndActiveTrue(String code);

    List<SatMotivoCancelacion> findAllByActiveTrue();
}
