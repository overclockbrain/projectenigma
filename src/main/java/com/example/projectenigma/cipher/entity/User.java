package com.example.projectenigma.cipher.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * ユーザー情報を表すエンティティクラス。
 * 保持するデータ
 * - ユーザーID
 * - 作成日時
 * データベースの "users" テーブルにマッピングされる。
 * Lombokの@Dataアノテーションを使用して、ゲッター、セッター、toString、equals、hashCodeメソッドを自動生成する。
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    private LocalDateTime createdAt;
}