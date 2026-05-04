CREATE TABLE IF NOT EXISTS challenge (
    challenge_id BIGINT NOT NULL AUTO_INCREMENT,
    mode_type VARCHAR(50) NOT NULL,
    category VARCHAR(100) NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    period_days INT NOT NULL,
    difficulty VARCHAR(50) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    max_participants INT NOT NULL,
    current_participants INT NOT NULL DEFAULT 0,
    CONSTRAINT pk_challenge PRIMARY KEY (challenge_id),
    CONSTRAINT fk_challenge_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_challenge (
    user_challenge_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    challenge_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    status VARCHAR(20) NOT NULL,
    giveup_at DATETIME NULL,
    rejoin_available_at DATETIME NULL,
    CONSTRAINT pk_user_challenge PRIMARY KEY (user_challenge_id),
    CONSTRAINT uk_user_challenge_user_challenge UNIQUE (user_id, challenge_id),
    CONSTRAINT fk_user_challenge_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_challenge_challenge
        FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS challenge_log (
    challenge_log_id BIGINT NOT NULL AUTO_INCREMENT,
    user_challenge_id BIGINT NOT NULL,
    log_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    memo TEXT NULL,
    CONSTRAINT pk_challenge_log PRIMARY KEY (challenge_log_id),
    CONSTRAINT uk_challenge_log_user_challenge_date UNIQUE (user_challenge_id, log_date),
    CONSTRAINT fk_challenge_log_user_challenge
        FOREIGN KEY (user_challenge_id) REFERENCES user_challenge (user_challenge_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS challenge_chat_message (
    message_id BIGINT NOT NULL AUTO_INCREMENT,
    challenge_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    nickname VARCHAR(100) NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_challenge_chat_message PRIMARY KEY (message_id),
    CONSTRAINT fk_challenge_chat_message_challenge
        FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_challenge_chat_message_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);
