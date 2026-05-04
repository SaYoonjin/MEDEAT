CREATE TABLE IF NOT EXISTS notification (
    notification_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    target_type VARCHAR(50) NULL,
    target_id BIGINT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_notification PRIMARY KEY (notification_id),
    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_progress (
    user_id BIGINT NOT NULL,
    total_xp INT NOT NULL DEFAULT 0,
    level INT NOT NULL DEFAULT 1,
    master_xp INT NOT NULL DEFAULT 0,
    CONSTRAINT pk_user_progress PRIMARY KEY (user_id),
    CONSTRAINT fk_user_progress_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS daily_xp (
    user_id BIGINT NOT NULL,
    `day` DATE NOT NULL,
    xp_sum INT NOT NULL DEFAULT 0,
    CONSTRAINT pk_daily_xp PRIMARY KEY (user_id, `day`),
    CONSTRAINT fk_daily_xp_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS xp_log (
    xp_log_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    ref_id VARCHAR(100) NOT NULL,
    xp INT NOT NULL,
    earned_date DATE NOT NULL,
    CONSTRAINT pk_xp_log PRIMARY KEY (xp_log_id),
    CONSTRAINT uk_xp_log_user_action_ref_day UNIQUE (user_id, action_type, ref_id, earned_date),
    CONSTRAINT fk_xp_log_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_streak (
    user_id BIGINT NOT NULL,
    current_streak INT NOT NULL DEFAULT 0,
    last_action_date DATE NULL,
    CONSTRAINT pk_user_streak PRIMARY KEY (user_id),
    CONSTRAINT fk_user_streak_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_badge (
    user_badge_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    badge_code VARCHAR(100) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_badge PRIMARY KEY (user_badge_id),
    CONSTRAINT fk_user_badge_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);
