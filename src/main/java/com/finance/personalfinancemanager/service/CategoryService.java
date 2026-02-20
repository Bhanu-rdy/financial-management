package com.finance.personalfinancemanager.service;


import com.finance.personalfinancemanager.dto.category_dto.CategoryRequest;
import com.finance.personalfinancemanager.dto.category_dto.CategoryResponse;
import com.finance.personalfinancemanager.entity.Category;
import com.finance.personalfinancemanager.entity.User;
import com.finance.personalfinancemanager.enums.CategoryType;
import com.finance.personalfinancemanager.repository.CategoryRepository;
import com.finance.personalfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    //Create Category
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {

        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        Category category = new Category();
        category.setUser(user);
        category.setName(request.getName());
        category.setType(request.getType());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getType(),
                savedCategory.getCreatedAt().toString()
        );
    }

    //Get all Categories for user
    public List<CategoryResponse> getAllCategories(Long userId) {

        List<Category> categories = categoryRepository.findByUserId(userId);

        List<CategoryResponse> responseList = new ArrayList<>();

        for(Category category: categories) {
            CategoryResponse response = new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getType(),
                    category.getCreatedAt().toString()
            );
            responseList.add(response);
        }
        return responseList;
    }

    //get categories by type
    public List<CategoryResponse> getCategoriesByType(Long userId, CategoryType type) {

        List<Category> categories = categoryRepository.findByUserIdAndType(userId, type);


        List<CategoryResponse> responseList = new ArrayList<>();
        for(Category category : categories) {
            CategoryResponse response = new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getType(),
                    category.getCreatedAt().toString()
            );
            responseList.add(response);
        }
        return responseList;
    }

    
}
