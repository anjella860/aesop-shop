package com.aesop.shop.service;

import com.aesop.shop.entity.Category;
import com.aesop.shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 전체 카테고리 목록
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // 대분류 카테고리 목록
    public List<Category> findParentCategories() {
        return categoryRepository.findByParentIdIsNull();
    }

    // 소분류 카테고리 목록
    public List<Category> findSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    // 카테고리 등록 (관리자)
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    // 카테고리 수정 (관리자)
    public Category updateCategory(Long id, Category updateCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리가 존재하지 않습니다."));
        category.setName(updateCategory.getName());
        category.setDescription(updateCategory.getDescription());
        return categoryRepository.save(category);
    }

    // 카테고리 삭제 (관리자)
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}