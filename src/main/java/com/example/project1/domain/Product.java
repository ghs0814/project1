package com.example.project1.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private Long productId;
    private String productName;
    private Integer price;
    private Integer quantity;
}