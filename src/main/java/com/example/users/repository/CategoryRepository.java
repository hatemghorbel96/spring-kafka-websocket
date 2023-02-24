package com.example.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.users.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {}

