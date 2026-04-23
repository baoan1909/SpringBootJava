package com.example.product.interfaces.rest;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.command.ReasonCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.service.ProductApplicationService;
import com.example.product.application.serviceImpl.AuthApplicationServiceImpl;
import com.example.product.security.config.SecurityConfig;
import com.example.product.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminProductController.class)
@Import(SecurityConfig.class)
class AdminProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductApplicationService productApplicationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AuthApplicationServiceImpl authApplicationService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Admin lấy danh sách sản phẩm - Thành công")
    void getAll() throws Exception {
        ProductResponse productResponse = productResponse(1l, "Sản phẩm của seller");
        Pagination<ProductResponse> expectedResponse = new Pagination<>(List.of(productResponse), 0, 1, 1L);

        when(productApplicationService.getAll(any(ProductCriteriaCommand.class))).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/admin/products")
                .with(user("admin").roles("ADMIN"))
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Admin lấy chi tiết sản phẩm - Thành công")
    void findById() throws Exception {
        ProductResponse response = productResponse(1L, "Sản phẩm A");
        when(productApplicationService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/admin/products/{id}", 1L)
                    .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sản phẩm A"));
    }

    @Test
    @DisplayName("Admin duyệt sản phẩm - Thành công")
    void approve() throws Exception {
        mockMvc.perform(patch("/api/admin/products/1/approve")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
        verify(productApplicationService, times(1)).approveProduct(1L);
    }

    @Test
    @DisplayName("Admin từ chối sản phẩm - Thành công")
    void reject() throws Exception {
        ReasonCommand command = new ReasonCommand("Hình ảnh sản phẩm không rõ nét");

        mockMvc.perform(patch("/api/admin/products/1/reject")
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());

        verify(productApplicationService).rejectProduct(1L, "Hình ảnh sản phẩm không rõ nét");
    }

    @Test
    void freeze() throws Exception {
        ReasonCommand command = new ReasonCommand("Nghi ngờ bán hàng giả hàng nhái");

        mockMvc.perform(patch("/api/admin/products/1/freeze")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());

        verify(productApplicationService).freezeProduct(1L, "Nghi ngờ bán hàng giả hàng nhái");
    }

    @Test
    void unfreeze() throws Exception {
        ReasonCommand command = new ReasonCommand("Đã bổ sung giấy tờ hợp lệ");

        mockMvc.perform(patch("/api/admin/products/1/unfreeze")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
        verify(productApplicationService).unfreezeProduct(1L,"Đã bổ sung giấy tờ hợp lệ");
    }

    @Test
    @DisplayName("Admin API - Trả về 403 khi người dùng chỉ có quyền SELLER")
    void anyApi_ShouldReturnForbidden_WhenUserIsSeller() throws Exception {
        mockMvc.perform(get("/api/admin/products")
                        .with(user("seller").roles("SELLER")))
                .andExpect(status().isForbidden());
    }

    private ProductResponse productResponse(Long id, String name) {
        return new ProductResponse(
                id,
                name,
                "san-pham-" + id,
                "Mô tả sản phẩm",
                "PENDING_REVIEW",
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(100000),
                10,
                LocalDateTime.of(2026, 4, 23, 10, 0),
                List.of()
        );
    }
}
