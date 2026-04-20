package com.example.product.application.service;

import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.mapper.ProductDtoMapper;
import com.example.product.domain.exception.ProductNotFoundException;
import com.example.product.domain.model.Product;
import com.example.product.domain.model.ProductStatus;
import com.example.product.domain.model.ProductVariant;
import com.example.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductApplicationServiceIntegrationTest {
    @Autowired
    private ProductApplicationService productApplicationService;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private ProductDtoMapper productDtoMapper;

    private Long savedProductId;
    private final String OWNER_EMAIL = "seller_owner@gmail.com";
    private final String OTHER_SELLER_EMAIL = "other_seller@gmail.com";
    private final String ADMIN_EMAIL = "admin@gmail.com";

    @BeforeEach
    void setUp() {
        Product product = new Product(
                null,
                "Sản phẩm gốc",
                "san-pham-goc",
                "Mô tả gốc"
        );
        product.setCreatedBy(OWNER_EMAIL);
        Product savedProduct = productRepository.save(product);
        this.savedProductId = savedProduct.getId();
    }

    @Nested
    @DisplayName("Public API Tests")
    class PublicApiTests {

        @Test
        void getByIdForUsers_ShouldReturnProduct_WhenStatusIsOnShelf() {
            Product product = productRepository.findById(savedProductId).get();
            product.approve();
            productRepository.save(product);

            when(productDtoMapper.toResponse(any())).thenReturn(mock(ProductResponse.class));

            ProductResponse response = productApplicationService.getByIdForUsers(savedProductId);
            assertNotNull(response);
        }

        @Test
        void getByIdForUsers_ShouldThrowException_WhenStatusIsNotOnShelf() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> productApplicationService.getByIdForUsers(savedProductId));
            assertEquals("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Seller Specific Tests")
    class SellerTests {

        @Test
        @WithMockUser(username = OWNER_EMAIL, roles = "SELLER")
        void update_ShouldSuccess_WhenSellerIsOwner() {
            UpdateProductCommand command = mock(UpdateProductCommand.class);
            when(command.name()).thenReturn("Tên mới");
            when(command.slug()).thenReturn("ten-moi");
            when(command.description()).thenReturn("Mô tả mới");
            List<ProductVariant> mockVariants = List.of(
                    new ProductVariant(
                            java.math.BigDecimal.valueOf(150000),
                            100,
                            "SKU-MOCK",
                            new ArrayList<>()
                    )
            );
            when(productDtoMapper.toVariantsFormUpdateCommand(any())).thenReturn(mockVariants);

            productApplicationService.update(savedProductId, command);

            Product updatedProduct = productRepository.findById(savedProductId).get();
            assertEquals("Tên mới", updatedProduct.getName());
        }

        @Test
        @WithMockUser(username = OTHER_SELLER_EMAIL, roles = "SELLER")
        void update_ShouldThrowAccessDenied_WhenSellerIsNotOwner() {
            UpdateProductCommand command = mock(UpdateProductCommand.class);

            AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                    () -> productApplicationService.update(savedProductId, command));
            assertEquals("Bạn không có quyền thao tác trên sản phẩm của người khác", ex.getMessage());
        }

        @Test
        @WithMockUser(username = OWNER_EMAIL, roles = "SELLER")
        void deleteBySeller_ShouldChangeStatusToDeleted_WhenSellerIsOwner() {
            productApplicationService.deleteBySeller(savedProductId);

            Product product = productRepository.findById(savedProductId).get();
            assertEquals(ProductStatus.DELETED, product.getStatus());
        }

        @Test
        @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
        void sellerMethods_ShouldThrowAccessDenied_WhenCalledByAdmin() {
            assertThrows(AccessDeniedException.class, () -> productApplicationService.deleteBySeller(savedProductId));
        }

        @Test
        @WithMockUser(username = OWNER_EMAIL, roles = "SELLER")
        void getByIdForSeller_ShouldReturnProduct_WhenSellerIsOwner() {
            when(productDtoMapper.toResponse(any())).thenReturn(mock(ProductResponse.class));

            assertDoesNotThrow(() -> productApplicationService.getByIdForSeller(savedProductId, OWNER_EMAIL));
        }

        @Test
        @WithMockUser(username = OTHER_SELLER_EMAIL, roles = "SELLER")
        void getByIdForSeller_ShouldThrowException_WhenSellerIsNotOwner() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> productApplicationService.getByIdForSeller(savedProductId, OTHER_SELLER_EMAIL));
            assertEquals("Bạn không có quyền truy cập sản phẩm này!", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Admin Specific Tests")
    class AdminTests {

        @Test
        @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
        void approveProduct_ShouldChangeStatusToOnShelf() {
            productApplicationService.approveProduct(savedProductId);

            Product product = productRepository.findById(savedProductId).get();
            assertEquals(ProductStatus.ON_SHELF, product.getStatus());
        }

        @Test
        @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
        void rejectProduct_ShouldChangeStatusToRejectedAndSaveReason() {
            productApplicationService.rejectProduct(savedProductId, "Hình ảnh mờ");

            Product product = productRepository.findById(savedProductId).get();
            assertEquals(ProductStatus.REJECTED, product.getStatus());
            assertEquals("Hình ảnh mờ", product.getRejectionReason());
        }

        @Test
        @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
        void freezeProduct_ShouldChangeStatusToFrozen_WhenStatusIsOnShelf() {
            Product product = productRepository.findById(savedProductId).get();
            product.approve();
            productRepository.save(product);

            productApplicationService.freezeProduct(savedProductId, "Vi phạm bản quyền");

            Product frozenProduct = productRepository.findById(savedProductId).get();
            assertEquals(ProductStatus.FROZEN, frozenProduct.getStatus());
            assertEquals("Vi phạm bản quyền", frozenProduct.getRejectionReason());
        }

        @Test
        @WithMockUser(username = OWNER_EMAIL, roles = "SELLER")
        void adminMethods_ShouldThrowAccessDenied_WhenCalledBySeller() {
            assertThrows(AccessDeniedException.class, () -> productApplicationService.approveProduct(savedProductId));
        }
    }

    @Nested
    @DisplayName("Common Exception Tests")
    class CommonTests {

        @Test
        @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
        void getById_ShouldThrowProductNotFound_WhenIdIsInvalid() {
            assertThrows(ProductNotFoundException.class, () -> productApplicationService.getById(999L));
        }
    }
}