package com.bookSystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅记录 VO（含图书信息）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String borrowNo;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private String bookCover;
    private String borrowMode;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Integer renewCount;
    private String status;
    private BigDecimal borrowFee;
    private BigDecimal overdueFine;
}
