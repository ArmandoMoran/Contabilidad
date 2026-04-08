package com.contabilidad.products;

import com.contabilidad.shared.PageResponse;
import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductDto> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        var companyId = SecurityContextUtils.currentCompanyId();
        Page<Product> page = productService.listProducts(companyId, search, pageable);
        return PageResponse.of(
            page.getContent().stream().map(p -> ProductMapper.toDto(p, productService.getTaxProfiles(p.getId()))).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(SecurityContextUtils.currentCompanyId(), request);
        List<ProductTaxProfile> profiles = productService.getTaxProfiles(product.getId());
        return ProductMapper.toDto(product, profiles);
    }

    @GetMapping("/{id}")
    public ProductDto get(@PathVariable java.util.UUID id) {
        Product product = productService.getProduct(SecurityContextUtils.currentCompanyId(), id);
        List<ProductTaxProfile> profiles = productService.getTaxProfiles(product.getId());
        return ProductMapper.toDto(product, profiles);
    }

    @PatchMapping("/{id}")
    public ProductDto update(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        Product product = productService.updateProduct(SecurityContextUtils.currentCompanyId(), id, request);
        List<ProductTaxProfile> profiles = productService.getTaxProfiles(product.getId());
        return ProductMapper.toDto(product, profiles);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable java.util.UUID id) {
        productService.deleteProduct(SecurityContextUtils.currentCompanyId(), id, SecurityContextUtils.currentUserId());
    }
}
