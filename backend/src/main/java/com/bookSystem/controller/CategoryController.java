package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.entity.Category;
import com.bookSystem.service.CategoryService;
import com.bookSystem.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Tag(name = "图书分类", description = "图书分类的增删改查")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<CategoryVO>> getCategoryTree() {
        List<CategoryVO> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取子分类")
    @GetMapping("/children")
    public Result<List<Category>> getChildCategories(@RequestParam(defaultValue = "0") Long parentId) {
        List<Category> categories = categoryService.getChildCategories(parentId);
        return Result.success(categories);
    }

    @Operation(summary = "获取所有分类（平级）")
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @Operation(summary = "添加分类")
    @PostMapping
    public Result<Category> addCategory(@RequestBody Category category) {
        Category saved = categoryService.addCategory(category);
        return Result.success("添加成功", saved);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        // 检查是否有子分类
        List<Category> children = categoryService.getChildCategories(id);
        if (!children.isEmpty()) {
            return Result.error("该分类下存在子分类，无法删除");
        }
        categoryService.removeById(id);
        return Result.success("删除成功");
    }
}
