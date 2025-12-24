package com.example.projectenigma.cipher.service;

import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.Riddle;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.repository.RiddleRepository;
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
 * RiddleRepositoryのモックを使用し、DBから正解を取得するフローを検証する。
 *
 * @author R.Morioka
 * @version 1.1
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameProgressRepository gameProgressRepository;

    /** 問題データのモック用 */
    @Mock
    private RiddleRepository riddleRepository;

    @InjectMocks
    private GameService gameService;

    /**
     * 正解の場合のテスト。
     * - Riddleリポジトリから正解(flash)が返される
     * - ユーザー入力と一致し true を返す
     * - 進捗が保存される
     * 
     * @author R.Morioka
     * @version 1.1
     * @since 1.0
     */
    @Test
    @DisplayName("checkAnswer: 正解(flash)の場合 - trueを返し、ステージを進めて保存する")
    void testCheckAnswer_Correct() {
        // 1. 準備 (Given)
        String userId = "user-123";
        GameProgress progress = new GameProgress();
        progress.setUserId(userId);
        progress.setCurrentStageId(1);
        
        // 進捗のモック
        when(gameProgressRepository.findById(userId)).thenReturn(Optional.of(progress));
        
        // ★Riddle(正解データ)のモック
        Riddle mockRiddle = new Riddle(1, "flash", "hint text");
        when(riddleRepository.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 2. 実行 (When)
        boolean result = gameService.checkAnswer(userId, "flash");

        // 3. 検証 (Then)
        assertTrue(result, "正解なのでtrueが返るはず");
        assertEquals(2, progress.getCurrentStageId(), "ステージが2に進んでいるはず");
        verify(gameProgressRepository, times(1)).save(progress);
    }

    /**
     * 不正解の場合のテスト。
     * - Riddleリポジトリから正解は返るが、入力と一致しない
     * 
     * @author R.Morioka
     * @version 1.1
     * @since 1.0
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
        
        // 正解は "flash" だが...
        Riddle mockRiddle = new Riddle(1, "flash", "hint text");
        when(riddleRepository.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 2. 実行 (入力は "banana")
        boolean result = gameService.checkAnswer(userId, "banana");

        // 3. 検証
        assertFalse(result);
        assertEquals(1, progress.getCurrentStageId());
        verify(gameProgressRepository, never()).save(any());
    }

    /**
     * 問題データが存在しない場合のテスト。
     * - Riddleリポジトリが空を返すケース
     * 
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Test
    @DisplayName("checkAnswer: 問題データなしの場合 - falseを返す")
    void testCheckAnswer_NoRiddleData() {
        // 1. 準備
        String userId = "user-123";
        GameProgress progress = new GameProgress();
        progress.setUserId(userId);
        progress.setCurrentStageId(1);

        when(gameProgressRepository.findById(userId)).thenReturn(Optional.of(progress));
        
        // Riddleリポジトリは空を返す
        when(riddleRepository.findById(1)).thenReturn(Optional.empty());

        // 2. 実行
        boolean result = gameService.checkAnswer(userId, "anyanswer");

        // 3. 検証
        assertFalse(result);
        assertEquals(1, progress.getCurrentStageId());
        verify(gameProgressRepository, never()).save(any());
    }
}