package com.example.project1.controller;

import com.example.project1.dto.ProductRequestDto;
import com.example.project1.dto.ProductResponseDto;
import com.example.project1.exception.ProductNotFoundException;
import com.example.project1.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // 실제 객체
    private ProductService productService;

    @Test
    void findAll_shouldReturnProducts() throws Exception {

        // given
        ProductResponseDto product = new ProductResponseDto(
                1L, "노트북", 950000, 5
        );

        given(productService.findAll())
                .willReturn(List.of(product));

        // when & then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("노트북"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {

        // given
        ProductResponseDto response = new ProductResponseDto(
                4L, "모니터", 180000, 4
        );

        given(productService.create(any(ProductRequestDto.class)))
                .willReturn(response);

        String json = """
                {
                  "productName": "모니터",
                  "price": 180000,
                  "quantity": 4
                }
                """;

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(4))
                .andExpect(jsonPath("$.productName").value("모니터"));
    }

    @Test
    void create_shouldReturnBadRequestWhenInvalid() throws Exception {

        // given
        String json = """
                {
                  "productName": "",
                  "price": -1000,
                  "quantity": -5
                }
                """;

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void findById_shouldReturnNotFound() throws Exception {

        // given
        given(productService.findById(999L))
                .willThrow(new ProductNotFoundException(999L));

        // when & then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("상품을 찾을 수 없습니다. ID: 999"));
    }
}