b CREATE DATABASE IF NOT EXISTS library_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_system;

DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS borrow_order;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，主键，自增',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱，唯一',
    phone VARCHAR(20) COMMENT '手机号',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0普通用户，1管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID，主键，自增',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id INT DEFAULT 0 COMMENT '父分类ID，0为顶级分类',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_name_parent (name, parent_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

CREATE TABLE book (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID，主键，自增',
    isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN号，唯一',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) NOT NULL COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    category_id INT NOT NULL COMMENT '分类ID，外键',
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    cover VARCHAR(255) COMMENT '封面图片URL',
    description TEXT COMMENT '图书简介',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_isbn (isbn),
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_category_id (category_id),
    INDEX idx_stock (stock),
    CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

CREATE TABLE borrow_order (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID，主键，自增',
    user_id INT NOT NULL COMMENT '用户ID，外键',
    book_id INT NOT NULL COMMENT '图书ID，外键',
    borrow_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅日期',
    due_date DATETIME NOT NULL COMMENT '应还日期',
    return_date DATETIME COMMENT '实际归还日期',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0借阅中，1已归还，2逾期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    CONSTRAINT fk_borrow_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅订单表';

CREATE TABLE purchase_order (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID，主键，自增',
    user_id INT NOT NULL COMMENT '用户ID，外键',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号，唯一',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    pay_method VARCHAR(20) COMMENT '支付方式：支付宝、微信',
    pay_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0未支付，1已支付，2已退款',
    logistics_info VARCHAR(255) COMMENT '物流信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time DATETIME COMMENT '支付时间',
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_pay_status (pay_status),
    INDEX idx_create_time (create_time),
    CONSTRAINT fk_purchase_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购买订单表';

CREATE TABLE order_detail (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '详情ID，主键，自增',
    order_id INT NOT NULL COMMENT '订单ID，外键',
    book_id INT NOT NULL COMMENT '图书ID，外键',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小计',
    INDEX idx_order_id (order_id),
    INDEX idx_book_id (book_id),
    CONSTRAINT fk_order_detail_order FOREIGN KEY (order_id) REFERENCES purchase_order(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_order_detail_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单详情表';

CREATE TABLE review (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID，主键，自增',
    user_id INT NOT NULL COMMENT '用户ID，外键',
    book_id INT NOT NULL COMMENT '图书ID，外键',
    rating TINYINT NOT NULL DEFAULT 5 COMMENT '评分（1-5分）',
    content TEXT COMMENT '评价内容',
    review_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态：0待审核，1已通过，2已拒绝',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_rating (rating),
    INDEX idx_audit_status (audit_status),
    INDEX idx_review_time (review_time),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_review_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

CREATE TABLE cart (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID，主键，自增',
    user_id INT NOT NULL COMMENT '用户ID，外键',
    book_id INT NOT NULL COMMENT '图书ID，外键',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    add_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    UNIQUE KEY uk_user_book (user_id, book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cart_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';
