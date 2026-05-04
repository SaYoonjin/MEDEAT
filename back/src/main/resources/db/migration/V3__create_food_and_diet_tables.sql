CREATE TABLE IF NOT EXISTS food (
    food_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    brand_name VARCHAR(255) NULL,
    category VARCHAR(255) NULL,
    serving_size VARCHAR(100) NULL,
    serving_gram INT NULL,
    calorie INT NULL,
    carb DOUBLE NULL,
    protein DOUBLE NULL,
    fat DOUBLE NULL,
    sodium INT NULL,
    sugar DOUBLE NULL,
    source VARCHAR(50) NULL,
    food_type VARCHAR(30) NULL,
    CONSTRAINT pk_food PRIMARY KEY (food_id)
);

CREATE TABLE IF NOT EXISTS diet_log (
    diet_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    log_date DATE NOT NULL,
    meal_time VARCHAR(50) NOT NULL,
    memo TEXT NULL,
    photo_path VARCHAR(512) NULL,
    total_calorie INT NULL,
    total_carb DOUBLE NULL,
    total_protein DOUBLE NULL,
    total_fat DOUBLE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_diet_log PRIMARY KEY (diet_id),
    CONSTRAINT fk_diet_log_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS diet_item (
    item_id BIGINT NOT NULL AUTO_INCREMENT,
    diet_id BIGINT NOT NULL,
    food_id BIGINT NULL,
    food_name VARCHAR(255) NULL,
    gram INT NULL,
    calorie INT NULL,
    carb DOUBLE NULL,
    protein DOUBLE NULL,
    fat DOUBLE NULL,
    CONSTRAINT pk_diet_item PRIMARY KEY (item_id),
    CONSTRAINT fk_diet_item_diet_log
        FOREIGN KEY (diet_id) REFERENCES diet_log (diet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_diet_item_food
        FOREIGN KEY (food_id) REFERENCES food (food_id)
        ON DELETE SET NULL
);
