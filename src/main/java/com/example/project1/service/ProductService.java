package com.example.project1.service;

import com.example.project1.domain.Product;
import com.example.project1.dto.ProductRequestDto;
import com.example.project1.dto.ProductResponseDto;
import com.example.project1.exception.ProductNotFoundException;
import com.example.project1.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public List<ProductResponseDto> findAll() {

        return productMapper.findAll()
                .stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    public ProductResponseDto findById(Long productId) {

        Product product = productMapper.findById(productId);

        if (product == null) {
            throw new ProductNotFoundException(productId);
        }

        return ProductResponseDto.from(product);
    }

    public ProductResponseDto create(ProductRequestDto request) {

        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        productMapper.insert(product);

        return ProductResponseDto.from(product);
    }

    public ProductResponseDto update(
            Long productId,
            ProductRequestDto request) {

        Product product = new Product();

        product.setProductId(productId);
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        int updatedRows = productMapper.update(product);

        if (updatedRows == 0) {
            throw new ProductNotFoundException(productId);
        }

        return ProductResponseDto.from(product);
    }

    public void delete(Long productId) {

        int deletedRows = productMapper.deleteById(productId);

        if (deletedRows == 0) {
            throw new ProductNotFoundException(productId);
        }
    }
}