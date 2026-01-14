package com.example.projectenigma.cipher.controller;

import com.example.projectenigma.cipher.constant.PathConst;
import com.example.projectenigma.cipher.constant.ViewConst;
import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.Riddle;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.service.AuthService;
import com.example.projectenigma.cipher.service.GameService;
import com.example.projectenigma.cipher.strategy.Stage1Strategy;
import com.example.projectenigma.cipher.strategy.Stage2Strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // ★ここが変更点
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
 * @version 1.3 (MockitoBean対応 & 時間保存対応)
 * @since 1.0
 */
@WebMvcTest(GameController.class)
@Import({Stage1Strategy.class, Stage2Strategy.class})
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /** 認証サービスのモック */
    @MockitoBean // ★変更
    private AuthService authService;

    /** 進捗リポジトリのモック */
    @MockitoBean // ★変更
    private GameProgressRepository gameProgressRepository;

    /** ゲームロジックサービスのモック */
    @MockitoBean // ★変更
    private GameService gameService;

    // テストで使う共通のダミーデータ
    private User mockUser;
    private GameProgress mockProgress;
    private Riddle mockRiddle;

    @BeforeEach
    void setUp() {
        // 1. ダミーデータの作成
        mockUser = new User();
        mockUser.setId("test-user");

        mockProgress = new GameProgress();
        mockProgress.setUserId("test-user");
        mockProgress.setCurrentStageId(1);
        mockProgress.setTotalElapsedSeconds(0L); 

        mockRiddle = new Riddle(1, "apple", "hint");

        // 2. 基本的なモックの挙動をセット
        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        when(gameService.getProgress(any())).thenReturn(mockProgress);
        when(gameService.getCurrentRiddle(any())).thenReturn(mockRiddle);
    }

    /**
     * GET /play の正常系テスト。
     * ユーザーと進捗が存在する場合、ゲーム画面が正しく表示され、
     * 必要なオブジェクト（user, progress, answerForm）がModelに格納されていることを確認する。
     * 
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
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
                .andExpect(model().attributeExists("answerForm"));
    }

    /**
     * POST /play/answer の正常系（正解）テスト。
     * GameServiceが true を返した場合、成功メッセージ付きで
     * ゲーム画面へリダイレクトされることを確認する。
     * 
     * @author R.Morioka
     * @version 1.1 (時間引数追加)
     * @since 1.0
     */
    @Test
    @DisplayName("POST /play/answer: 正解の場合 - メッセージ付きでリダイレクトされる")
    void testSubmitAnswer_Correct() throws Exception {
        // 1. 準備 (Given)
        User mockUser = new User();
        mockUser.setId("test-user");

        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        
        // ★修正: 第3引数(elapsedSeconds)を any() で受け入れるように変更
        when(gameService.checkAnswer(eq("test-user"), eq("apple"), any())).thenReturn(true);

        // 2. 実行と検証 (When & Then)
        mockMvc.perform(post("/play/answer")
                .param("answer", "apple")
                .param("elapsedSeconds", "120") // ★念のため送信データにも含めておく
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/play"))
                .andExpect(flash().attribute("alertClass", "success"));
    }

    /**
     * POST /play/answer の準正常系（不正解）テスト。
     * GameServiceが false を返した場合、エラーメッセージ付きで
     * ゲーム画面へリダイレクトされることを確認する。
     * 
     * @author R.Morioka
     * @version 1.1 (時間引数追加)
     * @since 1.0
     */
    @Test
    @DisplayName("POST /play/answer: 不正解の場合 - エラーメッセージ付きでリダイレクトされる")
    void testSubmitAnswer_Wrong() throws Exception {
        // 1. 準備 (Given)
        User mockUser = new User();
        mockUser.setId("test-user");

        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        
        // ★修正: 第3引数(elapsedSeconds)を any() で受け入れるように変更
        when(gameService.checkAnswer(eq("test-user"), any(), any())).thenReturn(false);

        // 2. 実行と検証 (When & Then)
        mockMvc.perform(post("/play/answer")
                .param("answer", "wrong_answer")
                .param("elapsedSeconds", "130") // ★念のため送信データにも含めておく
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
        mockMvc.perform(post("/play" + PathConst.RESTART))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/play"));

        verify(gameService, times(1)).resetGame(userId);
    }

    /**
     * Stage 1（天秤パズル）のプレイ画面表示テスト。
     * * 期待値:
     * ステータスコードが200 (OK) であること
     * View名が "play" であること
     * Modelに "scaleItems" (天秤アイテムリスト) が含まれていること
     * 
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Test
    @DisplayName("Stage1表示時: 天秤アイテム(scaleItems)がModelに渡されること")
    void testPlayPage_Stage1_LoadsItems() throws Exception {
        // Given
        User mockUser = new User();
        mockUser.setId("test-user");
        
        GameProgress mockProgress = new GameProgress();
        mockProgress.setUserId("test-user");
        mockProgress.setCurrentStageId(1); // Stage 1

        Riddle mockRiddle = new Riddle(1, "5050", "Hint");

        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        when(gameService.getProgress("test-user")).thenReturn(mockProgress);
        when(gameService.getCurrentRiddle("test-user")).thenReturn(mockRiddle);

        // When & Then
        mockMvc.perform(get(PathConst.PLAY))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConst.VIEW_PLAY))
                .andExpect(model().attributeExists("scaleItems"));
    }

    /**
     * Stage 2（人狼パズル）のプレイ画面表示テスト。
     * * 期待値:
     * ステータスコードが200 (OK) であること
     * View名が "play" であること
     * Modelに "suspects" (容疑者リスト) が含まれていること
     * 
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Test
    @DisplayName("Stage2表示時: 容疑者リスト(suspects)がModelに渡されること")
    void testPlayPage_Stage2_LoadsSuspects() throws Exception {
        // Given
        User mockUser = new User();
        mockUser.setId("test-user");
        
        GameProgress mockProgress = new GameProgress();
        mockProgress.setUserId("test-user");
        mockProgress.setCurrentStageId(2); // Stage 2

        Riddle mockRiddle = new Riddle(2, "B", "Hint");

        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        when(gameService.getProgress("test-user")).thenReturn(mockProgress);
        when(gameService.getCurrentRiddle("test-user")).thenReturn(mockRiddle);

        // When & Then
        mockMvc.perform(get(PathConst.PLAY))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConst.VIEW_PLAY))
                .andExpect(model().attributeExists("suspects"));
    }
}