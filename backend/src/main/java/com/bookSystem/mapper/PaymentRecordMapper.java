package com.bookSystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookSystem.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录 Mapper
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
}
