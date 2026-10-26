package com.example.project1.controller;

import com.example.project1.dto.ProductRequestDto;
import com.example.project1.dto.ProductResponseDto;
import com.example.project1.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponseDto> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{productId}")
    public ProductResponseDto findById(
            @PathVariable("productId") Long productId) {

        return productService.findById(productId);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(
            @Valid @RequestBody ProductRequestDto request) {

        ProductResponseDto response = productService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{productId}")
    public ProductResponseDto update(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody ProductRequestDto request) {

        return productService.update(productId, request);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(
            @PathVariable("productId") Long productId) {

        productService.delete(productId);

        return ResponseEntity.noContent().build();
    }
}