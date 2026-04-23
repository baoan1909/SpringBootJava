package com.example.product.interfaces.rest;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.service.ProductApplicationService;
import com.example.product.application.serviceImpl.AuthApplicationServiceImpl;
import com.example.product.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerProductController.class)
class SellerProductControllerTest {

    private static final String SELLER_EMAIL = "seller@gmail.com";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductApplicationService productApplicationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AuthApplicationServiceImpl authApplicationService;

    @Test
    @DisplayName("GET /api/seller/products - lấy danh sách sản phẩm của seller đang đăng nhập")
    void getSellerProducts_ShouldReturnSellerProducts() throws Exception {
        ProductResponse productResponse = productResponse(1L, "Sản phẩm seller");
        Pagination<ProductResponse> expectedResponse = new Pagination<>(List.of(productResponse), 0, 1, 1L);

        when(productApplicationService.getAllBySellerEmail(eq(SELLER_EMAIL), any(ProductCriteriaCommand.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(get("/api/seller/products")
                        .with(user(SELLER_EMAIL).roles("SELLER"))
                        .principal(sellerAuthentication())
                        .param("keyWord", "iphone")
                        .param("minPrice", "1000")
                        .param("maxPrice", "2000")
                        .param("status", "ALL")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Sản phẩm seller"));

        ArgumentCaptor<ProductCriteriaCommand> commandCaptor = ArgumentCaptor.forClass(ProductCriteriaCommand.class);
        verify(productApplicationService).getAllBySellerEmail(eq(SELLER_EMAIL), commandCaptor.capture());

        ProductCriteriaCommand command = commandCaptor.getValue();
        assertEquals("iphone", command.keyWord());
        assertEquals(0, command.minPrice().compareTo(BigDecimal.valueOf(1000)));
        assertEquals(0, command.maxPrice().compareTo(BigDecimal.valueOf(2000)));
        assertNull(command.status());
        assertEquals(0, command.page());
        assertEquals(10, command.size());
        verifyNoMoreInteractions(productApplicationService);
    }

    @Test
    @DisplayName("GET /api/seller/products/{id} - lấy chi tiết sản phẩm của seller")
    void getProductByIdForSeller_ShouldReturnProduct_WhenSellerOwnsProduct() throws Exception {
        ProductResponse expectedResponse = productResponse(10L, "Sản phẩm chi tiết");

        when(productApplicationService.getByIdForSeller(10L, SELLER_EMAIL)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/seller/products/10")
                        .with(user(SELLER_EMAIL).roles("SELLER"))
                        .principal(sellerAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Sản phẩm chi tiết"));

        verify(productApplicationService).getByIdForSeller(10L, SELLER_EMAIL);
        verifyNoMoreInteractions(productApplicationService);
    }

    @Test
    @DisplayName("POST /api/seller/products - tạo sản phẩm mới")
    void create_ShouldReturnCreatedProduct() throws Exception {
        CreateProductCommand command = createCommand();
        ProductResponse expectedResponse = productResponse(20L, command.name());

        when(productApplicationService.create(any(CreateProductCommand.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/seller/products")
                        .with(user(SELLER_EMAIL).roles("SELLER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProductJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.name").value(command.name()));

        ArgumentCaptor<CreateProductCommand> commandCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);
        verify(productApplicationService).create(commandCaptor.capture());
        assertEquals(command.name(), commandCaptor.getValue().name());
        assertEquals(command.slug(), commandCaptor.getValue().slug());
        assertEquals(command.variants().get(0).skuCode(), commandCaptor.getValue().variants().get(0).skuCode());
        verifyNoMoreInteractions(productApplicationService);
    }

    @Test
    @DisplayName("PUT /api/seller/products/{id} - cập nhật sản phẩm")
    void update_ShouldReturnUpdatedProduct() throws Exception {
        UpdateProductCommand command = updateCommand();
        ProductResponse expectedResponse = productResponse(30L, command.name());

        when(productApplicationService.update(eq(30L), any(UpdateProductCommand.class))).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/seller/products/30")
                        .with(user(SELLER_EMAIL).roles("SELLER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateProductJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.name").value(command.name()));

        ArgumentCaptor<UpdateProductCommand> commandCaptor = ArgumentCaptor.forClass(UpdateProductCommand.class);
        verify(productApplicationService).update(eq(30L), commandCaptor.capture());
        assertEquals(command.name(), commandCaptor.getValue().name());
        assertEquals(command.slug(), commandCaptor.getValue().slug());
        assertEquals(command.variants().get(0).id(), commandCaptor.getValue().variants().get(0).id());
        verifyNoMoreInteractions(productApplicationService);
    }

    @Test
    @DisplayName("PATCH /api/seller/products/{id}/restore - khôi phục sản phẩm")
    void restore_ShouldReturnNoContentAndCallService() throws Exception {
        mockMvc.perform(patch("/api/seller/products/40/restore")
                        .with(user(SELLER_EMAIL).roles("SELLER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productApplicationService).restoreProduct(40L);
        verifyNoMoreInteractions(productApplicationService);
    }

    @Test
    @DisplayName("PATCH /api/seller/products/{id} - seller xóa mềm sản phẩm")
    void deleteBySeller_ShouldReturnNoContentAndCallService() throws Exception {
        mockMvc.perform(patch("/api/seller/products/50")
                        .with(user(SELLER_EMAIL).roles("SELLER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productApplicationService).deleteBySeller(50L);
        verifyNoMoreInteractions(productApplicationService);
    }

    @Test
    @DisplayName("PATCH /api/seller/products/{id}/resubmit - gửi duyệt lại sản phẩm")
    void resubmit_ShouldReturnNoContentAndCallService() throws Exception {
        mockMvc.perform(patch("/api/seller/products/60/resubmit")
                        .with(user(SELLER_EMAIL).roles("SELLER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productApplicationService).resubmitProduct(60L);
        verifyNoMoreInteractions(productApplicationService);
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

    private CreateProductCommand createCommand() {
        return new CreateProductCommand(
                "Sản phẩm mới",
                "san-pham-moi",
                "Mô tả sản phẩm mới",
                List.of(new CreateProductCommand.VariantItem(
                        BigDecimal.valueOf(100000),
                        10,
                        "SKU-CREATE-1",
                        List.of(new CreateProductCommand.VariantItem.AttributeItem("Màu sắc", "Đen"))
                ))
        );
    }

    private UpdateProductCommand updateCommand() {
        return new UpdateProductCommand(
                "Sản phẩm đã cập nhật",
                "san-pham-da-cap-nhat",
                "Mô tả đã cập nhật",
                List.of(new UpdateProductCommand.VariantItem(
                        1L,
                        BigDecimal.valueOf(120000),
                        15,
                        "SKU-UPDATE-1",
                        List.of(new UpdateProductCommand.VariantItem.AttributeItem("Kích cỡ", "L"))
                ))
        );
    }

    private UsernamePasswordAuthenticationToken sellerAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                SELLER_EMAIL,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
        );
    }

    private String createProductJson() {
        return """
                {
                  "name": "Sản phẩm mới",
                  "slug": "san-pham-moi",
                  "description": "Mô tả sản phẩm mới",
                  "variants": [
                    {
                      "price": 100000,
                      "stockQuantity": 10,
                      "skuCode": "SKU-CREATE-1",
                      "attributes": [
                        {
                          "name": "Màu sắc",
                          "value": "Đen"
                        }
                      ]
                    }
                  ]
                }
                """;
    }

    private String updateProductJson() {
        return """
                {
                  "name": "Sản phẩm đã cập nhật",
                  "slug": "san-pham-da-cap-nhat",
                  "description": "Mô tả đã cập nhật",
                  "variants": [
                    {
                      "id": 1,
                      "price": 120000,
                      "stockQuantity": 15,
                      "skuCode": "SKU-UPDATE-1",
                      "attributes": [
                        {
                          "name": "Kích cỡ",
                          "value": "L"
                        }
                      ]
                    }
                  ]
                }
                """;
    }
}
