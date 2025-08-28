// ===================================================================
// 1. 画像保存サービス (新規作成)
// ===================================================================
// src/main/java/com/example/finalassignment/service/FileStorageService.java

package com.example.finalassignment.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // 画像を保存するディレクトリのパス
    private final Path storageLocation;

    public FileStorageService() {
        // プロジェクトのルートに 'review-images' というフォルダを作成して保存します。
        // このパスは application.properties などで外部から設定できるようにすると、より柔軟になります。
        this.storageLocation = Paths.get("review-images").toAbsolutePath().normalize();

        try {
            // 保存用ディレクトリが存在しない場合は作成
            Files.createDirectories(this.storageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * ファイルを保存し、保存先のパスを返す
     * @param file アップロードされたファイル
     * @return データベースに保存するためのファイルパス (例: /images/reviews/xxxx.jpg)
     */
    public String storeFile(MultipartFile file) {
        // ファイル名を安全なものに正規化
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + originalFileName);
            }

            // ファイル名が衝突しないように、ランダムなUUIDをファイル名の前につける
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

            // ファイルを保存するパスを解決
            Path targetLocation = this.storageLocation.resolve(uniqueFileName);

            // ファイルをターゲットの場所にコピー（同名ファイルがあれば上書き）
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // ★★★ Webからアクセスするためのパスを返す ★★★
            // 実際にWebサーバーで静的リソースとして配信するパスを想定します。
            // ここでは、保存したファイル名をそのまま返します。
            // Webサーバーの設定で /review-images/** へのアクセスを許可する必要があります。
            return uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    /**
     * ★★★ ファイルを削除するメソッドを追加 ★★★
     * @param filename 削除するファイル名
     */
    public void deleteFile(String filename) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            Path targetLocation = this.storageLocation.resolve(filename);
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            // ファイル削除に失敗しても処理は続行させたいので、ログ出力に留める
            System.err.println("Could not delete file: " + filename);
            ex.printStackTrace();
        }
    }
}