CREATE TABLE IF NOT EXISTS `user` (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    login_id VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    nickname VARCHAR(100) NULL,
    email VARCHAR(255) NULL,
    phone VARCHAR(50) NULL,
    gender VARCHAR(20) NULL,
    age INT NULL,
    height DOUBLE NULL,
    weight DOUBLE NULL,
    goal_type VARCHAR(50) NULL,
    default_mode VARCHAR(50) NULL,
    pregnant_status VARCHAR(20) NULL,
    pregnancy_week INT NULL,
    push_enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT uk_user_login_id UNIQUE (login_id)
);

CREATE TABLE IF NOT EXISTS push_subscription (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    endpoint VARCHAR(512) NOT NULL,
    p256dh VARCHAR(255) NOT NULL,
    auth VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_push_subscription PRIMARY KEY (id),
    CONSTRAINT uk_push_subscription_endpoint UNIQUE (endpoint),
    CONSTRAINT fk_push_subscription_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);
