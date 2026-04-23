package com.example.product.application.service;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.dto.response.ProductResponse;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductApplicationServiceIntegrationTest {
    @Autowired
    private ProductApplicationService productApplicationService;

    @Autowired
    private ProductRepository productRepository;

    private Long savedProductId;
    private final String OWNER_EMAIL = "seller_owner@gmail.com";
    private final String OTHER_SELLER_EMAIL = "other_seller@gmail.com";
    private final String ADMIN_EMAIL = "admin@gmail.com";

    @BeforeEach
    void setUp() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(OWNER_EMAIL, null, List.of(new SimpleGrantedAuthority("ROLE_SELLER")))
        );
        Product product = new Product(
                null,
                "Sản phẩm gốc",
                "san-pham-goc",
                "Mô tả gốc"
        );
        product.setCreatedBy(OWNER_EMAIL);

        ProductVariant productVariant = new ProductVariant(
                null,
                java.math.BigDecimal.valueOf(100000),
                50,
                "SKU-ROOT",
                new ArrayList<>()
        );
        product.syncVariants(List.of(productVariant));

        Product savedProduct = productRepository.save(product);
        this.savedProductId = savedProduct.getId();
        SecurityContextHolder.getContext().setAuthentication(currentAuth);
    }

    private Product saveProduct(String name, String slug, String ownerEmail, ProductStatus status) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(ownerEmail, null, List.of(new SimpleGrantedAuthority("ROLE_SELLER")))
        );

        try {
            Product product = new Product(null, name, slug, "Mô tả " + name);
            product.syncVariants(List.of(new ProductVariant(null, BigDecimal.valueOf(50000), 20, "SKU-" + slug, new ArrayList<>())));
            Product savedProduct = productRepository.save(product);

            if (status == ProductStatus.ON_SHELF) {
                savedProduct.approve();
                savedProduct = productRepository.save(savedProduct);
            } else if (status == ProductStatus.REJECTED) {
                savedProduct.reject("Không hợp lệ");
                savedProduct = productRepository.save(savedProduct);
            } else if (status == ProductStatus.DELETED) {
                savedProduct.sellerDelete();
                savedProduct = productRepository.save(savedProduct);
            }

            return savedProduct;
        } finally {
            SecurityContextHolder.getContext().setAuthentication(currentAuth);
        }
    }

    private ProductCriteriaCommand defaultCriteria() {
        return new ProductCriteriaCommand(null, null, null, null, null, 0, 20);
    }

    @Nested
    @DisplayName("Public API Tests")
    class PublicApiTests {

        @Test
        void getByIdForUsers_ShouldReturnProduct_WhenStatusIsOnShelf() {
            Product product = productRepository.findById(savedProductId).get();
            product.approve();
            productRepository.save(product);

            ProductResponse response = productApplicationService.getByIdForUsers(savedProductId);
            assertNotNull(response);
            assertEquals("Sản phẩm gốc", response.name());
        }

        @Test
        void getByIdForUsers_ShouldThrowException_WhenStatusIsNotOnShelf() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> productApplicationService.getByIdForUsers(savedProductId));
            assertEquals("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh", ex.getMessage());
        }

        @Test
        void getAllForUsers_ShouldReturnOnlyOnShelfProducts(){
            Product product1 = productRepository.findById(savedProductId).get();
            product1.approve();
            productRepository.save(product1);

            Product product2 = new Product(null, "Sản phẩm ẩn", "san-pham-an", "Mô tả");
            product2.setCreatedBy(OWNER_EMAIL);
            product2.syncVariants(List.of(new ProductVariant(null, BigDecimal.valueOf(50000), 50, "SKU-2", new ArrayList<>())));
            productRepository.save(product2);

            ProductCriteriaCommand command = new ProductCriteriaCommand(
                    null, null, null, null, null, 0, 10
            );

            Pagination<ProductResponse> list = productApplicationService.getAllForUsers(command);
            assertNotNull(list);
            assertEquals(1, list.totalElements());
            assertEquals(1, list.data().size());
            assertEquals(savedProductId, list.data().get(0).id());
            assertEquals(ProductStatus.ON_SHELF.name(), list.data().get(0).status());
            assertTrue(list.data().stream().noneMatch(product -> product.id().equals(product2.getId())));
        }

        @Test
        @WithMockUser(username = "customer@gmail.com", roles = "USER")
        void getAllForUsers_ShouldIgnoreRequestedStatusAndReturnOnlyOnShelfProducts_ForUserRole() {
            Product onShelfProduct = saveProduct("Sản phẩm đang bán", "san-pham-dang-ban", OWNER_EMAIL, ProductStatus.ON_SHELF);
            Product pendingProduct = saveProduct("Sản phẩm chờ duyệt", "san-pham-cho-duyet", OWNER_EMAIL, ProductStatus.PENDING_REVIEW);
            Product otherSellerOnShelfProduct = saveProduct("Sản phẩm seller khác", "san-pham-seller-khac", OTHER_SELLER_EMAIL, ProductStatus.ON_SHELF);

            ProductCriteriaCommand command = new ProductCriteriaCommand(null, null, null, "ALL", null, 0, 20);

            Pagination<ProductResponse> response = productApplicationService.getAllForUsers(command);

            assertNotNull(response);
            assertEquals(2, response.totalElements());
            assertTrue(response.data().stream().allMatch(product -> ProductStatus.ON_SHELF.name().equals(product.status())));
            assertTrue(response.data().stream().anyMatch(product -> product.id().equals(onShelfProduct.getId())));
            assertTrue(response.data().stream().anyMatch(product -> product.id().equals(otherSellerOnShelfProduct.getId())));
            assertTrue(response.data().stream().noneMatch(product -> product.id().equals(savedProductId)));
            assertTrue(response.data().stream().noneMatch(product -> product.id().equals(pendingProduct.getId())));
        }
    }

    @Nested
    @DisplayName("Seller Specific Tests")
    class SellerTests {

        @Test
        @WithMockUser(username = OWNER_EMAIL, roles = "SELLER")
        void update_ShouldSuccess_WhenSellerIsOwner() {
            Product originProduct = productRepository.findById(savedProductId).get();
            Long realVariantId = originProduct.getVariants().get(0).getId();
            UpdateProductCommand.VariantItem variantCommand = new UpdateProductCommand.VariantItem(
                    realVariantId,
                    BigDecimal.valueOf(200000),
                    100,
                    "SKU-NEW",
                    new ArrayList<>()
            );
            UpdateProductCommand command = new UpdateProductCommand(
                    "Tên sản phẩm mới",
                    "ten-san-pham-moi",
                    "Mô tả mới",
                    List.of(variantCommand)
            );

            productApplicationService.update(savedProductId, command);

            Product updatedProduct = productRepository.findById(savedProductId).get();
            assertEquals("Tên sản phẩm mới", updatedProduct.getName());
            assertEquals(100, updatedProduct.getTotalStock());
        }

        @Test
        @WithMockUser(username = OTHER_SELLER_EMAIL, roles = "SELLER")
        void update_ShouldThrowAccessDenied_WhenSellerIsNotOwner() {
            UpdateProductCommand command = new UpdateProductCommand("ten", "slug", "mota", List.of());

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
            ProductResponse response = productApplicationService.getByIdForSeller(savedProductId, OWNER_EMAIL);
            assertNotNull(response);
            assertEquals(savedProductId, response.id());
            assertFalse(response.name().isEmpty());
        }

        @Test
        @WithMockUser(username = OTHER_SELLER_EMAIL, roles = "SELLER")
        void getByIdForSeller_ShouldThrowException_WhenSellerIsNotOwner() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> productApplicationService.getByIdForSeller(savedProductId, OTHER_SELLER_EMAIL));
            assertEquals("Bạn không có quyền truy cập sản phẩm này!", ex.getMessage());
        }

        @Test
        @WithMockUser(username = OWNER_EMAIL, roles = "SELLER")
        void getAllBySellerEmail_ShouldReturnOnlyProductsCreatedBySeller() {
            Product ownerOnShelfProduct = saveProduct("Sản phẩm của seller", "san-pham-cua-seller", OWNER_EMAIL, ProductStatus.ON_SHELF);
            Product otherSellerProduct = saveProduct("Sản phẩm của seller khác", "san-pham-cua-seller-khac", OTHER_SELLER_EMAIL, ProductStatus.ON_SHELF);

            Pagination<ProductResponse> response = productApplicationService.getAllBySellerEmail(OWNER_EMAIL, defaultCriteria());

            assertNotNull(response);
            assertEquals(2, response.totalElements());
            assertTrue(response.data().stream().anyMatch(product -> product.id().equals(savedProductId)));
            assertTrue(response.data().stream().anyMatch(product -> product.id().equals(ownerOnShelfProduct.getId())));
            assertTrue(response.data().stream().noneMatch(product -> product.id().equals(otherSellerProduct.getId())));
        }

        @Test
        @WithMockUser(username = "customer@gmail.com", roles = "USER")
        void getAllBySellerEmail_ShouldThrowAccessDenied_WhenCalledByUser() {
            assertThrows(AccessDeniedException.class,
                    () -> productApplicationService.getAllBySellerEmail(OWNER_EMAIL, defaultCriteria()));
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

        @Test
        @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
        void getAll_ShouldReturnProductsFromAllSellersAndStatuses_WhenCalledByAdmin() {
            Product ownerOnShelfProduct = saveProduct("Sản phẩm đang bán admin xem", "san-pham-dang-ban-admin-xem", OWNER_EMAIL, ProductStatus.ON_SHELF);
            Product otherSellerPendingProduct = saveProduct("Sản phẩm chờ duyệt admin xem", "san-pham-cho-duyet-admin-xem", OTHER_SELLER_EMAIL, ProductStatus.PENDING_REVIEW);

            Pagination<ProductResponse> response = productApplicationService.getAll(defaultCriteria());

            assertNotNull(response);
            assertEquals(3, response.totalElements());
            assertTrue(response.data().stream().anyMatch(product -> product.id().equals(savedProductId)
                    && ProductStatus.PENDING_REVIEW.name().equals(product.status())));
            assertTrue(response.data().stream().anyMatch(product -> product.id().equals(ownerOnShelfProduct.getId())
                    && ProductStatus.ON_SHELF.name().equals(product.status())));
            assertTrue(response.data().stream().anyMatch(product -> product.id().equals(otherSellerPendingProduct.getId())
                    && ProductStatus.PENDING_REVIEW.name().equals(product.status())));
        }

        @Test
        @WithMockUser(username = OWNER_EMAIL, roles = "SELLER")
        void getAll_ShouldThrowAccessDenied_WhenCalledBySeller() {
            assertThrows(AccessDeniedException.class,
                    () -> productApplicationService.getAll(defaultCriteria()));
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
