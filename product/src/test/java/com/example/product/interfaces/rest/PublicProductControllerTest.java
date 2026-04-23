package com.example.product.interfaces.rest;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.service.ProductApplicationService;
import com.example.product.application.serviceImpl.AuthApplicationServiceImpl;
import com.example.product.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicProductController.class)
class PublicProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductApplicationService productApplicationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AuthApplicationServiceImpl authApplicationService;

    @Test
    @DisplayName("GET /api/products - Thành công")
    void getAllProducts_ShouldReturnPagination() throws Exception {
        ProductResponse product = new ProductResponse(
                1L, "iPhone 15", "iphone-15", "Desc",
                "ON_SHELF",
                BigDecimal.TEN, BigDecimal.TEN, 100,
                LocalDateTime.now(), new ArrayList<>()
        );
        Pagination<ProductResponse> mockPage = new Pagination<>(List.of(product), 0, 1, 1L);
        when(productApplicationService.getAllForUsers(any(ProductCriteriaCommand.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/products")
                        .param("keyWord", "iphone")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.data[0].status").value("ON_SHELF"));
    }

    @Test
    @DisplayName("Get /api/products/{id} - Thành công")
    void getProductById_ShouldReturnProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Sản phẩm A", "san-pham-a", "Mô tả", "ON_SHELF", BigDecimal.TEN, BigDecimal.TEN, 50, LocalDateTime.now(), new ArrayList<>());
        when(productApplicationService.getByIdForUsers(1L)).thenReturn(response);
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sản phẩm A"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Ném lỗi khi service báo không tìm thấy sản phẩm")
    void getProductById_ShouldThrowServletException_WhenNotFound() {
        when(productApplicationService.getByIdForUsers(999L)).thenThrow(new RuntimeException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh"));
        ServletException exception = assertThrows(ServletException.class,
                () -> mockMvc.perform(get("/api/products/999")));
        assertEquals("Request processing failed: java.lang.RuntimeException: Sản phẩm không tồn tại hoặc đã ngừng kinh doanh",
                exception.getMessage());
    }

    @Test
    @DisplayName("GET /api/products - Ném lỗi khi giá min > giá max")
    void getAllProducts_ShouldThrowServletException_WhenPriceIsInvalid() {
        ServletException exception = assertThrows(ServletException.class,
                () -> mockMvc.perform(get("/api/products")
                        .param("minPrice", "1000")
                        .param("maxPrice", "90")));
        assertTrue(exception.getMessage().contains("ProductCriteriaCommand"));
    }

    @Test
    @DisplayName("GET /api/products - Trả về 400 khi định dạng trang không phải số")
    void getAllProducts_ShouldReturn400_WhenPageIsNotNumber() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/products - Ném lỗi khi service bị lỗi bất ngờ")
    void getAllProducts_ShouldThrowServletException_WhenServiceFails() {
        when(productApplicationService.getAllForUsers(any()))
                .thenThrow(new RuntimeException("Database connection failed"));

        ServletException exception = assertThrows(ServletException.class,
                () -> mockMvc.perform(get("/api/products")));
        assertEquals("Request processing failed: java.lang.RuntimeException: Database connection failed",
                exception.getMessage());
    }
}
