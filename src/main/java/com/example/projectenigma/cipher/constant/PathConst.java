package com.example.projectenigma.cipher.constant;

/**
 * URLパス（マッピング用）を管理する定数クラス。
 * Controllerの@RequestMappingなどで使用する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
public class PathConst {

    /** インスタンス化禁止 */
    private PathConst() {}

    /** ルートパス */
    public static final String ROOT = "/";

    /** ゲームプレイ画面のベースパス */
    public static final String PLAY = "/play";

    /** 解答送信のアクションパス（相対パス） */
    public static final String ANSWER = "/answer";
}