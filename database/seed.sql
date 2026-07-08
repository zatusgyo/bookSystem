-- ============================================================
-- 测试种子数据 — 基于组长的 data.sql 转换
-- 兼容 book_system / tb_xxx 表结构
-- 执行: mysql -u root -p'=' book_system < database/seed.sql
-- ============================================================

USE book_system;

-- ============================================================
-- 1. 补充用户（密码均为 md5('123456')）
-- ============================================================
INSERT IGNORE INTO tb_user (username, password, nickname, email, phone, role, status, max_borrow_count, current_borrow_count) VALUES
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@example.com', '13800138001', 'MEMBER', 0, 5, 0),
('lisi',     'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@example.com',     '13800138002', 'MEMBER', 0, 5, 0),
('wangwu',   'e10adc3949ba59abbe56e057f20f883e', '王五', 'wangwu@example.com',   '13800138003', 'MEMBER', 0, 5, 0),
('zhaoliu',  'e10adc3949ba59abbe56e057f20f883e', '赵六', 'zhaoliu@example.com',  '13800138004', 'MEMBER', 0, 5, 0),
('sunqi',    'e10adc3949ba59abbe56e057f20f883e', '孙七', 'sunqi@example.com',    '13800138005', 'MEMBER', 0, 5, 0),
('zhouba',   'e10adc3949ba59abbe56e057f20f883e', '周八', 'zhouba@example.com',   '13800138006', 'MEMBER', 0, 5, 0),
('wujiu',    'e10adc3949ba59abbe56e057f20f883e', '吴九', 'wujiu@example.com',    '13800138007', 'MEMBER', 0, 5, 0),
('zhengshi', 'e10adc3949ba59abbe56e057f20f883e', '郑十', 'zhengshi@example.com', '13800138008', 'MEMBER', 0, 5, 0),
('liuhan',   'e10adc3949ba59abbe56e057f20f883e', '刘涵', 'liuhan@example.com',   '13800138009', 'MEMBER', 0, 5, 0),
('chenyue',  'e10adc3949ba59abbe56e057f20f883e', '陈悦', 'chenyue@example.com',  '13800138010', 'MEMBER', 0, 5, 0),
('yangfan',  'e10adc3949ba59abbe56e057f20f883e', '杨帆', 'yangfan@example.com',  '13800138011', 'MEMBER', 0, 5, 0),
('huangping','e10adc3949ba59abbe56e057f20f883e', '黄萍', 'huangping@example.com','13800138012', 'MEMBER', 0, 5, 0),
('linjie',   'e10adc3949ba59abbe56e057f20f883e', '林杰', 'linjie@example.com',   '13800138013', 'MEMBER', 0, 5, 0),
('xuwei',    'e10adc3949ba59abbe56e057f20f883e', '徐伟', 'xuwei@example.com',    '13800138014', 'MEMBER', 0, 5, 0),
('sunli',    'e10adc3949ba59abbe56e057f20f883e', '孙丽', 'sunli@example.com',    '13800138015', 'MEMBER', 0, 5, 0),
('maolin',   'e10adc3949ba59abbe56e057f20f883e', '毛琳', 'maolin@example.com',   '13800138016', 'MEMBER', 0, 5, 0),
('gaoyi',    'e10adc3949ba59abbe56e057f20f883e', '高一', 'gaoyi@example.com',    '13800138017', 'MEMBER', 0, 5, 0),
('luqing',   'e10adc3949ba59abbe56e057f20f883e', '陆晴', 'luqing@example.com',   '13800138018', 'MEMBER', 0, 5, 0),
('songhai',  'e10adc3949ba59abbe56e057f20f883e', '宋海', 'songhai@example.com',  '13800138019', 'MEMBER', 1, 5, 0);

-- ============================================================
-- 2. 补充分类（追加到现有分类后，ID从15开始）
-- ============================================================
INSERT IGNORE INTO tb_category (id, name, parent_id, sort_order) VALUES
(15, '编程开发', 1, 4),
(16, '经济学', 4, 3),
(17, '绘本', 8, 1),
(18, '科普读物', 8, 2);

-- ============================================================
-- 3. 补充图书（20本测试数据）
-- ============================================================
INSERT IGNORE INTO tb_book (isbn, title, author, publisher, publish_date, category_id, description, cover_image, total_stock, available_stock, sale_price, borrow_mode) VALUES
('9787532780123', '活着', '余华', '上海文艺出版社', '2012-08-01', 11, '讲述了农村人福贵悲惨的人生遭遇。', 'https://example.com/cover/huozhe.jpg', 50, 50, 39.00, 'SINGLE'),
('9787530218766', '平凡的世界', '路遥', '北京十月文艺出版社', '2017-07-01', 11, '以孙少安和孙少平两兄弟为中心，刻画了当时社会各阶层众多普通人的形象。', 'https://example.com/cover/pingfan.jpg', 30, 30, 108.00, 'SINGLE'),
('9787020132900', '红楼梦', '曹雪芹', '人民文学出版社', '2018-02-01', 11, '中国古典四大名著之一。', 'https://example.com/cover/hongloumeng.jpg', 40, 40, 108.00, 'MULTI'),
('9787020129762', '三体', '刘慈欣', '人民文学出版社', '2018-01-01', 5, '地球文明向宇宙发出的第一声啼鸣。', 'https://example.com/cover/santi.jpg', 60, 60, 68.00, 'SINGLE'),
('9787513533971', '小王子', '圣埃克苏佩里', '外语教学与研究出版社', '2013-01-01', 12, '一部献给曾经是孩子的大人们的童话。', 'https://example.com/cover/xiaowangzi.jpg', 80, 80, 32.00, 'SINGLE'),
('9787111544937', '深入理解计算机系统', '兰德尔·E·布莱恩特', '机械工业出版社', '2016-07-01', 15, '从程序员的视角详细阐述计算机系统的本质概念。', 'https://example.com/cover/csapp.jpg', 25, 25, 129.00, 'SINGLE'),
('9787115377324', 'JavaScript高级程序设计', '马特·弗里斯比', '人民邮电出版社', '2020-10-01', 15, 'JavaScript技术经典名著。', 'https://example.com/cover/js.jpg', 35, 35, 129.00, 'SINGLE'),
('9787115428577', 'Python编程：从入门到实践', '埃里克·马瑟斯', '人民邮电出版社', '2020-10-01', 15, '针对所有层次Python读者的经典编程入门书籍。', 'https://example.com/cover/python.jpg', 45, 45, 89.00, 'SINGLE'),
('9787208061322', '万历十五年', '黄仁宇', '上海人民出版社', '2006-08-01', 13, '从一个特殊的年份出发，剖析明代社会的症结。', 'https://example.com/cover/wanli.jpg', 55, 55, 38.00, 'SINGLE'),
('9787508669558', '人类简史', '尤瓦尔·赫拉利', '中信出版社', '2017-02-01', 14, '从认知革命、农业革命到科学革命，重新审视人类历史。', 'https://example.com/cover/renlei.jpg', 48, 48, 68.00, 'SINGLE'),
('9787508658660', '思考，快与慢', '丹尼尔·卡尼曼', '中信出版社', '2015-06-01', 3, '诺贝尔经济学奖得主丹尼尔·卡尼曼的代表作。', 'https://example.com/cover/sikao.jpg', 42, 42, 69.00, 'SINGLE'),
('9787508677643', '原则', '瑞·达利欧', '中信出版社', '2018-01-01', 4, '华尔街投资大神的人生经验之作。', 'https://example.com/cover/yuanze.jpg', 38, 38, 98.00, 'SINGLE'),
('9787508681906', '穷查理宝典', '彼得·考夫曼', '中信出版社', '2016-08-01', 4, '查理·芒格的智慧箴言录。', 'https://example.com/cover/qionglicha.jpg', 44, 44, 88.00, 'SINGLE'),
('9787532779073', '围城', '钱钟书', '上海文艺出版社', '2018-08-01', 11, '一部经典的讽刺小说，被誉为"新儒林外史"。', 'https://example.com/cover/weicheng.jpg', 52, 52, 42.00, 'SINGLE'),
('9787532758687', '百年孤独', '加西亚·马尔克斯', '上海文艺出版社', '2011-06-01', 12, '魔幻现实主义文学的代表作。', 'https://example.com/cover/bainian.jpg', 46, 46, 59.00, 'SINGLE'),
('9787532757666', '1984', '乔治·奥威尔', '上海文艺出版社', '2018-08-01', 12, '反乌托邦经典之作。', 'https://example.com/cover/1984.jpg', 58, 58, 45.00, 'SINGLE'),
('9787111641757', '机器学习', '周志华', '清华大学出版社', '2016-01-01', 10, '全面介绍机器学习的基础知识和核心算法。', 'https://example.com/cover/ml.jpg', 32, 32, 89.00, 'SINGLE'),
('9787115411375', '图解机器学习', '杉山将', '人民邮电出版社', '2018-01-01', 10, '用直观的图解方式解释机器学习的核心概念和算法。', 'https://example.com/cover/tujie_ml.jpg', 28, 28, 79.00, 'SINGLE'),
('9787533260580', '夏洛的网', 'E·B·怀特', '上海译文出版社', '2014-06-01', 17, '一部关于友谊与生命的童话。', 'https://example.com/cover/xialuo.jpg', 75, 75, 29.00, 'SINGLE'),
('9787544268073', '窗边的小豆豆', '黑柳彻子', '南海出版公司', '2018-01-01', 17, '讲述了小豆豆在巴学园的真实故事。', 'https://example.com/cover/xiaodoudou.jpg', 65, 65, 35.00, 'SINGLE');

-- ============================================================
-- 4. 补充图书评论
-- ============================================================
INSERT INTO tb_comment (book_id, user_id, username, rating, content) VALUES
(1, 1, 'admin', 5, 'Java经典中的经典，适合所有Java开发者！'),
(1, 2, 'zhangsan', 4, '内容全面，入门必读。'),
(2, 1, 'admin', 5, '深入理解计算机底层原理的必读书。'),
(3, 3, 'lisi', 5, '一部让人泪目的伟大作品。'),
(3, 4, 'wangwu', 5, '余华最好的小说之一。'),
(4, 2, 'zhangsan', 4, '魔幻现实主义巅峰，需要耐心读。'),
(5, 3, 'lisi', 5, '换个角度看历史，受益匪浅。'),
(6, 1, 'admin', 5, 'Java编程思想，百读不厌。'),
(7, 2, 'zhangsan', 5, '计算机专业必读。'),
(12, 3, 'lisi', 4, 'JavaScript开发者必读的红宝书。'),
(13, 4, 'wangwu', 5, 'Python入门的最佳选择。'),
(14, 5, 'sunqi', 4, '历史爱好者的心头好。'),
(15, 6, 'zhouba', 5, '颠覆认知的人类历史。'),
(18, 7, 'wujiu', 5, '机器学习领域的中文经典。'),
(20, 8, 'zhengshi', 4, '充满童趣的经典之作。');

-- ============================================================
-- 测试用户密码均为: 123456
-- 管理员: admin / 123456 (role=ADMIN)
-- ============================================================
