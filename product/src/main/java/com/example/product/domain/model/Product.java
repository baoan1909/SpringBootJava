package com.example.product.domain.model;
import com.example.product.domain.common.Default;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Product extends Auditable{
    private final Long id;
    private String name;
    private String slug;
    private String description;
    private ProductStatus status;
    private String rejectionReason;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalStock;

    private final List<ProductVariant> variants = new ArrayList<>();

    public Product(Long id, String name, String slug, String description) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name không được để trống");
        }
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.status = ProductStatus.PENDING_REVIEW;
        this.minPrice = BigDecimal.ZERO;
        this.maxPrice = BigDecimal.ZERO;
        this.totalStock = 0;
    }

    @Default
    public Product(Long id, String name, String slug, String description, String rejectionReason, List<ProductVariant> variants) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.status = ProductStatus.PENDING_REVIEW;
        this.rejectionReason = rejectionReason;
        if(variants != null){
            this.variants.addAll(variants);
            recalculateCache();
        }
    }

    public void updateInfo(String name, String slug, String description) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name không được để trống");
        }
        this.name = name;
        this.slug = slug;
        this.description = description;
    }

    public void syncVariants(List<ProductVariant> productVariant) {
        this.variants.clear();
        if(productVariant == null || productVariant.isEmpty()){
            throw new IllegalArgumentException("Sản phẩm có ít nhất 1 phân loại");
        }
        if(productVariant.size() > 50){
            throw new IllegalArgumentException("Sản phẩm không được vượt quá 50 phân loại");
        }
        this.variants.addAll(productVariant);
        recalculateCache();

    }

    private void recalculateCache() {
        if(this.variants.isEmpty()){
            this.minPrice = BigDecimal.ZERO;
            this.maxPrice = BigDecimal.ZERO;
            this.totalStock = 0;
            return;
        }

        this.minPrice = this.variants.stream()
                .map(ProductVariant::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        this.maxPrice = this.variants.stream()
                .map(ProductVariant::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        this.totalStock = this.variants.stream()
                .mapToInt(ProductVariant::getStockQuantity)
                .sum();
    }

    public List<ProductVariant> getVariants(){
        return Collections.unmodifiableList(variants);
    }

    public void approve(){
        if(this.status != ProductStatus.PENDING_REVIEW){
            throw new IllegalStateException("Chỉ có thể duyệt sản phẩm đang chờ xét đuyệt");
        }
        this.status = ProductStatus.ON_SHELF;
        this.rejectionReason = null;
    }

    public void reject(String reason){
        if(this.status != ProductStatus.PENDING_REVIEW){
            throw new IllegalStateException("Chỉ có thể từ chối sản phẩm đang chờ xét duyệt");
        }
        if(reason == null || reason.isBlank()){
            throw new IllegalArgumentException("Phải cung cấp lý do từ chối cho seller");
        }
        this.status = ProductStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public void resubmit(){
        if(this.status != ProductStatus.REJECTED && this.status != ProductStatus.DELETED){
            throw new IllegalStateException("Chỉ có thể gửi duyệt lại sản phẩm bị từ chối hoặc đã xóa");
        }
        this.status = ProductStatus.PENDING_REVIEW;
        this.rejectionReason = null;
    }

    public void freeze(String reason){
        if(this.status != ProductStatus.ON_SHELF){
            throw new IllegalStateException("Chỉ có thể đóng băng sản phẩm đã lên kệ bị vi phạm");
        }
        if(reason == null || reason.isBlank()){
            throw new IllegalArgumentException("Phải cung cấp lý do đóng băng cho seller");
        }
        this.status = ProductStatus.FROZEN;
        this.rejectionReason = reason;
    }

    public void unfreeze(String reason){
        if(reason == null || reason.isBlank()){
            throw new IllegalArgumentException("Phải cung cấp lý do gỡ đóng băng cho seller");
        }
        this.status = ProductStatus.ON_SHELF;
        this.rejectionReason = reason;
    }

    public void sellerDelete(){
        if(this.status == ProductStatus.FROZEN){
            throw new IllegalArgumentException("Sản phẩm đang bị khóa do vi phạm không thể tự xóa");
        }
        this.status = ProductStatus.DELETED;
    }

    public void restore(){
        if(status == ProductStatus.FROZEN){
            throw new IllegalStateException("Sản phẩm đang bị khóa do vi phạm không thể tự xóa");
        }
        if(status == ProductStatus.DELETED){
            this.status = ProductStatus.PENDING_REVIEW;
        }
    }
}
