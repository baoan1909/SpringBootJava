package com.example.product.domain.model;
import com.example.product.domain.common.Default;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Product {
    private final Long id;
    private final String sku;
    private String name;
    private BigDecimal price;

    private final List<ProductVariant> variants = new ArrayList<>();

    public Product(Long id, String sku, String name, BigDecimal price) {
        if (sku == null || sku.isBlank()){
            throw new IllegalArgumentException("SKU không được để trống");
        }

        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name không được để trống");
        }

        if ( price == null || price.compareTo(BigDecimal.ZERO) <= 0 ){
            throw new IllegalArgumentException("Price phải lớn hơn 0 đồng");
        }

        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    @Default
    public Product(Long id, String sku, String name, BigDecimal price, List<ProductVariant> variants) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        if(variants != null){
            this.variants.addAll(variants);
        }
    }

    public void updateInfo(String name, BigDecimal price) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name không được để trống");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0 ){
            throw new IllegalArgumentException("Price phải lớn hơn 0 đồng");
        }

        this.name = name;
        this.price = price;
    }

    public void addVariant(List<String> colors, List<String> sizes, BigDecimal additionalPrice) {
        if ((colors == null || colors.isEmpty()) && (sizes == null || sizes.isEmpty())){
            return;
        }

        int variantsToAdd = colors.size() * sizes.size();
        if(this.variants.size() + variantsToAdd > 50){
            throw new IllegalArgumentException(String.format("Sản phẩm không được vượt quá 50 phiên bản"));
        }

        for(String color: colors){
            for ( String size: sizes ){
                boolean exists = this.variants.stream()
                        .anyMatch(v -> v.getColor().equalsIgnoreCase(color) && v.getSize().equalsIgnoreCase(size));
                if (exists){
                    throw new IllegalArgumentException(String.format("Phiên bản %s - %s đã tồn tại", color, size));
                }

                this.variants.add(new ProductVariant(null, color, size, additionalPrice));
            }
        }
    }

    public void updateVariantPrice(String color, String size, BigDecimal additionalPrice) {
        ProductVariant variant = this.variants.stream()
                .filter(v -> v.getColor().equalsIgnoreCase(color) && v.getSize().equalsIgnoreCase(size))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Không tìm thấy phiên bản %s - %s để cập nhật ", color, size)));
        variant.updateAdditionalPrice(additionalPrice);
    }

    public void removeVariantsByCriteria(String color, String size) {
        List<ProductVariant> variantsToRemove = this.variants.stream()
                .filter(v -> color == null || v.getColor().equalsIgnoreCase(color))
                .filter(v -> size == null || v.getSize().equalsIgnoreCase(size))
                .toList();

        if (variantsToRemove.isEmpty()){
            throw new IllegalArgumentException("Không tìm thấy phiên bản khớp tiêu chí để xóa");
        }
        this.variants.removeAll(variantsToRemove);
    }

    public List<ProductVariant> getVariants() {
        return Collections.unmodifiableList(variants);
    }
}
