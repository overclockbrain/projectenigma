package com.example.projectenigma.cipher.entity;

import jakarta.persistence.Column;
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
 * @version 1.1
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

    // ★追加: ステージ固有の設定データ (JSONを入れる想定)
    // 長い文字列になる可能性があるから TEXT 型などを指定推奨やけど、
    // H2とMySQL両対応なら単純に長さ指定だけでもいけることが多い
    @Column(length = 4096) 
    private String stageData;
    
    // 今回は手動で3引数コンストラクタを残しつつ、4引数はLombokに任せる
    
    public Riddle(Integer stageId, String answer, String hint) {
        this.stageId = stageId;
        this.answer = answer;
        this.hint = hint;
    }
}