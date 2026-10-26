package com.example.project1.service;

import com.example.project1.domain.Product;
import com.example.project1.dto.ProductRequestDto;
import com.example.project1.dto.ProductResponseDto;
import com.example.project1.exception.ProductNotFoundException;
import com.example.project1.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    //가짜 Mock 객체
    @Mock
    private ProductMapper productMapper;

    //가짜 Mock 객체를 주입하는 대상
    @InjectMocks
    private ProductService productService;

    @Test
    void findById_shouldReturnProduct() {

        // given
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("노트북");
        product.setPrice(950000);
        product.setQuantity(5);

        given(productMapper.findById(1L)).willReturn(product);
        //service에서 사용되는 mapper 객체가 반환할 값을 생성해서 지정.

        // when
        ProductResponseDto result = productService.findById(1L);
        //test대상인 service의 메서드를 테스트, 메서드 내부에서 사용되는 mapper객체는 지정된 값을 반환

        // then
        assertEquals(1L, result.getProductId());
        assertEquals("노트북", result.getProductName());
        assertEquals(950000, result.getPrice());
        assertEquals(5, result.getQuantity());

        verify(productMapper).findById(1L);
        //mapper의 특정 메서드(매개변수)가 실행되었는지 검증
    }

    @Test
    void findById_shouldThrowWhenProductNotFound() {

        // given
        given(productMapper.findById(999L)).willReturn(null);

        // when
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.findById(999L)
        );
        //예외를 발생시켜서 일치하는지 테스트하고, 그 예외를 변수에 저장
        //assertThrows(expected, ()->)

        // then
        assertEquals(
                "상품을 찾을 수 없습니다. ID: 999",
                exception.getMessage()
        );
        // 저장한 예외의 저장된 메세지를 assertEquals로 테스트
    }

    @Test
    void update_shouldReturnUpdatedProduct() {

        // given
        ProductRequestDto request = new ProductRequestDto();
        request.setProductName("게이밍 노트북");
        request.setPrice(1200000);
        request.setQuantity(8);

        given(productMapper.update(any(Product.class))).willReturn(1);

        // when
        ProductResponseDto result = productService.update(1L, request);

        // then
        assertEquals(1L, result.getProductId());
        assertEquals("게이밍 노트북", result.getProductName());
        assertEquals(1200000, result.getPrice());
        assertEquals(8, result.getQuantity());

        verify(productMapper).update(any(Product.class));
    }

    @Test
    void delete_shouldCallMapper() {

        // given
        given(productMapper.deleteById(1L)).willReturn(1);

        // when
        productService.delete(1L);

        // then
        verify(productMapper).deleteById(1L);
    }
}