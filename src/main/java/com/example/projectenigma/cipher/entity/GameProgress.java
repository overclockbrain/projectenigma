package com.example.projectenigma.cipher.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * ゲームの進行状況を表すエンティティクラス。
 * 保持するデータ
 * - ユーザーID
 * - 現在のステージID
 * - 総経過時間（秒）
 * - 最終更新日時
 * データベースの "game_progress" テーブルにマッピングされる。
 * Lombokの@Dataアノテーションを使用して、ゲッター、セッター、toString、equals、hashCodeメソッドを自動生成する。
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Data
@Entity
@Table(name = "game_progress")
public class GameProgress {
    @Id
    private String userId;
    private Integer currentStageId;
    private Long totalElapsedSeconds;
    private LocalDateTime lastUpdated;
}