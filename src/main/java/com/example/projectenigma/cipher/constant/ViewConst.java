package com.example.projectenigma.cipher.constant;

/**
 * アプリケーション全体の画面名（View）やリダイレクト先を管理する定数クラス。
 * 文字列のハードコーディングを防ぎ、変更を一元管理するために使用する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
public class ViewConst {

    /** インスタンス化禁止（ユーティリティクラスのため） */
    private ViewConst() {}

    // === View名 (HTMLファイル名) ===
    /** トップ画面のテンプレート名 */
    public static final String VIEW_INDEX = "index";
    
    /** ゲームプレイ画面のテンプレート名 */
    public static final String VIEW_PLAY = "play";

    // === リダイレクトパス ===
    // ★文字列結合で PathConst を参照する！
    public static final String REDIRECT_PLAY = "redirect:" + PathConst.PLAY;
}