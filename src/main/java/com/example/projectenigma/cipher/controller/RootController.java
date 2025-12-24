package com.example.projectenigma.cipher.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.service.AuthService;

import java.util.Optional;

/**
 * トップページおよびルートアクセスの制御を行うコントローラー。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequiredArgsConstructor
public class RootController {

    private final AuthService authService;
    private final GameProgressRepository gameProgressRepository;

    /**
     * トップページを表示します。
     * アクセス時に自動でユーザー認証（Cookie確認・新規作成）を行います。
     *
     * @param model    画面に渡すデータ
     * @param request  リクエスト情報
     * @param response レスポンス情報（Cookie書き込み用）
     * @return テンプレート名
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
        // 1. サービスを呼ぶ
        User user = authService.authOrCreateUser(request, response);

        // 2. 進捗状況を取得
        // ※ ここで user が null だと user.getId() でまたエラーになるからガードしとく
        Optional<GameProgress> progressOpt = Optional.empty();
        if (user != null) {
             progressOpt = gameProgressRepository.findById(user.getId());
        }

        // 3. 画面にデータを渡す
        model.addAttribute("user", user);
        model.addAttribute("progress", progressOpt.orElse(null));

        return "index";
    }
}