package com.example.projectenigma.cipher.controller;

import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

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

    /**
     * トップページ（/）への正常アクセスをテストする。
     * 期待値:
     * - ステータスコード 200 (OK)
     * - ビュー名 "index"
     * - Modelに "user", "progress" が含まれていること
     *
     * @author R.Morioka
     * @version 1.0
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
        when(gameProgressRepository.findById("root-test-user")).thenReturn(Optional.of(mockProgress));

        // 2. 実行と検証 (When & Then)
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("progress"))
                .andExpect(model().attribute("user", mockUser)); // 中身が一致するかも確認できるで
    }
}