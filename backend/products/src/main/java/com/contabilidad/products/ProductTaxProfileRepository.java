package com.contabilidad.products;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProductTaxProfileRepository extends JpaRepository<ProductTaxProfile, UUID> {

    List<ProductTaxProfile> findByProductIdAndActiveTrue(UUID productId);
}
