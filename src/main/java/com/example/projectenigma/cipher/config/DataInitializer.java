package com.example.projectenigma.cipher.config;

import com.example.projectenigma.cipher.entity.Riddle;
import com.example.projectenigma.cipher.repository.RiddleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * アプリケーション起動時に実行される初期化クラス。
 * resources/data/riddles.json ファイルを読み込み、
 * 問題データが空の場合にDBへ初期データを投入する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RiddleRepository riddleRepository;
    private final ObjectMapper objectMapper;

    /**
     * Spring Boot起動完了後に呼ばれるメソッド。
     * データの二重登録を防ぐチェックを行った上で、JSONロードを実行する。
     *
     * @param args 起動引数（未使用）
     * @throws Exception ファイル読み込みエラーなど
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Override
    public void run(String... args) throws Exception {
        // すでにデータがある場合は何もしない（二重登録防止）
        if (riddleRepository.count() > 0) {
            return;
        }

        // /data/riddles.json を読み込む
        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/riddles.json")) {
            if (inputStream == null) {
                System.err.println("❌ riddles.json not found!");
                return;
            }

            // JSON -> List<Riddle> に変換
            List<Riddle> riddles = objectMapper.readValue(inputStream, new TypeReference<List<Riddle>>(){});
            
            // DBに保存
            riddleRepository.saveAll(riddles);
            System.out.println("🎉 Riddles loaded from JSON successfully! Count: " + riddles.size());
        } catch (Exception e) {
            System.err.println("❌ Failed to load riddles: " + e.getMessage());
            e.printStackTrace();
        }
    }
}