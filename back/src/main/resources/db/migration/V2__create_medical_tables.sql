CREATE TABLE IF NOT EXISTS disease (
    disease_id BIGINT NOT NULL AUTO_INCREMENT,
    disease_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_disease PRIMARY KEY (disease_id),
    CONSTRAINT uk_disease_name UNIQUE (disease_name)
);

CREATE TABLE IF NOT EXISTS user_disease (
    user_disease_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    disease_id BIGINT NOT NULL,
    CONSTRAINT pk_user_disease PRIMARY KEY (user_disease_id),
    CONSTRAINT uk_user_disease_user_disease UNIQUE (user_id, disease_id),
    CONSTRAINT fk_user_disease_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_disease_disease
        FOREIGN KEY (disease_id) REFERENCES disease (disease_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS med_info (
    user_id BIGINT NOT NULL,
    disease TEXT NULL,
    medicine TEXT NULL,
    CONSTRAINT pk_med_info PRIMARY KEY (user_id),
    CONSTRAINT fk_med_info_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS medication (
    medication_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    item_seq BIGINT NULL,
    drug_name VARCHAR(255) NOT NULL,
    ingredient TEXT NULL,
    dose VARCHAR(255) NULL,
    intake_time VARCHAR(255) NULL,
    interval_hour INT NULL,
    memo TEXT NULL,
    daily_count INT NULL,
    recommended VARCHAR(255) NULL,
    CONSTRAINT pk_medication PRIMARY KEY (medication_id),
    CONSTRAINT fk_medication_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS medication_log (
    log_id BIGINT NOT NULL AUTO_INCREMENT,
    medication_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    taken_index INT NULL,
    taken_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_medication_log PRIMARY KEY (log_id),
    CONSTRAINT fk_medication_log_medication
        FOREIGN KEY (medication_id) REFERENCES medication (medication_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_medication_log_user
        FOREIGN KEY (user_id) REFERENCES `user` (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS drug_info (
    item_seq BIGINT NOT NULL,
    item_name VARCHAR(255) NULL,
    entp_name VARCHAR(255) NULL,
    ingr_name TEXT NULL,
    item_image TEXT NULL,
    efcy_qesitm TEXT NULL,
    use_method_qesitm TEXT NULL,
    atpn_warn_qesitm TEXT NULL,
    atpn_qesitm TEXT NULL,
    intrc_qesitm TEXT NULL,
    se_qesitm TEXT NULL,
    deposit_method_qesitm TEXT NULL,
    drug_shape VARCHAR(100) NULL,
    color_class1 VARCHAR(100) NULL,
    color_class2 VARCHAR(100) NULL,
    print_front VARCHAR(100) NULL,
    print_back VARCHAR(100) NULL,
    line_front VARCHAR(100) NULL,
    line_back VARCHAR(100) NULL,
    leng_long VARCHAR(100) NULL,
    leng_short VARCHAR(100) NULL,
    thick VARCHAR(100) NULL,
    CONSTRAINT pk_drug_info PRIMARY KEY (item_seq)
);
