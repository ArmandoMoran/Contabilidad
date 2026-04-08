package com.contabilidad.products;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductTaxProfileRepository taxProfileRepository;

    public ProductService(ProductRepository productRepository,
                          ProductTaxProfileRepository taxProfileRepository) {
        this.productRepository = productRepository;
        this.taxProfileRepository = taxProfileRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> listProducts(UUID companyId, String search, Pageable pageable) {
        log.info("Listing products for companyId={}, search={}, page={}", companyId, search, pageable);
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        Page<Product> page = normalizedSearch == null
            ? productRepository.findByCompanyIdAndDeletedAtIsNull(companyId, pageable)
            : productRepository.searchByCompanyId(companyId, normalizedSearch, pageable);
        log.debug("Found {} products (total={})", page.getNumberOfElements(), page.getTotalElements());
        return page;
    }

    @Transactional(readOnly = true)
    public Product getProduct(UUID companyId, UUID productId) {
        log.info("Getting product companyId={}, productId={}", companyId, productId);
        return productRepository.findByCompanyIdAndIdAndDeletedAtIsNull(companyId, productId)
            .orElseThrow(() -> {
                log.warn("Product not found: companyId={}, productId={}", companyId, productId);
                return new EntityNotFoundException("Product not found: " + productId);
            });
    }

    public Product createProduct(UUID companyId, CreateProductRequest request) {
        log.info("Creating product companyId={}, name={}", companyId, request.internalName());
        Product product = ProductMapper.toEntity(companyId, request);
        product = productRepository.save(product);
        log.info("Created product id={}", product.getId());

        if (request.taxProfiles() != null) {
            for (CreateTaxProfileRequest tp : request.taxProfiles()) {
                ProductTaxProfile profile = ProductMapper.toTaxProfileEntity(product.getId(), companyId, tp);
                taxProfileRepository.save(profile);
            }
        }

        return product;
    }

    public Product updateProduct(UUID companyId, UUID productId, UpdateProductRequest request) {
        Product product = getProduct(companyId, productId);

        if (request.internalCode() != null) product.setInternalCode(request.internalCode());
        if (request.internalName() != null) product.setInternalName(request.internalName());
        if (request.description() != null) product.setDescription(request.description());
        if (request.satProductCode() != null) product.setSatProductCode(request.satProductCode());
        if (request.satUnitCode() != null) product.setSatUnitCode(request.satUnitCode());
        if (request.unitPrice() != null) product.setUnitPrice(request.unitPrice());
        if (request.currencyCode() != null) product.setCurrencyCode(request.currencyCode());
        if (request.objetoImpCode() != null) product.setObjetoImpCode(request.objetoImpCode());
        if (request.cuentaPredial() != null) product.setCuentaPredial(request.cuentaPredial());
        if (request.active() != null) product.setActive(request.active());

        return productRepository.save(product);
    }

    public void deleteProduct(UUID companyId, UUID productId, UUID userId) {
        Product product = getProduct(companyId, productId);
        product.softDelete(userId);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductTaxProfile> getTaxProfiles(UUID productId) {
        return taxProfileRepository.findByProductIdAndActiveTrue(productId);
    }
}
