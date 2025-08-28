-- ユーザー情報を格納するテーブル
CREATE TABLE users
(
    user_id    SERIAL PRIMARY KEY,
    email      VARCHAR(100) UNIQUE NOT NULL,
    username   VARCHAR(255) ,
    password   VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Google Place APIから取得した場所の情報を格納するテーブル
CREATE TABLE places
(
    place_id             VARCHAR(255) NOT NULL PRIMARY KEY,
    name                 VARCHAR(255),
    address              VARCHAR(500),
    description          TEXT, -- ★追加: 場所の概要説明
    latitude             DECIMAL(9, 6),
    longitude            DECIMAL(9, 6),
    phone_number         VARCHAR(50),
    website_url          VARCHAR(500),
    rating_google        DECIMAL(2, 1),
    user_ratings_total   INTEGER,
    price_level          TINYINT,
    opening_hours_json   TEXT,
    types_json           TEXT, -- ★変更: カテゴリをJSONで保存
    -- photo_referenceカラムは不要になる
    last_fetched_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE place_photos
(
    id                INTEGER PRIMARY KEY AUTO_INCREMENT,
    place_id          VARCHAR(255) NOT NULL,
    photo_reference   TEXT NOT NULL,
    height            INTEGER,
    width             INTEGER,
    html_attributions TEXT,
    FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE CASCADE -- 場所が消えたら写真も消す
);

-- お気に入りの場所を管理するテーブル
CREATE TABLE favoritePlaces
(
    favorite_place_id SERIAL PRIMARY KEY,
    user_id           INT,
    place_id          VARCHAR(255),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES places (place_id) ON DELETE CASCADE,
    UNIQUE (user_id, place_id)
);

-- 口コミ情報を格納するテーブル
CREATE TABLE reviews
(
    review_id  SERIAL PRIMARY KEY,
    user_id    INT           NOT NULL,
    place_id   VARCHAR(255)  NOT NULL,
    rating     DECIMAL(2, 1) NOT NULL,
    comment    TEXT,
    visited_at DATE, -- 訪問日はDATE型が適切
    image_path VARCHAR(255), -- 画像ファイル名を保存するカラム
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES places (place_id) ON DELETE CASCADE
);