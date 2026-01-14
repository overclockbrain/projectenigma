package com.example.projectenigma.cipher.strategy;

import com.example.projectenigma.cipher.dto.ScaleItem;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

/**
 * Stage 1 (天秤パズル) のデータセットアップ戦略。
 */
@Component
public class Stage1Strategy implements StageStrategy {

    @Override
    public int getStageId() {
        return 1;
    }

    /**
     * Stage 1（天秤パズル）で使用するアイテムリストを生成して返却する。
     * * 戻り値:
     * 天秤に乗せるアイテム（箱、水、金貨など）のリスト
     * 各アイテムには名前、重さ、CSSクラスが定義されている
     * 
     * @return List<ScaleItem>
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Override
    public void setupModel(Model model) {
        List<ScaleItem> items = Arrays.asList(
            new ScaleItem("📦", 50, "item-box"),
            new ScaleItem("💧", 50, "item-water"),
            new ScaleItem("💰", 40, "item-gold"),
            new ScaleItem("🪨", 30, "item-rock"),
            new ScaleItem("🔧", 20, "item-tool"),
            new ScaleItem("🪶", 10, "item-feather")
        );
        model.addAttribute("scaleItems", items);
    }
}