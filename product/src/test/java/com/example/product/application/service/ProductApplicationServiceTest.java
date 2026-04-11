package com.example.product.application.service;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.CreateVariantsCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.mapper.ProductDtoMapper;
import com.example.product.domain.exception.ProductNotFoundException;
import com.example.product.domain.model.Product;
import com.example.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @InjectMocks
    private ProductApplicationService productApplicationService;

    private Product mockProduct;
    private ProductResponse mockProductResponse;
    private final Long productId = 1L;


    @BeforeEach
    void setUp() {
        mockProduct =  mock(Product.class);
        mockProductResponse = mock(ProductResponse.class);
    }

    @Test
    @DisplayName("Create Product - Success khi SKU chưa tồn tại")
    void create_Success() {
        CreateProductCommand command = mock(CreateProductCommand.class);
        when(command.sku()).thenReturn("SKU-123");
        when(command.name()).thenReturn("Áo thun");
        when(command.variants()).thenReturn(Collections.emptyList());
        when(command.price()).thenReturn(BigDecimal.valueOf(150000.0));

        when(productRepository.findBySku("SKU-123")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);
        when(productDtoMapper.toResponse(mockProduct)).thenReturn(mockProductResponse);

        ProductResponse result = productApplicationService.create(command);

        assertNotNull(result);
        assertEquals(mockProductResponse, result);
        verify(productRepository).findBySku("SKU-123");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Create Product - Throws IllegalArgumentException khi SKU đã tồn tại")
    void create_ThrowsException_WhenSkuExists() {
        CreateProductCommand command = mock(CreateProductCommand.class);
        when(command.sku()).thenReturn("SKU-123");

        when(productRepository.findBySku("SKU-123")).thenReturn(Optional.of(mockProduct));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productApplicationService.create(command));

        assertEquals("SKU đã tồn tại", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createVariantToExistingProduct_Success() {
        CreateVariantsCommand command = mock(CreateVariantsCommand.class);
        when(command.colors()).thenReturn(List.of("Đen", "Trắng"));
        when(command.sizes()).thenReturn(List.of("S", "M", "L"));
        when(command.additionalPrice()).thenReturn(BigDecimal.valueOf(50000.0));

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);
        when(productDtoMapper.toResponse(mockProduct)).thenReturn(mockProductResponse);

        ProductResponse result = productApplicationService.createVariantToExistingProduct(productId, command);
        assertNotNull(result);
        assertEquals(mockProductResponse, result);

        verify(mockProduct).addVariant(List.of("Đen", "Trắng"), List.of("S", "M", "L"), BigDecimal.valueOf(50000.0));
        verify(productRepository).save(mockProduct);
    }

    @Test
    @DisplayName("Create Variant to Existing Product - Throws ProductNotFoundException")
    void createVariantToExistingProduct_ThrowsException_WhenProductNotFound() {
        CreateVariantsCommand command = mock(CreateVariantsCommand.class);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,
                () -> productApplicationService.createVariantToExistingProduct(productId, command));
        verify(productRepository, never()).save(any(Product.class));
    }


    @Test
    @DisplayName("Update Product - Success")
    void update_Success() {
        UpdateProductCommand command = mock(UpdateProductCommand.class);
        when(command.variants()).thenReturn(Collections.emptyList());

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(mockProduct)).thenReturn(mockProduct);
        when(productDtoMapper.toResponse(mockProduct)).thenReturn(mockProductResponse);

        ProductResponse result = productApplicationService.update(productId, command);

        assertNotNull(result);
        verify(mockProduct).updateInfo(command.name(), command.price());
        verify(productRepository).save(mockProduct);
    }

    @Test
    @DisplayName("Update Product - Throws ProductNotFoundException")
    void update_ThrowsException_WhenProductNotFound() {
        UpdateProductCommand command = mock(UpdateProductCommand.class);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productApplicationService.update(productId, command));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get By Id - Success")
    void getById_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productDtoMapper.toResponse(mockProduct)).thenReturn(mockProductResponse);
        ProductResponse result = productApplicationService.getById(productId);
        assertEquals(mockProductResponse, result);
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Get By Id - Throws ProductNotFoundException")
    void getById_ThrowsException_WhenProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productApplicationService.getById(productId));
    }

    @Test
    @DisplayName("Get All - Success")
    void getAll_Success() {
        ProductCriteriaCommand criteriaCommand = mock(ProductCriteriaCommand.class);
        List<Product> productList = List.of(mockProduct);
        Pagination<Product> mockPagination = new Pagination<>(productList, 1,1,1L);

        when(productRepository.findAll(criteriaCommand)).thenReturn(mockPagination);
        when(productDtoMapper.toResponse(mockProduct)).thenReturn(mockProductResponse);

        Pagination<ProductResponse> result = productApplicationService.getAll(criteriaCommand);
        assertNotNull(result);
        assertEquals(1, result.data().size());
        assertEquals(mockProductResponse, result.data().get(0));
        assertEquals(1, result.currentPage());
        assertEquals(1, result.totalPage());
        assertEquals(1L, result.totalElements());
    }

    @Test
    @DisplayName("Delete Product - Success")
    void delete_Success() {
        when(productRepository.existsById(productId)).thenReturn(true);
        productApplicationService.delete(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Delete Product - Throws ProductNotFoundException")
    void delete_ThrowsException_WhenProductNotFound() {
        when(productRepository.existsById(productId)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productApplicationService.delete(productId));
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Delete Product Variant - Success")
    void deleteProductVariant_Success() {
        String color = "Red";
        String size = "XL";
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        productApplicationService.deleteProductVariant(productId, color, size);
        verify(mockProduct).removeVariantsByCriteria(color, size);
        verify(productRepository).save(mockProduct);
    }
}