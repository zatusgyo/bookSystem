package com.bookSystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅记录实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_borrow_record")
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 借阅编号（业务编号，如 BR202401010001） */
    private String borrowNo;

    /** 用户ID */
    private Long userId;

    /** 图书ID */
    private Long bookId;

    /** 借阅模式: SINGLE-单人借阅, MULTI-多人共读 */
    private String borrowMode;

    /** 借阅日期 */
    private LocalDateTime borrowDate;

    /** 应还日期 */
    private LocalDateTime dueDate;

    /** 实际归还日期 */
    private LocalDateTime returnDate;

    /** 续借次数 */
    private Integer renewCount;

    /** 借阅状态: BORROWING-借阅中, RETURNED-已归还, OVERDUE-已逾期 */
    private String status;

    /** 借阅费用 */
    private BigDecimal borrowFee;

    /** 逾期罚款 */
    private BigDecimal overdueFine;

    /** 逻辑删除 */
    @TableLogic
    private Integer isDeleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
