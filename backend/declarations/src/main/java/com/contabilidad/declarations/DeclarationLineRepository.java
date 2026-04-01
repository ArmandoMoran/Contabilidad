package com.contabilidad.declarations;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DeclarationLineRepository extends JpaRepository<DeclarationLine, UUID> {

    void deleteByDeclarationRunId(UUID declarationRunId);
}
