package com.example.projectenigma.cipher.dto;

import lombok.Data;

/**
 * Stage 4 (Gravity Architect) の設定データを保持するDTO。
 * Riddleエンティティの stageData (JSON) からマッピングされる。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Data
public class Stage4Config {
    /**
     * 盤面グリッド
     * 0: 空白, 1: 赤坂, 2: 青坂, 9: ゴール
     */
    private int[][] grid;

    /**
     * スタート位置 [row, col]
     * 例: [0, 2]
     */
    private int[] start;
}