package com.example.product.application.service;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.dto.response.ProductResponse;

public interface ProductApplicationService {
    ProductResponse create(CreateProductCommand command);
    ProductResponse update(Long id, UpdateProductCommand command);
    void deleteBySeller(Long id);
    void resubmitProduct(Long id);
    void restoreProduct(Long id);

    void approveProduct(Long id);
    void rejectProduct(Long id, String reason);
    void freezeProduct(Long id, String reason);
    void unfreezeProduct(Long id, String reason);

    ProductResponse getById(Long id);
    Pagination<ProductResponse> getAll(ProductCriteriaCommand command);
}
