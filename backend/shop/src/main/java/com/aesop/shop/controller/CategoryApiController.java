package com.aesop.shop.controller;

import com.aesop.shop.entity.Category;
import com.aesop.shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryApiController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/parents")
    public ResponseEntity<List<Category>> getParentCategories() {
        return ResponseEntity.ok(categoryService.findParentCategories());
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<Category>> getChildren(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.findSubCategories(parentId));
    }
}
