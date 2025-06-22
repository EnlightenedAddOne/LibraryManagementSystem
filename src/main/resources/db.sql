# 图书表
CREATE TABLE book (
                      book_id INT AUTO_INCREMENT PRIMARY KEY, -- 图书ID
                      title VARCHAR(255) NOT NULL,            -- 书名
                      author VARCHAR(255) NOT NULL,           -- 作者名字
                      publisher VARCHAR(255),                 -- 出版社名称
                      isbn VARCHAR(255),                      -- 国际标准书号
                      category VARCHAR(100),                  -- 图书分类
                      description TEXT,                       -- 图书描述（可选）
                      cover VARCHAR(255),                     -- 图书封面URL（可选）
                      status INT NOT NULL DEFAULT 1,                    -- 图书状态：1表示可借阅，0表示不可借阅
                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP, -- 图书添加时间
                      publish_date DATE                       -- 出版日期，格式为 YYYY-MM-DD
);

INSERT INTO book (title, author, publisher, isbn, category, description, cover, status, create_time, publish_date)
VALUES
    ('Spring Boot实战', 'Craig Walls', '人民邮电出版社', '9787115446633', '编程', '一本关于Spring Boot的实战书籍', NULL, 1, NOW(), '2016-12-01'),
    ('Java编程思想', 'Bruce Eckel', '机械工业出版社', '9787111213826', '编程', 'Java经典书籍', NULL, 1, NOW(), '2007-06-01'),
    ('Effective Java', 'Joshua Bloch', '机械工业出版社', '9787111612728', '编程', 'Java开发最佳实践', NULL, 1, NOW(), '2018-12-01');


# 用户表
CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,   -- 账号
                       password VARCHAR(100) NOT NULL,         -- 密码（加密存储）
                       role VARCHAR(10) DEFAULT 'R_USER',             -- 用户角色：'R_USER'（默认）、'R_ADMIN' 或 'R_SUPER'
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO users
(username, password, role) VALUES
                               ('lily', SHA2('lilypassword', 256), 'R_USER'),
                               ('admin1', SHA2('123456', 256), 'R_ADMIN'),
                               ('super123', SHA2('1234567', 256), 'R_SUPER'),
                               ('tomlee', SHA2('tomspass', 256), 'R_USER'),
                               ('superuser', SHA2('secret123', 256), 'R_ADMIN');

# 用户个人资料表
CREATE TABLE user_profiles (
                               profile_id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT UNIQUE,           -- 关联到 users 表的 user_id
                               real_name VARCHAR(50) NOT NULL,        -- 真实姓名
                               nickname VARCHAR(50),         -- 昵称
                               email VARCHAR(100),           -- 邮箱
                               phone VARCHAR(20),           -- 手机号
                               school VARCHAR(100),         -- 学校
                               sex CHAR(1),                 -- 性别：'0'-女，'1'-男
                               description TEXT,                    -- 个人描述
                               avatar VARCHAR(255),         -- 头像URL
                               background_url VARCHAR(255),           -- 背景图UR
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id)
);
INSERT INTO user_profiles
(user_id, real_name, nickname, email, phone, school, sex, description, avatar) VALUES
                                                                                   (1, '张三', '三哥', 'zhangsan@example.com', '13800138000', '北京大学', '1', '热爱编程，喜欢篮球', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.7GLMYPqMlt2LgkbPsOnDIAAAAA?w=196&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2'),
                                                                                   (2, '李四', '四哥', 'lisi@example.com', '13900139000', '清华大学', '1', '热爱旅游，喜欢摄影', 'https://img2.huashi6.com/images/resource/thumbnail/2025/06/09/173934_9744912964.jpg?imageMogr2/quality/75/interlace/1/thumbnail/446x/gravity/North/crop/446x626/format/webp'),
                                                                                   (3, '王五', '五哥', 'wangwu@example.com', '13700137000', '浙江大学', '1', '热爱阅读，喜欢音乐', 'https://img2.huashi6.com/images/resource/thumbnail/2025/06/06/20346_42694052266.jpg?imageMogr2/quality/75/interlace/1/thumbnail/446x/gravity/North/crop/446x582/format/webp'),
                                                                                   (4, '赵六', '六哥', 'zhaoliu@example.com', '13600136000', '复旦大学', '1', '热爱运动，喜欢游泳', 'https://img2.huashi6.com/images/resource/thumbnail/2024/11/01/143255_7752273619.gif?imageMogr2/quality/75/interlace/1/thumbnail/x446/gravity/Center/crop/446x446'),
                                                                                   (5, '孙七', '七哥', 'sunqi@example.com', '13500135000', '上海交通大学', '1', '热爱电影，喜欢美食', 'https://img2.huashi6.com/images/resource/thumbnail/2025/05/26/233231_55153337750.jpg?imageMogr2/quality/75/interlace/1/thumbnail/x446/gravity/Center/crop/446x446/format/webp');




# 借阅表
CREATE TABLE borrow_records (
                                record_id     INT PRIMARY KEY AUTO_INCREMENT,
                                book_id       INT,
                                user_id       INT,            -- 从 users 表引用
                                borrow_date   DATE,
                                return_date   DATE,

                                FOREIGN KEY (book_id) REFERENCES book(book_id),
                                FOREIGN KEY (user_id) REFERENCES users(user_id)
);

# -- 插入测试数据
# INSERT INTO borrow_records (book_id, user_id, borrow_date, return_date) VALUES
#                                                                             (1, 2, '2025-06-01', NULL),        -- 李莉借了《Spring Boot实战》，未还
#                                                                             (3, 3, '2025-05-20', '2025-06-05'),-- 汤姆·李借了《Effective Java》，已还
#                                                                             (2, 4, '2025-06-03', NULL),        -- 王强借了《Java编程思想》，未还
#                                                                             (3, 1, '2025-06-04', NULL);        -- 张三又借了《Effective Java》（可能归还后再次借出）
#
#


-- 在借阅记录插入时自动将图书状态改为不可借阅(0)
DELIMITER //
CREATE TRIGGER update_book_status_after_borrow
    AFTER INSERT ON borrow_records
    FOR EACH ROW
BEGIN
    -- 仅当借阅记录未归还时更新状态
    IF NEW.return_date IS NULL THEN
        UPDATE book SET status = 0 WHERE book_id = NEW.book_id;
    END IF;
END //
DELIMITER ;

-- 在借阅记录更新(归还图书)时自动将图书状态改为可借阅(1)
DELIMITER //
CREATE TRIGGER update_book_status_after_return
    AFTER UPDATE ON borrow_records
    FOR EACH ROW
BEGIN
    -- 仅当归还日期从NULL变为非NULL时更新状态
    IF OLD.return_date IS NULL AND NEW.return_date IS NOT NULL THEN
        UPDATE book SET status = 1 WHERE book_id = NEW.book_id;
    END IF;
END //
DELIMITER ;