package com.thientri.book_area.service.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.catalog.CategoryRequest;
import com.thientri.book_area.dto.response.catalog.CategoryResponse;
import com.thientri.book_area.model.catalog.Category;
import com.thientri.book_area.repository.catalog.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> list = new ArrayList<>();
        for (Category ct : categoryRepository.findAll()) {
            list.add(mapToResponse(ct));
        }
        return list;
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category newCategory = new Category();
        newCategory.setName(categoryRequest.getName());
        return mapToResponse(categoryRepository.save(newCategory));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay danh muc de cap nhat"));
        category.setName(categoryRequest.getName());
        return mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse deleteCategory(Long id) {
        Category deletedCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay danh muc de xoa"));
        categoryRepository.deleteById(id);
        return mapToResponse(deletedCategory);
    }
}
