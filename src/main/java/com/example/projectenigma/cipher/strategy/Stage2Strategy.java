package com.example.projectenigma.cipher.strategy;

import com.example.projectenigma.cipher.dto.Suspect;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

/**
 * Stage 2 (人狼パズル) のデータセットアップ戦略。
 */
@Component
public class Stage2Strategy implements StageStrategy {

    @Override
    public int getStageId() {
        return 2;
    }

    /**
     * Stage 2（人狼パズル）のプレイ画面表示テスト。
     * 
     * 期待値:
     * ステータスコードが200 (OK) であること
     * View名が "play" であること
     * Modelに "suspects" (容疑者リスト) が含まれていること
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Override
    public void setupModel(Model model) {
        List<Suspect> suspects = Arrays.asList(
            new Suspect("E", "👨‍🌾", "stage2.quote.e"),
            new Suspect("A", "👱‍♂️", "stage2.quote.a"),
            new Suspect("F", "👩‍🍳", "stage2.quote.f"),
            new Suspect("G", "👮‍♂️", "stage2.quote.g"),
            new Suspect("C", "👴",   "stage2.quote.c"),
            new Suspect("H", "🧙‍♀️", "stage2.quote.h"),
            new Suspect("B", "👩‍🦰", "stage2.quote.b"),
            new Suspect("I", "👷‍♂️", "stage2.quote.i"),
            new Suspect("J", "👩‍🎤", "stage2.quote.j"),
            new Suspect("D", "👦",   "stage2.quote.d"),
            new Suspect("K", "🕵️‍♂️", "stage2.quote.k"),
            new Suspect("L", "🧟",   "stage2.quote.l")
        );
        model.addAttribute("suspects", suspects);
    }
}