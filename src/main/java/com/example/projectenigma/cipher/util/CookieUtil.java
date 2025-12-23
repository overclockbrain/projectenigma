package com.example.projectenigma.cipher.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

/**
 * Cookie操作を行うユーティリティクラス。
 * ユーザーID識別用のCookieの取得と設定をサポートします。
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
public class CookieUtil {

    /**
     * Cookieのキー名
     */
    public static final String COOKIE_NAME = "enigma_uid";
    
    /**
     * Cookieの有効期限（秒）: 30日
     */
    private static final int MAX_AGE = 60 * 60 * 24 * 30;

    /**
     * リクエストから指定された名前のCookieの値を取得します。
     *
     * @param request HttpServletRequest
     * @param name    Cookie名
     * @return Cookieの値を持つOptional、存在しない場合はEmpty
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    public static Optional<String> getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    /**
     * レスポンスにユーザーID識別用のCookieを設定します。
     *
     * @param response HttpServletResponse
     * @param userId   設定するユーザーID
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    public static void setUserIdCookie(HttpServletResponse response, String userId) {
        Cookie cookie = new Cookie(COOKIE_NAME, userId);
        cookie.setPath("/");
        cookie.setMaxAge(MAX_AGE);
        cookie.setHttpOnly(true); // JavaScriptからのアクセスを禁止（セキュリティ向上）
        // cookie.setSecure(true); // 本番環境（HTTPS）では有効化推奨
        response.addCookie(cookie);
    }
}