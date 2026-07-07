-- ============================================================
-- 图书借阅与销售平台 - 数据库初始化脚本
-- Database: MySQL 8.0+
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS book_system
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE book_system;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '用户ID',
    username        VARCHAR(50)     NOT NULL UNIQUE              COMMENT '用户名',
    password        VARCHAR(128)    NOT NULL                     COMMENT '密码（MD5加密）',
    nickname        VARCHAR(50)                                  COMMENT '昵称',
    email           VARCHAR(100)                                 COMMENT '邮箱',
    phone           VARCHAR(20)                                  COMMENT '手机号',
    avatar          VARCHAR(255)                                 COMMENT '头像URL',
    role            VARCHAR(20)     DEFAULT 'MEMBER'             COMMENT '角色: ADMIN/MEMBER/VIP',
    status          TINYINT         DEFAULT 0                    COMMENT '状态: 0-正常, 1-禁用',
    max_borrow_count INT            DEFAULT 5                    COMMENT '最大可借数量',
    current_borrow_count INT        DEFAULT 0                    COMMENT '当前借阅数量',
    is_deleted      TINYINT         DEFAULT 0                    COMMENT '逻辑删除: 0-未删除, 1-已删除',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 图书分类表
-- ============================================================
DROP TABLE IF EXISTS tb_category;
CREATE TABLE tb_category (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '分类ID',
    name            VARCHAR(50)     NOT NULL                     COMMENT '分类名称',
    parent_id       BIGINT          DEFAULT 0                    COMMENT '父分类ID（0=顶级分类）',
    sort_order      INT             DEFAULT 0                    COMMENT '排序号',
    status          TINYINT         DEFAULT 1                    COMMENT '状态: 0-禁用, 1-启用',
    is_deleted      TINYINT         DEFAULT 0                    COMMENT '逻辑删除',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

-- ============================================================
-- 3. 图书表
-- ============================================================
DROP TABLE IF EXISTS tb_book;
CREATE TABLE tb_book (
    id                  BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '图书ID',
    isbn                VARCHAR(20)                                  COMMENT 'ISBN编号',
    title               VARCHAR(200)    NOT NULL                     COMMENT '书名',
    author              VARCHAR(100)                                 COMMENT '作者',
    publisher           VARCHAR(100)                                 COMMENT '出版社',
    publish_date        DATE                                         COMMENT '出版日期',
    category_id         BIGINT                                       COMMENT '分类ID',
    description         TEXT                                         COMMENT '图书简介',
    cover_image         VARCHAR(255)                                 COMMENT '封面图片URL',
    total_stock         INT             DEFAULT 0                    COMMENT '总库存',
    available_stock     INT             DEFAULT 0                    COMMENT '可借/可售数量',
    sale_price          DECIMAL(10,2)   DEFAULT 0.00                 COMMENT '销售价格',
    borrow_price_per_day DECIMAL(10,2)  DEFAULT 0.00                 COMMENT '借阅价格（元/天）',
    borrow_mode         VARCHAR(20)     DEFAULT 'SINGLE'             COMMENT '借阅模式: SINGLE/MULTI',
    max_co_read_count   INT             DEFAULT 1                    COMMENT '最大共读人数',
    status              TINYINT         DEFAULT 1                    COMMENT '状态: 0-下架, 1-上架, 2-缺货',
    view_count          BIGINT          DEFAULT 0                    COMMENT '浏览次数',
    borrow_count        BIGINT          DEFAULT 0                    COMMENT '借阅次数',
    is_deleted          TINYINT         DEFAULT 0                    COMMENT '逻辑删除',
    create_time         DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_category_id (category_id),
    INDEX idx_isbn (isbn),
    FULLTEXT INDEX ft_title_desc (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- ============================================================
-- 4. 借阅记录表
-- ============================================================
DROP TABLE IF EXISTS tb_borrow_record;
CREATE TABLE tb_borrow_record (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '记录ID',
    borrow_no       VARCHAR(30)     NOT NULL UNIQUE              COMMENT '借阅编号',
    user_id         BIGINT          NOT NULL                     COMMENT '用户ID',
    book_id         BIGINT          NOT NULL                     COMMENT '图书ID',
    borrow_mode     VARCHAR(20)     DEFAULT 'SINGLE'             COMMENT '借阅模式: SINGLE/MULTI',
    borrow_date     DATETIME                                     COMMENT '借阅日期',
    due_date        DATETIME                                     COMMENT '应还日期',
    return_date     DATETIME                                     COMMENT '实际归还日期',
    renew_count     INT             DEFAULT 0                    COMMENT '续借次数',
    status          VARCHAR(20)     DEFAULT 'BORROWING'          COMMENT '状态: BORROWING/RETURNED/OVERDUE',
    borrow_fee      DECIMAL(10,2)   DEFAULT 0.00                 COMMENT '借阅费用',
    overdue_fine    DECIMAL(10,2)   DEFAULT 0.00                 COMMENT '逾期罚款',
    is_deleted      TINYINT         DEFAULT 0                    COMMENT '逻辑删除',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_no (borrow_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- ============================================================
-- 5. 订单表
-- ============================================================
DROP TABLE IF EXISTS tb_order;
CREATE TABLE tb_order (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '订单ID',
    order_no        VARCHAR(30)     NOT NULL UNIQUE              COMMENT '订单编号',
    user_id         BIGINT          NOT NULL                     COMMENT '用户ID',
    total_amount    DECIMAL(10,2)   DEFAULT 0.00                 COMMENT '订单总金额',
    paid_amount     DECIMAL(10,2)   DEFAULT 0.00                 COMMENT '实付金额',
    payment_method  VARCHAR(20)                                  COMMENT '支付方式: ALIPAY/WECHAT',
    payment_status  VARCHAR(20)     DEFAULT 'UNPAID'             COMMENT '支付状态: UNPAID/PAID/REFUNDING/REFUNDED',
    payment_time    DATETIME                                     COMMENT '支付时间',
    payment_trade_no VARCHAR(64)                                  COMMENT '支付交易流水号',
    order_status    VARCHAR(20)     DEFAULT 'PENDING'            COMMENT '订单状态: PENDING/PROCESSING/SHIPPED/DELIVERED/CANCELLED',
    receiver_name   VARCHAR(50)                                  COMMENT '收货人姓名',
    receiver_phone  VARCHAR(20)                                  COMMENT '收货人手机号',
    receiver_address VARCHAR(255)                                COMMENT '收货地址',
    remark          VARCHAR(500)                                 COMMENT '备注',
    is_deleted      TINYINT         DEFAULT 0                    COMMENT '逻辑删除',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_order_status (order_status),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================================
-- 6. 订单项表
-- ============================================================
DROP TABLE IF EXISTS tb_order_item;
CREATE TABLE tb_order_item (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '订单项ID',
    order_id        BIGINT          NOT NULL                     COMMENT '订单ID',
    book_id         BIGINT          NOT NULL                     COMMENT '图书ID',
    book_title      VARCHAR(200)                                 COMMENT '图书名称（冗余）',
    book_cover      VARCHAR(255)                                 COMMENT '图书封面（冗余）',
    quantity        INT             DEFAULT 1                    COMMENT '购买数量',
    unit_price      DECIMAL(10,2)   DEFAULT 0.00                 COMMENT '单价',
    subtotal        DECIMAL(10,2)   DEFAULT 0.00                 COMMENT '小计',
    is_deleted      TINYINT         DEFAULT 0                    COMMENT '逻辑删除',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- ============================================================
-- 7. 支付记录表（模拟支付流水）
-- ============================================================
DROP TABLE IF EXISTS tb_payment_record;
CREATE TABLE tb_payment_record (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '记录ID',
    order_id        BIGINT          NOT NULL                     COMMENT '订单ID',
    order_no        VARCHAR(30)                                  COMMENT '订单编号（冗余）',
    amount          DECIMAL(10,2)   NOT NULL                     COMMENT '支付金额',
    payment_method  VARCHAR(20)                                  COMMENT '支付方式: ALIPAY/WECHAT',
    trade_no        VARCHAR(64)     NOT NULL UNIQUE              COMMENT '交易流水号',
    status          VARCHAR(20)     DEFAULT 'PROCESSING'         COMMENT '支付状态: PROCESSING/SUCCESS/FAILED',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '支付发起时间',
    complete_time   DATETIME                                     COMMENT '支付完成时间',
    INDEX idx_order_id (order_id),
    INDEX idx_trade_no (trade_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 插入默认分类
INSERT INTO tb_category (name, parent_id, sort_order) VALUES
('计算机科学', 0, 1),
('文学小说', 0, 2),
('历史哲学', 0, 3),
('经济管理', 0, 4),
('科学技术', 0, 5),
('教育考试', 0, 6),
('艺术设计', 0, 7),
('生活百科', 0, 8);

-- 插入子分类
INSERT INTO tb_category (name, parent_id, sort_order) VALUES
('编程语言', 1, 1),
('数据结构', 1, 2),
('人工智能', 1, 3),
('中国文学', 2, 1),
('外国文学', 2, 2),
('中国古代史', 3, 1),
('世界历史', 3, 2);

-- 插入测试管理员
INSERT INTO tb_user (username, password, nickname, role, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'ADMIN', 0);

-- ============================================================
-- 8. 图书评论表
-- ============================================================
DROP TABLE IF EXISTS tb_comment;
CREATE TABLE tb_comment (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '评论ID',
    book_id         BIGINT          NOT NULL                     COMMENT '图书ID',
    user_id         BIGINT          NOT NULL                     COMMENT '用户ID',
    username        VARCHAR(50)                                  COMMENT '用户名（冗余）',
    rating          TINYINT         NOT NULL DEFAULT 5           COMMENT '评分（1-5）',
    content         TEXT                                         COMMENT '评论内容',
    is_deleted      TINYINT         DEFAULT 0                    COMMENT '逻辑删除',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    INDEX idx_book_id (book_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书评论表';

-- ============================================================
-- 9. 物流轨迹表
-- ============================================================
DROP TABLE IF EXISTS tb_shipping_track;
CREATE TABLE tb_shipping_track (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '轨迹ID',
    order_id        BIGINT          NOT NULL                     COMMENT '订单ID',
    order_no        VARCHAR(30)                                  COMMENT '订单编号（冗余）',
    location        VARCHAR(255)                                 COMMENT '物流节点',
    description     VARCHAR(255)                                 COMMENT '物流描述',
    track_time      DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '轨迹时间',
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流轨迹表';

-- 插入测试图书
INSERT INTO tb_book (isbn, title, author, publisher, category_id, description, total_stock, available_stock, sale_price, borrow_mode) VALUES
('978-7-111-11111-1', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', 9, 'Java经典入门书籍，全面讲解Java语言核心概念', 50, 50, 79.00, 'SINGLE'),
('978-7-111-22222-2', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', 9, '从程序员视角深入理解计算机系统', 30, 30, 99.00, 'SINGLE'),
('978-7-111-33333-3', '活着', '余华', '作家出版社', 11, '讲述了一个人一生的故事，展现了中国历史的变迁', 100, 100, 35.00, 'MULTI'),
('978-7-111-44444-4', '百年孤独', '加西亚·马尔克斯', '南海出版公司', 12, '魔幻现实主义代表作，讲述布恩迪亚家族的故事', 80, 80, 45.00, 'SINGLE'),
('978-7-111-55555-5', '万历十五年', '黄仁宇', '中华书局', 13, '以万历十五年为切入点，分析明朝历史的转折', 40, 40, 29.00, 'SINGLE');
