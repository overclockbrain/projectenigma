package com.example.projectenigma.cipher.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CookieUtilの単体テスト。
 * ServletAPI（Request/Response）をモック化して、
 * Cookieの取得・設定ロジックが正しいか検証する。
 */
class CookieUtilTest {

    @Test
    @DisplayName("getCookieValue: 目的のCookieが存在する場合、その値を返す")
    void testGetCookieValue_Found() {
        // 1. 準備
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = {
                new Cookie("dummy", "ignore"),
                new Cookie(CookieUtil.COOKIE_NAME, "target-value")
        };
        when(request.getCookies()).thenReturn(cookies);

        // 2. 実行
        Optional<String> result = CookieUtil.getCookieValue(request, CookieUtil.COOKIE_NAME);

        // 3. 検証
        assertTrue(result.isPresent());
        assertEquals("target-value", result.get());
    }

    @Test
    @DisplayName("getCookieValue: Cookieが1つもない(null)場合、Emptyを返す")
    void testGetCookieValue_NullCookies() {
        // 1. 準備
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        // 2. 実行
        Optional<String> result = CookieUtil.getCookieValue(request, CookieUtil.COOKIE_NAME);

        // 3. 検証
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCookieValue: Cookieはあるが目的のものがない場合、Emptyを返す")
    void testGetCookieValue_NotFound() {
        // 1. 準備
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("other", "value") };
        when(request.getCookies()).thenReturn(cookies);

        // 2. 実行
        Optional<String> result = CookieUtil.getCookieValue(request, CookieUtil.COOKIE_NAME);

        // 3. 検証
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("setUserIdCookie: 正しい設定でCookieがレスポンスに追加される")
    void testSetUserIdCookie() {
        // 1. 準備
        HttpServletResponse response = mock(HttpServletResponse.class);
        String userId = "new-user-id";

        // 2. 実行
        CookieUtil.setUserIdCookie(response, userId);

        // 3. 検証: addCookieに渡された引数を捕まえる
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());

        Cookie savedCookie = cookieCaptor.getValue();
        assertEquals(CookieUtil.COOKIE_NAME, savedCookie.getName());
        assertEquals(userId, savedCookie.getValue());
        assertEquals("/", savedCookie.getPath());
        assertTrue(savedCookie.isHttpOnly());
        assertTrue(savedCookie.getMaxAge() > 0);
    }
}