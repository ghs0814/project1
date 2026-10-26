package com.example.project1;

import com.example.project1.domain.Product;
import com.example.project1.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductCheckRunner implements CommandLineRunner {

    private final ProductMapper productMapper;

    @Override
    public void run(String... args) {

        List<Product> products = productMapper.findAll();

        for (Product product : products) {
            System.out.println(
                    product.getProductId() + " / " +
                            product.getProductName() + " / " +
                            product.getPrice() + " / " +
                            product.getQuantity()
            );
        }
    }
}