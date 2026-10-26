package com.example.project1.mapper;

import com.example.project1.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> findAll();

    Product findById(@Param("productId") Long productId);

    int insert(Product product);

    int update(Product product);

    int deleteById(@Param("productId") Long productId);
}