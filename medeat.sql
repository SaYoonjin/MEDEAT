/* =========================================================
   MEDEAT - FINAL SCHEMA (ERD 기준)
   - DB 생성부터 USE까지 포함
   - 28개 테이블 전체
========================================================= */

SET NAMES utf8mb4;
SET time_zone = '+09:00';

DROP DATABASE IF EXISTS medeat;
CREATE DATABASE medeat
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE medeat;

/* FK 때문에 드랍 순서 귀찮으니 체크 끄고 정리 */
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `post_like`;
DROP TABLE IF EXISTS `post_scrap`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `post`;
DROP TABLE IF EXISTS `notification`;

DROP TABLE IF EXISTS `challenge_log`;
DROP TABLE IF EXISTS `challenge_chat_message`;
DROP TABLE IF EXISTS `user_challenge`;
DROP TABLE IF EXISTS `challenge`;

DROP TABLE IF EXISTS `medication_log`;
DROP TABLE IF EXISTS `medication`;

DROP TABLE IF EXISTS `diet_item`;
DROP TABLE IF EXISTS `diet_log`;
DROP TABLE IF EXISTS `food`;

DROP TABLE IF EXISTS `user_disease`;
DROP TABLE IF EXISTS `disease`;

DROP TABLE IF EXISTS `user_badge`;
DROP TABLE IF EXISTS `badge`;

DROP TABLE IF EXISTS `push_subscription`;

DROP TABLE IF EXISTS `daily_xp`;
DROP TABLE IF EXISTS `xp_log`;
DROP TABLE IF EXISTS `user_streak`;
DROP TABLE IF EXISTS `user_progress`;

DROP TABLE IF EXISTS `api_pill_embedding`;
DROP TABLE IF EXISTS `pill_embedding`;
DROP TABLE IF EXISTS `drug_info`;

DROP TABLE IF EXISTS `med_info`;

DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

/* =========================================================
   1) USER
========================================================= */
CREATE TABLE `user` (
  `user_id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
  `login_id`       VARCHAR(50)  NOT NULL UNIQUE,
  `password`       VARCHAR(255) NOT NULL,
  `name`           VARCHAR(50)  NOT NULL,
  `nickname`       VARCHAR(50),
  `email`          VARCHAR(100),
  `phone`          VARCHAR(20),
  `gender`         VARCHAR(10),
  `age`            INT,
  `height`         DECIMAL(5,2),
  `weight`         DECIMAL(5,2),
  `goal_type`      VARCHAR(20),
  `default_mode`   VARCHAR(20),
  `pregnant_status` VARCHAR(10),
  `pregnancy_week`  INT,
  `created_at`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  `push_enabled`   TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   2) DISEASE / USER_DISEASE
========================================================= */
CREATE TABLE `disease` (
  `disease_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
  `disease_name` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_disease` (
  `user_disease_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`         BIGINT NOT NULL,
  `disease_id`      BIGINT NOT NULL,
  UNIQUE KEY `uq_user_disease` (`user_id`, `disease_id`),
  CONSTRAINT `fk_user_disease_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_user_disease_disease`
    FOREIGN KEY (`disease_id`) REFERENCES `disease`(`disease_id`)
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* (선택) seed */
INSERT INTO `disease` (`disease_id`, `disease_name`) VALUES
(1, '비만'),
(2, '고혈압'),
(3, '당뇨병'),
(4, '이상지질혈증'),
(5, '간염'),
(6, '빈혈'),
(7, '만성콩팥병'),
(8, '구강 건강'),
(9, '이비인후질환'),
(10, '굴절 이상'),
(11, '기타')
ON DUPLICATE KEY UPDATE disease_name = VALUES(disease_name);

DELETE FROM user_disease
WHERE disease_id = 11;

DELETE FROM disease
WHERE disease_id = 11;

/* =========================================================
   3) FOOD / DIET_LOG / DIET_ITEM
========================================================= */
CREATE TABLE `food` (
  `food_id`    BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name`       VARCHAR(255) NOT NULL,
  `calorie`    DECIMAL(10,2),
  `carb`       DECIMAL(10,2),
  `protein`    DECIMAL(10,2),
  `fat`        DECIMAL(10,2),
  `sodium`     DECIMAL(10,2),
  `sugar`      DECIMAL(10,2),
  `source`     VARCHAR(50),
  `food_type`  VARCHAR(20) NOT NULL DEFAULT 'MAIN'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/food_db.csv'
INTO TABLE food
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(name, calorie, carb, protein, fat, sodium, sugar, food_type)
SET 
    source = 'FOOD_DB';

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/product_db.csv'
INTO TABLE food
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(name, calorie, carb, protein, fat, sodium, sugar, food_type)
SET 
    source = 'PRODECT_DB';

SET SQL_SAFE_UPDATES = 0;
UPDATE food
SET food_type = 'SAUCE'
WHERE REPLACE(REPLACE(REPLACE(food_type, '\r', ''), '\n', ''), '\t', '') LIKE 'SUACE%';

UPDATE food
SET food_type = TRIM(REPLACE(REPLACE(REPLACE(food_type, '\r', ''), '\n', ''), '\t', ''));
    
CREATE TABLE `diet_log` (
  `diet_id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`        BIGINT NOT NULL,
  `log_date`       DATE NOT NULL,
  `meal_time`      VARCHAR(20) NOT NULL,
  `memo`           VARCHAR(255),
  `total_calorie`  INT DEFAULT 0,
  `total_carb`     DECIMAL(6,2) DEFAULT 0,
  `total_protein`  DECIMAL(6,2) DEFAULT 0,
  `total_fat`      DECIMAL(6,2) DEFAULT 0,
  `photo_path`     VARCHAR(255),
  `created_at`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_diet_log_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `diet_item` (
  `item_id`    BIGINT AUTO_INCREMENT PRIMARY KEY,
  `diet_id`    BIGINT NOT NULL,
  `food_id`    BIGINT NULL,
  `food_name`  VARCHAR(200) NOT NULL,
  `calorie`    INT DEFAULT 0,
  `carb`       DECIMAL(6,2) DEFAULT 0,
  `protein`    DECIMAL(6,2) DEFAULT 0,
  `fat`        DECIMAL(6,2) DEFAULT 0,
  `gram`       INT DEFAULT 100,
  CONSTRAINT `fk_diet_item_diet`
    FOREIGN KEY (`diet_id`) REFERENCES `diet_log`(`diet_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_diet_item_food`
    FOREIGN KEY (`food_id`) REFERENCES `food`(`food_id`)
    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   4) DRUG_INFO / PILL_EMBEDDING / API_PILL_EMBEDDING
========================================================= */
CREATE TABLE `drug_info` (
  `item_seq` BIGINT PRIMARY KEY,
  `item_name` VARCHAR(500),
  `entp_name` VARCHAR(255),
  `ingr_name` TEXT,
  `item_image` TEXT,
  `efcy_qesitm` MEDIUMTEXT,
  `use_method_qesitm` MEDIUMTEXT,
  `atpn_warn_qesitm` MEDIUMTEXT,
  `atpn_qesitm` MEDIUMTEXT,
  `intrc_qesitm` MEDIUMTEXT,
  `se_qesitm` MEDIUMTEXT,
  `deposit_method_qesitm` MEDIUMTEXT,
  `drug_shape` VARCHAR(100),
  `color_class1` VARCHAR(100),
  `color_class2` VARCHAR(100),
  `print_front` VARCHAR(100),
  `print_back` VARCHAR(100),
  `line_front` VARCHAR(50),
  `line_back` VARCHAR(50),
  `leng_long` VARCHAR(50),
  `leng_short` VARCHAR(50),
  `thick` VARCHAR(50),
  `img_regist_ts` TIMESTAMP NULL,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pill_embedding` (
  `item_seq` BIGINT PRIMARY KEY,
  `item_name` VARCHAR(255),
  `entp_name` VARCHAR(255),
  `dim` INT NOT NULL,
  `vector_json` JSON NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `api_pill_embedding` (
  `api_item_seq` BIGINT PRIMARY KEY,
  `image_url` TEXT,
  `embedding_json` MEDIUMTEXT NOT NULL,
  `dim` INT NOT NULL,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   5) MEDICATION / MEDICATION_LOG / MED_INFO
========================================================= */
CREATE TABLE `medication` (
  `medication_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`       BIGINT NOT NULL,
  `drug_name`     VARCHAR(100) NOT NULL,
  `ingredient`    VARCHAR(100),
  `dose`          VARCHAR(50),
  `intake_time`   VARCHAR(20),
  `interval_hour` INT,
  `memo`          VARCHAR(255),
  `daily_count`   INT DEFAULT 1,
  `recommended`   TEXT,
  `item_seq`      BIGINT,
  CONSTRAINT `fk_medication_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `medication_log` (
  `log_id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
  `medication_id` BIGINT NOT NULL,
  `user_id`       BIGINT NOT NULL,
  `taken_index`   INT NOT NULL,
  `taken_at`      DATETIME DEFAULT NOW(),
  CONSTRAINT `fk_medication_log_medication`
    FOREIGN KEY (`medication_id`) REFERENCES `medication`(`medication_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_medication_log_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `med_info` (
  `med_info_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`     BIGINT NOT NULL,
  `disease`     VARCHAR(255),
  `medicine`    VARCHAR(255),
  `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_med_info_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   6) CHALLENGE / USER_CHALLENGE / CHALLENGE_LOG / CHAT
========================================================= */
CREATE TABLE `challenge` (
  `challenge_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `mode_type`    VARCHAR(20) NOT NULL,
  `category`     VARCHAR(50),
  `title`        VARCHAR(100) NOT NULL,
  `description`  TEXT,
  `period_days`  INT,
  `difficulty`   VARCHAR(20),
  `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP,
  `user_id`      BIGINT NOT NULL,
  `max_participants` INT NOT NULL DEFAULT 10,
  `current_participants` INT NOT NULL DEFAULT 0,
  `rejoin_cooldown_days` INT NOT NULL DEFAULT 1,
  CONSTRAINT `fk_challenge_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_challenge` (
  `user_challenge_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`           BIGINT NOT NULL,
  `challenge_id`      BIGINT NOT NULL,
  `start_date`        DATE NOT NULL,
  `end_date`          DATE,
  `status`            VARCHAR(20) NOT NULL,
  `giveup_at`         DATETIME,
  `rejoin_available_at` DATETIME,
  UNIQUE KEY `uq_user_challenge` (`user_id`, `challenge_id`),
  CONSTRAINT `fk_user_challenge_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_user_challenge_challenge`
    FOREIGN KEY (`challenge_id`) REFERENCES `challenge`(`challenge_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_user_challenge_challenge_status`
  ON `user_challenge` (`challenge_id`, `status`);
CREATE INDEX `idx_user_challenge_user_status`
  ON `user_challenge` (`user_id`, `status`);

CREATE TABLE `challenge_log` (
  `challenge_log_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_challenge_id` BIGINT NOT NULL,
  `log_date`         DATE NOT NULL,
  `status`           VARCHAR(20) NOT NULL,
  `memo`             VARCHAR(255),
  UNIQUE KEY `uk_challenge_log` (`user_challenge_id`, `log_date`),
  CONSTRAINT `fk_challenge_log_user_challenge`
    FOREIGN KEY (`user_challenge_id`) REFERENCES `user_challenge`(`user_challenge_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `challenge_chat_message` (
  `message_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
  `challenge_id` BIGINT NOT NULL,
  `user_id`      BIGINT NOT NULL,
  `nickname`     VARCHAR(50) NOT NULL,
  `content`      TEXT NOT NULL,
  `created_at`   DATETIME DEFAULT NOW(),
  CONSTRAINT `fk_chat_challenge`
    FOREIGN KEY (`challenge_id`) REFERENCES `challenge`(`challenge_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_chat_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   7) BADGE / USER_BADGE
========================================================= */
CREATE TABLE `badge` (
  `badge_id`    BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name`        VARCHAR(100) NOT NULL,
  `description` VARCHAR(255),
  `mode_type`   VARCHAR(20),
  `icon_path`   VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_badge` (
  `user_badge_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`       BIGINT NOT NULL,
  `badge_id`      BIGINT NOT NULL,
  `earned_at`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_user_badge_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_user_badge_badge`
    FOREIGN KEY (`badge_id`) REFERENCES `badge`(`badge_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   8) COMMUNITY: POST / COMMENT / LIKE / SCRAP
========================================================= */
CREATE TABLE `post` (
  `post_id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`        BIGINT NOT NULL,
  `mode_type`      VARCHAR(20) NOT NULL,
  `category`       VARCHAR(50),
  `title`          VARCHAR(200) NOT NULL,
  `content`        TEXT NOT NULL,
  `image_path`     VARCHAR(255),
  `like_count`     INT DEFAULT 0,
  `comment_count`  INT DEFAULT 0,
  `scrap_count`    INT DEFAULT 0,
  `is_deleted`     TINYINT(1) DEFAULT 0,
  `created_at`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_post_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_post_mode` ON `post`(`mode_type`);
CREATE INDEX `idx_post_created` ON `post`(`created_at`);

CREATE TABLE `comment` (
  `comment_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `post_id`    BIGINT NOT NULL,
  `user_id`    BIGINT NOT NULL,
  `parent_id`  BIGINT NULL,
  `content`    VARCHAR(500) NOT NULL,
  `is_deleted` TINYINT(1) DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_comment_post`
    FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `post_like` (
  `post_like_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `post_id`      BIGINT NOT NULL,
  `user_id`      BIGINT NOT NULL,
  `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uq_post_like` (`post_id`, `user_id`),
  CONSTRAINT `fk_post_like_post`
    FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_post_like_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `post_scrap` (
  `post_scrap_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `post_id`       BIGINT NOT NULL,
  `user_id`       BIGINT NOT NULL,
  `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uq_post_scrap` (`post_id`, `user_id`),
  CONSTRAINT `fk_post_scrap_post`
    FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_post_scrap_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   9) NOTIFICATION
========================================================= */
CREATE TABLE `notification` (
  `notification_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`     BIGINT NOT NULL,
  `type`        VARCHAR(50) NOT NULL,
  `message`     VARCHAR(255) NOT NULL,
  `is_read`     TINYINT(1) NOT NULL DEFAULT 0,
  `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  `target_type` VARCHAR(30),
  `target_id`   BIGINT,
  CONSTRAINT `fk_notification_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE,
  INDEX `idx_notification_user_created` (`user_id`, `created_at`),
  INDEX `idx_notification_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   10) PUSH SUBSCRIPTION
========================================================= */
CREATE TABLE `push_subscription` (
  `id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`   BIGINT NOT NULL,
  `endpoint`  TEXT NOT NULL,
  `p256dh`    TEXT NOT NULL,
  `auth`      TEXT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_push_subscription_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================================================
   11) XP / PROGRESS / STREAK
========================================================= */
CREATE TABLE `user_progress` (
  `user_id`    BIGINT PRIMARY KEY,
  `total_xp`   INT DEFAULT 0,
  `level`      TINYINT DEFAULT 1,
  `master_xp`  INT DEFAULT 0,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_user_progress_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_streak` (
  `user_id`          BIGINT PRIMARY KEY,
  `current_streak`   INT DEFAULT 0,
  `last_action_date` DATE,
  `updated_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_user_streak_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xp_log` (
  `xp_log_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id`     BIGINT NOT NULL,
  `action_type` VARCHAR(40) NOT NULL,
  `ref_id`      VARCHAR(64) NOT NULL,
  `xp`          SMALLINT DEFAULT 0,
  `earned_date` DATE NOT NULL,
  `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uq_xp_log_once` (`user_id`, `action_type`, `earned_date`, `ref_id`),
  INDEX `idx_xp_log_user_date` (`user_id`, `earned_date`),
  INDEX `idx_xp_log_user_action_date` (`user_id`, `action_type`, `earned_date`),
  CONSTRAINT `fk_xp_log_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `daily_xp` (
  `user_id`    BIGINT NOT NULL,
  `day`        DATE NOT NULL,
  `xp_sum`     SMALLINT DEFAULT 0,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `day`),
  CONSTRAINT `fk_daily_xp_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;