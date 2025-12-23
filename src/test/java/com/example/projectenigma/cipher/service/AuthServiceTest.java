package com.example.projectenigma.cipher.service;

import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.repository.UserRepository;
import com.example.projectenigma.cipher.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthServiceの単体テスト。
 * Cookieの有無に応じたユーザー特定・作成ロジックを検証する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameProgressRepository gameProgressRepository;

    @InjectMocks
    private AuthService authService;

    // HTTPのリクエスト・レスポンスもモック化するで
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("authOrCreateUser: CookieもDBデータもある場合 - 既存ユーザーを返す")
    void testAuth_ExistingUser() {
        // 1. 準備 (Given)
        String userId = "existing-user-id";
        Cookie cookie = new Cookie(CookieUtil.COOKIE_NAME, userId);
        User existUser = new User();
        existUser.setId(userId);

        // リクエストにCookieが含まれている振る舞い
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        // DB検索でユーザーが見つかる振る舞い
        when(userRepository.findById(userId)).thenReturn(Optional.of(existUser));

        // 2. 実行 (When)
        User result = authService.authOrCreateUser(request, response);

        // 3. 検証 (Then)
        assertEquals(userId, result.getId());
        // 新規保存は呼ばれてないはず
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("authOrCreateUser: Cookieがない場合 - 新規ユーザーを作成してCookie発行")
    void testAuth_NewUser_NoCookie() {
        // 1. 準備 (Given)
        // Cookieは空っぽ
        when(request.getCookies()).thenReturn(null);

        // 2. 実行 (When)
        User result = authService.authOrCreateUser(request, response);

        // 3. 検証 (Then)
        assertNotNull(result.getId(), "新しいIDが発行されてるはず");
        
        // 保存処理が走ったか
        verify(userRepository, times(1)).save(any(User.class));
        verify(gameProgressRepository, times(1)).save(any()); // 進捗初期化も大事
        
        // レスポンスにCookieを追加したか（これがAuthServiceの肝！）
        verify(response, times(1)).addCookie(any(Cookie.class));
    }
    
    @Test
    @DisplayName("authOrCreateUser: CookieはあるけどDBにない場合 - 新規ユーザーを作成(再発行)")
    void testAuth_CookieExists_But_NoDB() {
        // 1. 準備
        String oldId = "ghost-user";
        Cookie cookie = new Cookie(CookieUtil.COOKIE_NAME, oldId);
        
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        // DB探しても見つからん！(Optional.empty)
        when(userRepository.findById(oldId)).thenReturn(Optional.empty());

        // 2. 実行
        User result = authService.authOrCreateUser(request, response);

        // 3. 検証
        assertNotNull(result);
        assertNotEquals(oldId, result.getId(), "古いIDとは別のIDになってるはず");
        
        // やっぱり新規作成ルートに入るはず
        verify(userRepository, times(1)).save(any());
        verify(response, times(1)).addCookie(any());
    }
}