package com.finance.personalfinancemanager.controller;


import com.finance.personalfinancemanager.dto.category_dto.CategoryRequest;
import com.finance.personalfinancemanager.dto.category_dto.CategoryResponse;
import com.finance.personalfinancemanager.enums.CategoryType;
import com.finance.personalfinancemanager.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    //create category (Protected - requires JWT)
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request,
                                            HttpServletRequest httpRequest) {
        try {
            // Get userId from JWT
            Long userId = (Long) httpRequest.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }

            CategoryResponse response = categoryService.createCategory(userId, request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Get all categories (Protected - requires JWT)
    @GetMapping
    public ResponseEntity<?> getAllCategories(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            System.out.println(userId);
            if (userId == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }

            List<CategoryResponse> categories = categoryService.getAllCategories(userId);
            return ResponseEntity.ok(categories);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Get categories by type (Protected - requires JWT)
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getCategoriesByType(
            @PathVariable CategoryType type,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }

            List<CategoryResponse> categories = categoryService.getCategoriesByType(userId, type);
            return ResponseEntity.ok(categories);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}

