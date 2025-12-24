package com.example.projectenigma.cipher.controller;

import com.example.projectenigma.cipher.constant.ViewConst;
import com.example.projectenigma.cipher.constant.PathConst;
import com.example.projectenigma.cipher.dto.AnswerForm;
import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.Riddle;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.repository.RiddleRepository;
import com.example.projectenigma.cipher.service.AuthService;
import com.example.projectenigma.cipher.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ゲームのメイン画面と解答処理を制御するコントローラー。
 * ユーザーの認証、進行状況の取得、問題の表示、解答の判定結果へのリダイレクトを行う。
 *
 * @author R.Morioka
 * @version 1.4 (共通定数クラス利用)
 * @since 1.0
 */
@Controller
@RequestMapping(PathConst.PLAY)
@RequiredArgsConstructor
public class GameController {
    private final AuthService authService;
    private final GameProgressRepository gameProgressRepository;
    private final GameService gameService;
    
    /** 問題データ（正解やヒント）を取得するためのリポジトリ */
    private final RiddleRepository riddleRepository;

    /**
     * ゲームプレイ画面（GET /play）を表示する。
     *
     * @param model 画面（View）にデータを渡すためのコンテナ
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @return 遷移先のテンプレート名 (ViewConst.VIEW_PLAY)
     */
    @GetMapping
    public String play(Model model, HttpServletRequest request, HttpServletResponse response) {
        User user = authService.authOrCreateUser(request, response);
        
        GameProgress progress = gameProgressRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("No progress"));

        Riddle riddle = riddleRepository.findById(progress.getCurrentStageId())
                .orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("progress", progress);
        model.addAttribute("riddle", riddle);
        
        if (!model.containsAttribute("answerForm")) {
            model.addAttribute("answerForm", new AnswerForm());
        }
        
        // ★定数クラスを使用
        return ViewConst.VIEW_PLAY;
    }

    /**
     * 解答送信（POST /play/answer）を処理する。
     *
     * @param answerForm フォームから送信された解答データ
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param redirectAttributes リダイレクト先にデータを渡すためのオブジェクト
     * @return プレイ画面へのリダイレクト (ViewConst.REDIRECT_PLAY)
     * 
     * @version 1.1
     * @since 1.0
     */
    @PostMapping(PathConst.ANSWER)
    public String submitAnswer(@ModelAttribute AnswerForm answerForm,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
        
        User user = authService.authOrCreateUser(request, response);
        
        boolean isCorrect = gameService.checkAnswer(user.getId(), answerForm.getAnswer());

        if (isCorrect) {
            redirectAttributes.addFlashAttribute("message", "Correct! Next Stage Unlocked! 🎉");
            redirectAttributes.addFlashAttribute("alertClass", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Wrong answer... Try again. 😢");
            redirectAttributes.addFlashAttribute("alertClass", "error");
        }

        // ★定数クラスを使用
        return ViewConst.REDIRECT_PLAY;
    }

    /**
     * ゲームリスタート処理（POST /play/restart）。
     * 進捗をリセットしてプレイ画面へリダイレクトする。
     * 
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @return プレイ画面へのリダイレクト (ViewConst.REDIRECT_PLAY)
     * 
     * @version 1.0
     * @since 1.0
     */
    @PostMapping(PathConst.RESTART)
    public String restartGame(HttpServletRequest request, HttpServletResponse response) {
        User user = authService.authOrCreateUser(request, response);
        
        // 進捗リセット実行
        gameService.resetGame(user.getId());

        // プレイ画面（ステージ1）へリダイレクト
        return ViewConst.REDIRECT_PLAY;
    }
}