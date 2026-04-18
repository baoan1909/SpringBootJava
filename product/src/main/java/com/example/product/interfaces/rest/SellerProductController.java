package com.example.product.interfaces.rest;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.service.ProductApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/products")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
public class SellerProductController {
    private final ProductApplicationService productApplicationService;


    @GetMapping
    public Pagination<ProductResponse> getSellerProducts(
            @ModelAttribute ProductCriteriaCommand command,
            Authentication authentication) {

        String sellerEmail = authentication.getName();
        return productApplicationService.getAllBySellerEmail(sellerEmail, command);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductByIdForSeller(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String sellerEmail = authentication.getName();
        return productApplicationService.getByIdForSeller(id, sellerEmail);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody CreateProductCommand command) {
        return productApplicationService.create(command);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody UpdateProductCommand command) {
        return productApplicationService.update(id, command);
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restore(@PathVariable Long id) {
        productApplicationService.restoreProduct(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBySeller(@PathVariable Long id) {
        productApplicationService.deleteBySeller(id);
    }

    @PatchMapping("/{id}/resubmit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resubmit(@PathVariable Long id) {
        productApplicationService.resubmitProduct(id);
    }
}
