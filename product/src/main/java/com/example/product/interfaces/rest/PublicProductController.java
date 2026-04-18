package com.example.product.interfaces.rest;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.service.ProductApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class PublicProductController {
    private final ProductApplicationService productApplicationService;

    @GetMapping
    public Pagination<ProductResponse> getAllProducts(
            @ModelAttribute ProductCriteriaCommand command) {
        return productApplicationService.getAllForUsers(command);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productApplicationService.getByIdForUsers(id);

    }
}
