package com.example.projectenigma.cipher.strategy;

import org.springframework.ui.Model;

public interface StageStrategy {
    /**
     * 担当するステージIDを返す
     */
    int getStageId();

    /**
     * Modelに必要なデータをセットする
     */
    void setupModel(Model model);
}