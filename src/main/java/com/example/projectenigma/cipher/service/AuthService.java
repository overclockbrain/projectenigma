package com.example.projectenigma.cipher.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.repository.UserRepository;
import com.example.projectenigma.cipher.util.CookieUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * ユーザー認証および登録処理を行うサービス実装クラス。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final GameProgressRepository gameProgressRepository;

    /**
     * リクエストからユーザーを特定、または新規登録を行います。
     * CookieにIDがあればDB検索、なければ新規発行してCookieとDBに保存します。
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 特定または作成されたUserオブジェクト
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Transactional
    public User authOrCreateUser(HttpServletRequest request, HttpServletResponse response) {
        // 1. CookieからIDを探す
        Optional<String> cookieId = CookieUtil.getCookieValue(request, CookieUtil.COOKIE_NAME);

        if (cookieId.isPresent()) {
            // 2. IDがあればDBから検索
            Optional<User> user = userRepository.findById(cookieId.get());
            if (user.isPresent()) {
                // 既存ユーザーが見つかったらそれを返す
                return user.get();
            }
            // CookieはあるけどDBにない（データ消えた？）場合は新規作成へ流す
        }

        // 3. 新規ユーザー作成
        return createNewUser(response);
    }

    /**
     * 新規ユーザーを作成し、DB保存とCookie設定を行います。
     *
     * @param response HttpServletResponse
     * @return 作成されたUserオブジェクト
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    private User createNewUser(HttpServletResponse response) {
        String newUserId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        // User保存
        User newUser = new User();
        newUser.setId(newUserId);
        newUser.setCreatedAt(now);
        userRepository.save(newUser);

        // 初期進捗保存 (Stage 1, タイム0)
        GameProgress newProgress = new GameProgress();
        newProgress.setUserId(newUserId);
        newProgress.setCurrentStageId(1);
        newProgress.setTotalElapsedSeconds(0L);
        newProgress.setLastUpdated(now);
        gameProgressRepository.save(newProgress);

        // CookieにIDを焼く
        CookieUtil.setUserIdCookie(response, newUserId);

        return newUser;
    }
}