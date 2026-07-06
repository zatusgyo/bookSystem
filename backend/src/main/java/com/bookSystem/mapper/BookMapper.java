package com.bookSystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookSystem.entity.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书 Mapper
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
