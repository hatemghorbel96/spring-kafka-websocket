package com.example.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.users.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}



