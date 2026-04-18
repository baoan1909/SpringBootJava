package com.example.product.application.serviceImpl;

import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.mapper.ProductDtoMapper;
import com.example.product.domain.model.Product;
import com.example.product.domain.model.ProductVariant;
import com.example.product.domain.model.ProductVariantAttribute;
import com.example.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @InjectMocks
    private ProductApplicationServiceImpl productApplicationService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private final String CURRENT_SELLER_EMAIL = "seller_chinh_chu@gmail.com";
    private final String HACKER_EMAIL = "hacker@gmail.com";

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    private void mockLoginUser(String email) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    private List<ProductVariant> createProductVariants() {
        ProductVariantAttribute attribute = new ProductVariantAttribute("Màu sắc", "Đỏ");
        ProductVariant variant = new ProductVariant(
                BigDecimal.valueOf(150000),
                100,
                "SKU-DO-01",
                List.of(attribute)
        );
        return List.of(variant);
    }

    @Test
    void create_ShouldReturnProductResponse_WhenDataAndVariantsAreValid() {
        CreateProductCommand command = new CreateProductCommand("Iphone", "iphone", "Mô tả", new ArrayList<>());
        ProductResponse expectedProductResponse = mock(ProductResponse.class);
        List<ProductVariant> productVariants = createProductVariants();

        when(productDtoMapper.toVariantsFormCreateCommand(any())).thenReturn(productVariants);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            assertEquals(BigDecimal.valueOf(150000), savedProduct.getMinPrice());
            assertEquals(100, savedProduct.getTotalStock());
            return savedProduct;
        });
        when(productDtoMapper.toResponse(any(Product.class))).thenReturn(expectedProductResponse);

        ProductResponse actualProductResponse = productApplicationService.create(command);

        assertNotNull(actualProductResponse);
        verify(productRepository, times(1)).save(any(Product.class));

    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenVariantsAreEmpty() {
        CreateProductCommand command = new CreateProductCommand("Iphone", "iphone", "Mô tả", new ArrayList<>());
        when(productDtoMapper.toVariantsFormCreateCommand(any())).thenReturn(new ArrayList<>());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
           productApplicationService.create(command);
        });

        assertEquals("Sản phẩm có ít nhất 1 phân loại", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_ShouldSyncVariantsAndSave_WhenUserIsOwner() {
        Long productId = 1L;
        UpdateProductCommand command = new UpdateProductCommand("Iphone 15", "iphone-15", "Mô tả mới", new ArrayList<>());
        List<ProductVariant> updatedVariants = createProductVariants();

        mockLoginUser(CURRENT_SELLER_EMAIL);

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getCreatedBy()).thenReturn(CURRENT_SELLER_EMAIL);

        when(productDtoMapper.toVariantsFormUpdateCommand(any())).thenReturn(updatedVariants);
        when(productRepository.save(mockProduct)).thenReturn(mockProduct);
        when(productDtoMapper.toResponse(mockProduct)).thenReturn(mock(ProductResponse.class));

        productApplicationService.update(productId, command);
        verify(mockProduct, times(1)).updateInfo(command.name(), command.slug(), command.description());

        verify(mockProduct, times(1)).syncVariants(updatedVariants);
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void update_ShouldThrowAccessDeniedException_WhenUserIsNotOwner() {
        Long productId = 1L;
        UpdateProductCommand command = new UpdateProductCommand("Iphone 15", "iphone-15", "Mô tả mới", new ArrayList<>());
        List<ProductVariant> updatedVariants = createProductVariants();

        mockLoginUser(HACKER_EMAIL);

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getCreatedBy()).thenReturn(CURRENT_SELLER_EMAIL);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            productApplicationService.update(productId, command);
        });

        assertEquals("Bạn không có quyền thao tác trên sản phẩm của người khác", exception.getMessage());

        verify(mockProduct, never()).updateInfo(anyString(), anyString(), anyString());
        verify(mockProduct, never()).syncVariants(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteBySeller_ShouldCallSellerDeleteAndSave_WhenUserIsOwner() {
        Long productId = 1L;
        mockLoginUser(CURRENT_SELLER_EMAIL);

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getCreatedBy()).thenReturn(CURRENT_SELLER_EMAIL);

        productApplicationService.deleteBySeller(productId);

        verify(mockProduct, times(1)).sellerDelete();
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void deleteBySeller_ShouldThrowAccessDeniedException_WhenUserIsNotOwner() {
        Long productId = 1L;
        mockLoginUser(HACKER_EMAIL);

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getCreatedBy()).thenReturn(CURRENT_SELLER_EMAIL);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            productApplicationService.deleteBySeller(productId);
        });

        assertEquals("Bạn không có quyền thao tác trên sản phẩm của người khác", exception.getMessage());

        verify(mockProduct, never()).sellerDelete();
        verify(productRepository, never()).save(any());
    }
    @Test
    void deleteBySeller_ShouldThrowIllegalArgumentException_WhenProductIsFrozen() {
        Long productId = 1L;
        mockLoginUser(CURRENT_SELLER_EMAIL);

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getCreatedBy()).thenReturn(CURRENT_SELLER_EMAIL);

        doThrow(new IllegalArgumentException("Sản phẩm đang bị khóa do vi phạm không thể tự xóa"))
                .when(mockProduct).sellerDelete();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productApplicationService.deleteBySeller(productId);
        });

        assertEquals("Sản phẩm đang bị khóa do vi phạm không thể tự xóa", exception.getMessage());

        verify(productRepository, never()).save(any());
    }

    @Test
    void resubmitProduct_ShouldCallResubmitAndSave_WhenUserIsOwner() {
        Long productId = 1L;
        mockLoginUser(CURRENT_SELLER_EMAIL);

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getCreatedBy()).thenReturn(CURRENT_SELLER_EMAIL);

        productApplicationService.resubmitProduct(productId);

        verify(mockProduct, times(1)).resubmit();
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void restoreProduct_ShouldCallRestoreAndSave_WhenUserIsOwner() {
        Long productId = 1L;
        mockLoginUser(CURRENT_SELLER_EMAIL);

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(mockProduct.getCreatedBy()).thenReturn(CURRENT_SELLER_EMAIL);

        productApplicationService.restoreProduct(productId);

        verify(mockProduct, times(1)).restore();
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void approveProduct_ShouldCallApproveAndSave_WhenProductExists() {
        Long productId = 1L;
        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        productApplicationService.approveProduct(productId);

        verify(mockProduct, times(1)).approve();
        verify(productRepository, times(1)).save(mockProduct);
    }
    @Test
    void approveProduct_ShouldThrowIllegalStateException_WhenStatusIsNotPending() {
        Long productId = 1L;
        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        doThrow(new IllegalStateException("Chỉ có thể duyệt sản phẩm đang chờ xét đuyệt"))
                .when(mockProduct).approve();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            productApplicationService.approveProduct(productId);
        });

        assertEquals("Chỉ có thể duyệt sản phẩm đang chờ xét đuyệt", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void rejectProduct_ShouldCallRejectAndSave_WhenProductExists() {
        Long productId = 1L;
        String reason = "Hình ảnh mờ";
        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        productApplicationService.rejectProduct(productId, reason);

        verify(mockProduct, times(1)).reject(reason);
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void rejectProduct_ShouldThrowIllegalArgumentException_WhenReasonIsBlank() {
        Long productId = 1L;
        String emptyReason = "   ";

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        doThrow(new IllegalArgumentException("Phải cung cấp lý do từ chối cho seller"))
                .when(mockProduct).reject(emptyReason);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productApplicationService.rejectProduct(productId, emptyReason);
        });

        assertEquals("Phải cung cấp lý do từ chối cho seller", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void freezeProduct_ShouldCallFreezeAndSave_WhenProductExists() {
        Long productId = 1L;
        String reason = "Bán hàng giả";
        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        productApplicationService.freezeProduct(productId, reason);

        verify(mockProduct, times(1)).freeze(reason);
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void unfreezeProduct_ShouldCallUnfreezeAndSave_WhenProductExists() {
        Long productId = 1L;
        String reason = "Đã bổ sung giấy tờ";
        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        productApplicationService.unfreezeProduct(productId, reason);

        verify(mockProduct, times(1)).unfreeze(reason);
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void getById_ShouldReturnProductResponse_WhenProductExists() {
        Long productId = 1L;
        Product mockProduct = mock(Product.class);
        ProductResponse expectedResponse = mock(ProductResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productDtoMapper.toResponse(mockProduct)).thenReturn(expectedResponse);

        ProductResponse actualResponse = productApplicationService.getById(productId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getAll_ShouldReturnPaginationOfProductResponse() {
        ProductCriteriaCommand command = mock(ProductCriteriaCommand.class);
        Product mockProduct = mock(Product.class);
        ProductResponse mockResponse = mock(ProductResponse.class);

        com.example.product.application.common.Pagination<Product> mockProductPage =
                new com.example.product.application.common.Pagination<>(List.of(mockProduct), 1, 1, 1L);

        when(productRepository.findAll(command)).thenReturn(mockProductPage);
        when(productDtoMapper.toResponse(mockProduct)).thenReturn(mockResponse);

        com.example.product.application.common.Pagination<ProductResponse> actualPage =
                productApplicationService.getAll(command);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.data().size());
        assertEquals(mockResponse, actualPage.data().get(0)); // Kiểm tra xem DTO mapper có map đúng item không
        assertEquals(1, actualPage.totalElements());
    }
}