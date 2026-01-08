package com.example.projectenigma.cipher.controller;

import com.example.projectenigma.cipher.constant.PathConst;
import com.example.projectenigma.cipher.constant.ViewConst;
import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.service.AuthService;
import com.example.projectenigma.cipher.service.GameService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RootControllerの単体テストクラス。
 * トップページへのアクセスとモデルの受け渡しを検証する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@WebMvcTest(RootController.class)
public class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private GameProgressRepository gameProgressRepository;

    @MockitoBean // ★ ここ追加！これがないと起動せえへん
    private GameService gameService;

    /**
     * トップページ（/）への正常アクセスをテストする。
     * 期待値:
     * - ステータスコード 200 (OK)
     * - ビュー名 "index"
     * - Modelに "user", "progress" が含まれていること
     *
     * @author R.Morioka
     * @version 1.1
     * @since 1.0
     */
    @Test
    @DisplayName("正常系: トップページにアクセスするとindexテンプレートが表示される")
    void testIndexPage_Success() throws Exception {
        // 1. 準備 (Given)
        User mockUser = new User();
        mockUser.setId("root-test-user");

        GameProgress mockProgress = new GameProgress();
        mockProgress.setUserId("root-test-user");
        mockProgress.setCurrentStageId(1);

        // モックの挙動定義
        when(authService.authOrCreateUser(any(), any())).thenReturn(mockUser);
        // ★ RepositoryじゃなくてServiceをモックする
        when(gameService.getProgress("root-test-user")).thenReturn(mockProgress);

        // 2. 実行と検証
        mockMvc.perform(get(PathConst.ROOT))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConst.VIEW_INDEX))
                .andExpect(model().attributeExists("progress"));
    }
}