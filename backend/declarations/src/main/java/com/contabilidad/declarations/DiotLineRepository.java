package com.contabilidad.declarations;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DiotLineRepository extends JpaRepository<DiotLine, UUID> {

    void deleteByDeclarationRunId(UUID declarationRunId);
}
