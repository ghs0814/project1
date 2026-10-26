package com.example.project1;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseCheckRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {

        String database = jdbcTemplate.queryForObject(
                "SELECT current_database()", String.class
        );

        String username = jdbcTemplate.queryForObject(
                "SELECT current_user", String.class
        );

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products", Long.class
        );

        System.out.println("Database: " + database);
        System.out.println("Username: " + username);
        System.out.println("Product count: " + count);
    }
}