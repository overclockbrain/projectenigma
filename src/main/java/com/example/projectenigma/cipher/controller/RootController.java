package com.example.projectenigma.cipher.controller;

import com.example.projectenigma.cipher.constant.ViewConst;
import com.example.projectenigma.cipher.constant.PathConst;
import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.service.AuthService;
import com.example.projectenigma.cipher.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * トップページおよびルートアクセスの制御を行うコントローラー。
 *
 * @author R.Morioka
 * @version 1.2 (Service利用へリファクタリング)
 * @since 1.0
 */
@Controller
@RequiredArgsConstructor
public class RootController {

    private final AuthService authService;
    private final GameService gameService;

    /**
     * トップページを表示する。
     * アクセス時に自動でユーザー認証（Cookie確認・新規作成）を行う。
     *
     * @param model    画面に渡すデータ
     * @param request  リクエスト情報
     * @param response レスポンス情報（Cookie書き込み用）
     * @return テンプレート名 (index)
     */
    @GetMapping(PathConst.ROOT)
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
        User user = authService.authOrCreateUser(request, response);

        if (user != null) {
            try {
                GameProgress progress = gameService.getProgress(user.getId());
                model.addAttribute("progress", progress);
            } catch (Exception e) {
                // 万が一データ不整合があった場合のケア
                model.addAttribute("progress", null);
            }
        }

        return ViewConst.VIEW_INDEX;
    }
}