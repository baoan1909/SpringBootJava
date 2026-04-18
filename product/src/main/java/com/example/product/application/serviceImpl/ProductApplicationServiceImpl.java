package com.example.product.application.serviceImpl;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.mapper.ProductDtoMapper;
import com.example.product.application.service.ProductApplicationService;
import com.example.product.domain.exception.ProductNotFoundException;
import com.example.product.domain.model.Product;
import com.example.product.domain.model.ProductStatus;
import com.example.product.domain.model.ProductVariant;
import com.example.product.domain.repository.ProductRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductApplicationServiceImpl implements ProductApplicationService {
    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;

    public ProductApplicationServiceImpl(ProductRepository productRepository, ProductDtoMapper productDtoMapper) {
        this.productRepository = productRepository;
        this.productDtoMapper = productDtoMapper;
    }

    @Override
    @PreAuthorize("hasRole('SELLER')")
    public ProductResponse create(CreateProductCommand command) {
        Product product = new Product(
                null,
                command.name(),
                command.slug(),
                command.description()
        );
        List<ProductVariant> variants = productDtoMapper.toVariantsFormCreateCommand(command.variants());
        product.syncVariants(variants);
        Product created = productRepository.save(product);
        return productDtoMapper.toResponse(created);
    }

    @Override
    @PreAuthorize("hasRole('SELLER')")
    public ProductResponse update(Long id, UpdateProductCommand command) {
        Product product = getProductAndCheckOwnership(id);
        product.updateInfo(command.name(), command.slug() ,command.description());

        List<ProductVariant> variants = productDtoMapper.toVariantsFormUpdateCommand(command.variants());
        product.syncVariants(variants);
        Product updated = productRepository.save(product);
        return productDtoMapper.toResponse(updated);
    }

    @Override
    @PreAuthorize("hasRole('SELLER')")
    public void deleteBySeller(Long id){
        Product product = getProductAndCheckOwnership(id);
        product.sellerDelete();
        productRepository.save(product);
    }

    @Override
    @PreAuthorize("hasRole('SELLER')")
    public void resubmitProduct(Long id) {
        Product product = getProductAndCheckOwnership(id);
        product.resubmit();
        productRepository.save(product);
    }

    @Override
    @PreAuthorize("hasRole('SELLER')")
    public void restoreProduct(Long id) {
        Product product = getProductAndCheckOwnership(id);
        product.restore();
        productRepository.save(product);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void approveProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.approve();
        productRepository.save(product);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void rejectProduct(Long id, String reason){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.reject(reason);
        productRepository.save(product);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void freezeProduct(Long id, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.freeze(reason);
        productRepository.save(product);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void unfreezeProduct(Long id, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.unfreeze(reason);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getByIdForUsers(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getStatus() != ProductStatus.ON_SHELF) {
            throw new RuntimeException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh");
        }
        return productDtoMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SELLER')")
    public ProductResponse getByIdForSeller(Long id, String sellerEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        if (product.getCreatedBy() == null || !product.getCreatedBy().equals(sellerEmail)) {
            throw new RuntimeException("Bạn không có quyền truy cập sản phẩm này!");
        }

        return productDtoMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productDtoMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagination<ProductResponse> getAllForUsers(ProductCriteriaCommand command) {
        ProductCriteriaCommand forcedCommand = command.withStatus("ON_SHELF");
        return this.getAll(forcedCommand);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SELLER')")
    public Pagination<ProductResponse> getAllBySellerEmail(String email, ProductCriteriaCommand command) {
        ProductCriteriaCommand forcedCommand = command.withOwnerEmail(email);
        return this.getAll(forcedCommand);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Pagination<ProductResponse> getAll(ProductCriteriaCommand command) {
        Pagination<Product> productPage = productRepository.findAll(command);
        List<ProductResponse> responseData = productPage.data()
                .stream()
                .map(productDtoMapper::toResponse)
                .toList();

        return new Pagination<>(
                responseData,
                productPage.currentPage(),
                productPage.totalPage(),
                productPage.totalElements()
        );
    }

    private Product getProductAndCheckOwnership(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        String currentUserEmail = getCurrentUserEmail();
        if(!currentUserEmail.equals(product.getCreatedBy())){
            throw new AccessDeniedException("Bạn không có quyền thao tác trên sản phẩm của người khác");
        }
        return product;
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Vui lòng đăng nhập để thực hiện chức năng");
        }
        return authentication.getName();
    }
}
