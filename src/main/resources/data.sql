-- 既存のデータを一度クリアすると確実です
DELETE
FROM reviews;
DELETE
FROM favoritePlaces;
DELETE
FROM places;
DELETE
FROM users;

-- パスワードは 'morijyobi' をBCryptでハッシュ化したものです。
-- 実際のアプリケーションでは、ユーザー登録時にハッシュ化処理を行ってください。
INSERT INTO users (email, username, password)
VALUES ('test@example.com', 'test', '$2a$08$sw2SDH2/saf7W7z5dOD2SeGVHXcpeALCjVUk2DweiaqE4kRm/j9x.');

INSERT INTO users (email, username, password)
VALUES ( 'test2@example.com', 'テストユーザー2', '$2a$08$sw2SDH2/saf7W7z5dOD2SeGVHXcpeALCjVUk2DweiaqE4kRm/j9x.');

-- ★★★ 修正点: 口コミで使う場所を先に登録する ★★★
-- テスト用の場所を追加
INSERT INTO places (place_id, name, address)
VALUES ('ChIJCewJkL2LGGAR3Qmk0vCTGkg', '東京タワー', '〒105-0011 東京都港区芝公園４丁目２−８'),
       ('ChIJ3X9S5PaLGGAR9c2sft2-63Q', '東京スカイツリー', '〒131-0045 東京都墨田区押上１丁目１−２');

-- ★★★ 修正点: place_idを統一する ★★★
-- 東京タワーへの口コミを3件追加
INSERT INTO reviews (user_id, place_id, rating, comment, visited_at, image_path)
VALUES (2, 'ChIJCewJkL2LGGAR3Qmk0vCTGkg', 5.0, '展望台からの眺めは最高でした！特に夜景は感動的です。一生の思い出になりました。', '2024-07-15',
        'sample-img1.jpg'),
       (2, 'ChIJCewJkL2LGGAR3Qmk0vCTGkg', 4.0, '週末は少し混雑していましたが、それでも行く価値はあります。お土産屋さんも充実していました。',
        '2023-11-20', null),
       (2, 'ChIJCewJkL2LGGAR3Qmk0vCTGkg', 4.5, '平日の昼間に行ったので、ゆっくりと景色を楽しむことができました。カフェでの休憩もおすすめです。',
        '2024-05-01', 'sample-img2.jpg');

-- (任意) テスト用のお気に入りを追加
INSERT INTO favoritePlaces (user_id, place_id)
VALUES (2, 'ChIJCewJkL2LGGAR3Qmk0vCTGkg');
