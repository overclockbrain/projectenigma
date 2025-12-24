package com.example.projectenigma.cipher.controller;

import com.example.projectenigma.cipher.constant.PathConst;
import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.repository.RiddleRepository;
import com.example.projectenigma.cipher.service.AuthService;
import com.example.projectenigma.cipher.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GameControllerの単体テストクラス。
 * MockMvcを使用してHTTPリクエストをシミュレートし、
 * コントローラーの挙動（画面遷移、モデルの受け渡し、リダイレクト等）を検証する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /** 認証サービスのモック */
    @MockitoBean
    private AuthService authService;

    /** 進捗リポジトリのモック */
    @MockitoBean
    private GameProgressRepository gameProgressRepository;

    /** ゲームロジックサービスのモック */
    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private RiddleRepository riddleRepository;

    /**
     * GET /play の正常系テスト。
     * ユーザーと進捗が存在する場合、ゲーム画面が正しく表示され、
     * 必要なオブジェクト（user, progress, answerForm）がModelに格納されていることを確認する。
     */
    @Test
    @DisplayName("GET /play: 正常系 - 画面が表示され、フォームが渡される")
    void testPlayPage_Success() throws Exception {
        // 1. 準備 (Given)
        User mockUser = new User();
        mockUser.setId("test-user");
        GameProgress mockProgress = new GameProgress();
        mockProgress.setUserId("test-user");
        mockProgress.setCurrentStageId(1);

        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        when(gameProgressRepository.findById("test-user")).thenReturn(Optional.of(mockProgress));

        // 2. 実行と検証 (When & Then)
        mockMvc.perform(get("/play"))
                .andExpect(status().isOk())
                .andExpect(view().name("play"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("progress"))
                // フォーム入力用の空オブジェクトが渡されているか重要チェック
                .andExpect(model().attributeExists("answerForm"));
    }

    /**
     * POST /play/answer の正常系（正解）テスト。
     * GameServiceが true を返した場合、成功メッセージ付きで
     * ゲーム画面へリダイレクトされることを確認する。
     */
    @Test
    @DisplayName("POST /play/answer: 正解の場合 - メッセージ付きでリダイレクトされる")
    void testSubmitAnswer_Correct() throws Exception {
        // 1. 準備 (Given)
        User mockUser = new User();
        mockUser.setId("test-user");

        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        // 正解(true)を返すように仕込む
        when(gameService.checkAnswer(eq("test-user"), eq("apple"))).thenReturn(true);

        // 2. 実行と検証 (When & Then)
        mockMvc.perform(post("/play/answer")
                        .param("answer", "apple")
                        // .with(csrf()) // CSRF対策トークン（Security導入時用）
            )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/play"))
                .andExpect(flash().attribute("alertClass", "success"));
    }

    /**
     * POST /play/answer の準正常系（不正解）テスト。
     * GameServiceが false を返した場合、エラーメッセージ付きで
     * ゲーム画面へリダイレクトされることを確認する。
     */
    @Test
    @DisplayName("POST /play/answer: 不正解の場合 - エラーメッセージ付きでリダイレクトされる")
    void testSubmitAnswer_Wrong() throws Exception {
        // 1. 準備 (Given)
        User mockUser = new User();
        mockUser.setId("test-user");

        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        // 不正解(false)を返すように仕込む
        when(gameService.checkAnswer(eq("test-user"), any())).thenReturn(false);

        // 2. 実行と検証 (When & Then)
        mockMvc.perform(post("/play/answer")
                        .param("answer", "wrong_answer")
                        // .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/play"))
                .andExpect(flash().attribute("alertClass", "error"));
    }

    /**
     * POST /play/restart のテスト。
     * ゲームのリスタート処理が実行され、プレイ画面へリダイレクトされることを確認する。
     * 
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */

    @Test
    @DisplayName("POST /play/restart: リスタート処理が実行され、プレイ画面へリダイレクトされる")
    void testRestartGame() throws Exception {
        // 1. 準備
        String userId = "test-user-id";
        User mockUser = new User();
        mockUser.setId(userId);
        
        // authServiceがユーザーを返すようにモック
        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);

        // 2. 実行 & 検証
        mockMvc.perform(post("/play" + PathConst.RESTART) // /play/restart
                        // .with(csrf()) // セキュリティ入れたらコメントアウト外す
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/play")); // リダイレクト先確認

        // Serviceの resetGame がちゃんと1回呼ばれたか確認
        verify(gameService, times(1)).resetGame(userId);
    }
}