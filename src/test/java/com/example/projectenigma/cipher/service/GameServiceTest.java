package com.example.projectenigma.cipher.service;

import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
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
 * GameServiceのビジネスロジックを検証する単体テスト。
 * 正誤判定のロジックや、DBへの保存が正しく行われるかを確認する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameProgressRepository gameProgressRepository;

    @InjectMocks
    private GameService gameService;

    /**
     * 正解の場合のテスト。
     * - trueが返ること
     * - ステージ番号が +1 されること
     * - 変更がリポジトリに保存されること
     */
    @Test
    @DisplayName("checkAnswer: 正解(apple)の場合 - trueを返し、ステージを進めて保存する")
    void testCheckAnswer_Correct() {
        // 1. 準備 (Given)
        String userId = "user-123";
        GameProgress progress = new GameProgress();
        progress.setUserId(userId);
        progress.setCurrentStageId(1); // 現在ステージ1

        when(gameProgressRepository.findById(userId)).thenReturn(Optional.of(progress));

        // 2. 実行 (When)
        boolean result = gameService.checkAnswer(userId, "apple");

        // 3. 検証 (Then)
        assertTrue(result, "正解なのでtrueが返るはず");
        assertEquals(2, progress.getCurrentStageId(), "ステージが2に進んでいるはず");
        
        // saveメソッドが1回呼ばれたか確認（これが大事！）
        verify(gameProgressRepository, times(1)).save(progress);
    }

    /**
     * 大文字小文字の違いを許容するかのテスト。
     * - "Apple" や "APPLE" でも正解になること
     */
    @Test
    @DisplayName("checkAnswer: 正解(大文字混じり Apple)の場合 - trueを返す(大文字小文字無視)")
    void testCheckAnswer_CaseInsensitive() {
        // 1. 準備
        String userId = "user-123";
        GameProgress progress = new GameProgress();
        progress.setUserId(userId);
        progress.setCurrentStageId(1);

        when(gameProgressRepository.findById(userId)).thenReturn(Optional.of(progress));

        // 2. 実行
        boolean result = gameService.checkAnswer(userId, "Apple");

        // 3. 検証
        assertTrue(result, "大文字混じりでも正解になるはず");
        verify(gameProgressRepository, times(1)).save(any());
    }

    /**
     * 不正解の場合のテスト。
     * - falseが返ること
     * - ステージ番号が変わらないこと
     * - DB保存が走らないこと
     */
    @Test
    @DisplayName("checkAnswer: 不正解の場合 - falseを返し、ステージは進まない")
    void testCheckAnswer_Wrong() {
        // 1. 準備
        String userId = "user-123";
        GameProgress progress = new GameProgress();
        progress.setUserId(userId);
        progress.setCurrentStageId(1);

        when(gameProgressRepository.findById(userId)).thenReturn(Optional.of(progress));

        // 2. 実行
        boolean result = gameService.checkAnswer(userId, "banana");

        // 3. 検証
        assertFalse(result, "不正解なのでfalseが返るはず");
        assertEquals(1, progress.getCurrentStageId(), "ステージは1のままのはず");
        
        // saveメソッドが呼ばれていないことを確認（無駄な保存を防ぐ）
        verify(gameProgressRepository, never()).save(any());
    }
}