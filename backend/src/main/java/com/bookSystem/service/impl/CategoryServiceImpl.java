package com.bookSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.Category;
import com.bookSystem.mapper.CategoryMapper;
import com.bookSystem.service.CategoryService;
import com.bookSystem.vo.CategoryVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 查询所有启用分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1)
               .orderByAsc(Category::getSortOrder);
        List<Category> allCategories = this.list(wrapper);

        // 构建树形结构
        Map<Long, List<Category>> parentIdMap = allCategories.stream()
                .collect(Collectors.groupingBy(Category::getParentId));

        // 获取顶级分类并递归构建子分类
        List<Category> topCategories = parentIdMap.getOrDefault(0L, new ArrayList<>());
        return topCategories.stream()
                .map(cat -> buildCategoryVO(cat, parentIdMap))
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> getChildCategories(Long parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId)
               .eq(Category::getStatus, 1)
               .orderByAsc(Category::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public Category addCategory(Category category) {
        // 检查分类名是否已存在
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName())
               .eq(Category::getParentId, category.getParentId() != null ? category.getParentId() : 0);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("该分类名称已存在");
        }

        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        this.save(category);
        return category;
    }

    @Override
    public boolean updateCategory(Category category) {
        Category existing = this.getById(category.getId());
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }
        return this.updateById(category);
    }

    /**
     * 递归构建分类 VO
     */
    private CategoryVO buildCategoryVO(Category category, Map<Long, List<Category>> parentIdMap) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setParentId(category.getParentId());
        vo.setSortOrder(category.getSortOrder());
        vo.setStatus(category.getStatus());

        List<Category> children = parentIdMap.getOrDefault(category.getId(), new ArrayList<>());
        if (!children.isEmpty()) {
            vo.setChildren(children.stream()
                    .map(child -> buildCategoryVO(child, parentIdMap))
                    .collect(Collectors.toList()));
        } else {
            vo.setChildren(new ArrayList<>());
        }

        return vo;
    }
}
