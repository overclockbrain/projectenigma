package com.example.projectenigma.cipher.controller;

import com.example.projectenigma.cipher.constant.ViewConst;
import com.example.projectenigma.cipher.constant.PathConst;
import com.example.projectenigma.cipher.dto.AnswerForm;
import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.Riddle;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.service.AuthService;
import com.example.projectenigma.cipher.service.GameService;
import com.example.projectenigma.cipher.strategy.StageStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ゲームのメイン画面と解答処理を制御するコントローラー。
 * ユーザーの認証、進行状況の取得、問題の表示、解答の判定結果へのリダイレクトを行う。
 *
 * @author R.Morioka
 * @version 1.6
 * @since 1.0
 */
@Controller
@RequestMapping(PathConst.PLAY)
public class GameController {

    private final AuthService authService;
    private final GameService gameService;
    private final MessageSource messageSource;
    private final Map<Integer, StageStrategy> stageStrategies;

    // コンストラクタでListをMapに変換して注入！
    public GameController(
            AuthService authService,
            GameService gameService,
            MessageSource messageSource,
            List<StageStrategy> strategies) {
        
        this.authService = authService;
        this.gameService = gameService;
        this.messageSource = messageSource;
        // List<Strategy> -> Map<StageId, Strategy>
        this.stageStrategies = strategies.stream()
                .collect(Collectors.toMap(StageStrategy::getStageId, s -> s));
    }

    /**
     * ゲームプレイ画面を表示する。
     * 現在のステージに応じた問題データを取得してViewに渡す。
     *
     * @param model    画面モデル
     * @param request  HTTPリクエスト
     * @param response HTTPレスポンス
     * @return プレイ画面のテンプレート名
     */
    @GetMapping
    public String play(Model model, HttpServletRequest request, HttpServletResponse response) {
        User user = authService.authOrCreateUser(request, response);

        GameProgress progress = gameService.getProgress(user.getId());
        Riddle riddle = gameService.getCurrentRiddle(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("progress", progress);
        model.addAttribute("riddle", riddle);

        int stageId = progress.getCurrentStageId();
        if (stageStrategies.containsKey(stageId)) {
            stageStrategies.get(stageId).setupModel(model);
        }

        if (!model.containsAttribute("answerForm")) {
            model.addAttribute("answerForm", new AnswerForm());
        }

        return ViewConst.VIEW_PLAY;
    }

    /**
     * ユーザーからの解答を受け付けて判定を行う。
     * 結果に応じてフラッシュメッセージを設定し、プレイ画面へリダイレクトする。
     *
     * @param answerForm         解答フォーム
     * @param request            HTTPリクエスト
     * @param response           HTTPレスポンス
     * @param redirectAttributes リダイレクト時の属性
     * @param locale             現在のロケール（メッセージ取得用）
     * @return プレイ画面へのリダイレクトパス
     */
    @PostMapping(PathConst.ANSWER)
    public String submitAnswer(@ModelAttribute AnswerForm answerForm,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        User user = authService.authOrCreateUser(request, response);

        boolean isCorrect = gameService.checkAnswer(user.getId(), answerForm.getAnswer(), answerForm.getElapsedSeconds());

        if (isCorrect) {
            String msg = messageSource.getMessage("game.msg.correct", null, locale);
            redirectAttributes.addFlashAttribute("message", msg);
            redirectAttributes.addFlashAttribute("alertClass", "success");
        } else {
            String msg = messageSource.getMessage("game.msg.wrong", null, locale);
            redirectAttributes.addFlashAttribute("message", msg);
            redirectAttributes.addFlashAttribute("alertClass", "error");
        }

        return ViewConst.REDIRECT_PLAY;
    }

    /**
     * ゲームリスタート処理を実行する。
     * 進捗をリセットしてプレイ画面（ステージ1）へリダイレクトする。
     *
     * @param request  HTTPリクエスト
     * @param response HTTPレスポンス
     * @return プレイ画面へのリダイレクトパス
     */
    @PostMapping(PathConst.RESTART)
    public String restartGame(HttpServletRequest request, HttpServletResponse response) {
        User user = authService.authOrCreateUser(request, response);

        gameService.resetGame(user.getId());

        return ViewConst.REDIRECT_PLAY;
    }
}