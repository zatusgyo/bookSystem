package com.bookSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookSystem.entity.Category;
import com.bookSystem.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取所有启用分类（树形结构）
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取父分类下的子分类
     */
    List<Category> getChildCategories(Long parentId);

    /**
     * 添加分类
     */
    Category addCategory(Category category);

    /**
     * 更新分类
     */
    boolean updateCategory(Category category);
}
