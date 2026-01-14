package com.example.projectenigma.cipher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stage 1: 天秤アイテムの定義クラス。
 * HTMLにハードコードされていた重さやクラス名を管理する。
 * 
 * 例: 名前、重さ、CSSクラス名
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScaleItem {
    private String name;      // 表示アイコン (📦, 💧 etc)
    private int weight;       // 重さ (50, 40 etc)
    private String cssClass;  // CSSクラス名 (item-box, item-water etc)
}