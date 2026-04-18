package com.example.product.interfaces.rest;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.command.ReasonCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.service.ProductApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductApplicationService productApplicationService;


    @GetMapping
    public Pagination<ProductResponse> findAll(ProductCriteriaCommand command) {
        return productApplicationService.getAll(command);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productApplicationService.getById(id);
    }

    @PatchMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(@PathVariable Long id) {
        productApplicationService.approveProduct(id);
    }

    @PatchMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable Long id, @RequestBody ReasonCommand command) {
        productApplicationService.rejectProduct(id, command.reason());
    }

    @PatchMapping("/{id}/freeze")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void freeze(@PathVariable Long id, @RequestBody ReasonCommand command) {
        productApplicationService.freezeProduct(id, command.reason());
    }

    @PatchMapping("/{id}/unfreeze")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfreeze(@PathVariable Long id, @RequestBody ReasonCommand command) {
        productApplicationService.unfreezeProduct(id, command.reason());
    }
}
