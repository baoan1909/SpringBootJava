package com.example.product.interfaces.rest;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.CreateVariantsCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.service.ProductApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductApplicationService productApplicationService;

    public ProductController(ProductApplicationService productApplicationService) {
        this.productApplicationService = productApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody CreateProductCommand command) {
        return productApplicationService.create(command);
    }

    @PostMapping("/{productId}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createVariant(@PathVariable Long productId, @RequestBody CreateVariantsCommand command) {
        return productApplicationService.createVariantToExistingProduct(productId, command);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody UpdateProductCommand command) {
        return productApplicationService.update(id, command);
    }

    @GetMapping
    public Pagination<ProductResponse> findAll(ProductCriteriaCommand command) {
        return productApplicationService.getAll(command);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productApplicationService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productApplicationService.delete(id);
    }

    @DeleteMapping("/{productId}/variants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVariant(
            @PathVariable Long productId,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String size
    ){
        productApplicationService.deleteProductVariant(productId, color, size);
    }

}
