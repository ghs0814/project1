package com.example.project1.dto;

import com.example.project1.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponseDto {

    private Long productId;
    private String productName;
    private Integer price;
    private Integer quantity;

    public static ProductResponseDto from(Product product) {

        return new ProductResponseDto(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}