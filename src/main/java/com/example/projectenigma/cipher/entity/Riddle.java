package com.example.projectenigma.cipher.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* 謎解きの問題定義（正解やヒント）を管理するEntityクラス。
 * DBの "riddle" テーブルに対応し、JSONから読み込まれたデータが格納される。
 * フィールド:
 * stageId: ステージ番号
 * answer: 正解文字列
 * hint: ヒント文
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Riddle {
    @Id
    private Integer stageId; // ステージ番号 (1, 2, 3...)
    private String answer;   // 正解 (flash, apple...)
    private String hint;     // (おまけ) 画面に出すヒント文とか
}