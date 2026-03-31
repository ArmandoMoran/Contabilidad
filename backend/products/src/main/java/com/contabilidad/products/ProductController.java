package com.contabilidad.products;

import com.contabilidad.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductDto> list(
            @RequestHeader("X-Company-Id") UUID companyId,
            Pageable pageable) {
        Page<Product> page = productService.listProducts(companyId, pageable);
        return PageResponse.of(
            page.getContent().stream().map(p -> ProductMapper.toDto(p, productService.getTaxProfiles(p.getId()))).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(companyId, request);
        List<ProductTaxProfile> profiles = productService.getTaxProfiles(product.getId());
        return ProductMapper.toDto(product, profiles);
    }

    @GetMapping("/{id}")
    public ProductDto get(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        Product product = productService.getProduct(companyId, id);
        List<ProductTaxProfile> profiles = productService.getTaxProfiles(product.getId());
        return ProductMapper.toDto(product, profiles);
    }

    @PatchMapping("/{id}")
    public ProductDto update(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        Product product = productService.updateProduct(companyId, id, request);
        List<ProductTaxProfile> profiles = productService.getTaxProfiles(product.getId());
        return ProductMapper.toDto(product, profiles);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        productService.deleteProduct(companyId, id, userId);
    }
}
