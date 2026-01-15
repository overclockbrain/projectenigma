package com.example.projectenigma.cipher.strategy;

import com.example.projectenigma.cipher.dto.Stage4Config;
import com.example.projectenigma.cipher.entity.Riddle;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * Stage 4 (Gravity Architect) のデータセットアップ戦略。
 * Riddleエンティティに含まれるJSON文字列をパースし、
 * フロントエンドで使用する Stage4Config オブジェクトをModelに格納する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class Stage4Strategy implements StageStrategy {

    /** JSON変換用ライブラリ (Spring標準搭載) */
    private final ObjectMapper objectMapper;

    @Override
    public int getStageId() {
        return 4;
    }

    @Override
    public void setupModel(Model model) {
        // Controllerですでに "riddle" がModelに入っている前提
        if (!model.containsAttribute("riddle")) {
            return;
        }

        Riddle riddle = (Riddle) model.getAttribute("riddle");
        String stageDataJson = riddle.getStageData();

        // データがある場合のみパース処理を行う
        if (stageDataJson != null && !stageDataJson.isEmpty()) {
            try {
                // JSON文字列 -> DTO変換
                Stage4Config config = objectMapper.readValue(stageDataJson, Stage4Config.class);
                
                // Thymeleafで使いやすいようにModelに登録
                model.addAttribute("stage4Config", config);
            } catch (Exception e) {
                // 本番ではログ出力推奨やけど、パース失敗時は何もしない（画面側で空として扱われる）
                e.printStackTrace();
            }
        }
    }
}