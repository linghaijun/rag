-- 创建数据库
CREATE DATABASE IF NOT EXISTS kris_rag2 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE kris_rag2;

-- 文档表
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(500),
    content TEXT,
    file_size BIGINT,
    file_type VARCHAR(100),
    upload_time DATETIME,
    chunk_count INT DEFAULT 0,
    INDEX idx_upload_time (upload_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
