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
import static org.mockito.Mockito.*;

/**
 * GameServiceのビジネスロジックを検証する単体テスト。
 * RiddleRepositoryのモックを使用し、DBから正解を取得するフローを検証する。
 *
 * @author R.Morioka
 * @version 1.3
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
     * @version 1.3 (時間引数追加)
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
        // ★第3引数に時間を追加 (テストなので適当な値 100L でOK)
        boolean result = gameService.checkAnswer(userId, "flash", 100L);

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
     * @version 1.2 (時間引数追加)
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
        // ★第3引数に時間を追加
        boolean result = gameService.checkAnswer(userId, "banana", 50L);

        // 3. 検証
        assertFalse(result);
        assertEquals(1, progress.getCurrentStageId());
        // ★不正解でも時間更新のためにsaveは呼ばれるようになったので、never()から修正するか、あるいは「進捗が進んでないこと」だけを確認
        // 今回の修正で「不正解でも時間を保存する」ロジックに変えたなら、save(progress)は呼ばれるのが正解や。
        // なので、↓の行はコメントアウトするか、 verify(..., times(1)) に変えるべきやな。
        verify(gameProgressRepository, times(1)).save(progress);
    }

    /**
     * 問題データが存在しない場合のテスト。
     * - Riddleリポジトリが空を返すケース
     * 
     * @author R.Morioka
     * @version 1.1 (時間引数追加)
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
        // ★第3引数に時間を追加
        boolean result = gameService.checkAnswer(userId, "anyanswer", 50L);

        // 3. 検証
        assertFalse(result);
        assertEquals(1, progress.getCurrentStageId());
        // ここも同様、時間保存のためにsaveが呼ばれる実装にするなら times(1) になる
        verify(gameProgressRepository, times(1)).save(progress);
    }

    /**
     * 進捗リセットのテスト。
     * - ステージが1に戻り、経過時間が0になることを確認する
     * 
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Test
    @DisplayName("resetGame: 進捗が初期化（ステージ1、タイム0）されて保存される")
    void testResetGame() {
        // 1. 準備 (今の状態はステージ3で時間も経過してる設定)
        String userId = "user-123";
        GameProgress progress = new GameProgress();
        progress.setUserId(userId);
        progress.setCurrentStageId(3);
        progress.setTotalElapsedSeconds(120L);

        when(gameProgressRepository.findById(userId)).thenReturn(Optional.of(progress));

        // 2. 実行
        gameService.resetGame(userId);

        // 3. 検証
        assertEquals(1, progress.getCurrentStageId(), "ステージが1に戻っていること");
        assertEquals(0L, progress.getTotalElapsedSeconds(), "タイムが0にリセットされていること");
        
        // ちゃんと保存（save）が呼ばれたかチェック
        verify(gameProgressRepository, times(1)).save(progress);
    }

    /**
     * 正解時に経過時間が更新されることを確認するテスト。
     * - 正解した場合、ステージが進むとともに経過時間も更新される
     * 
     * @author R.Morioka
     * @version 1.0
     * @since 1.2
     */
    @Test
    @DisplayName("正解時: ステージが進み、かつ経過時間(elapsedSeconds)がDBに保存されること")
    void testCheckAnswer_Correct_UpdatesTime() {
        // Given
        String userId = "user1";
        GameProgress progress = new GameProgress();
        progress.setUserId(userId);
        progress.setCurrentStageId(1);
        progress.setTotalElapsedSeconds(10L); // 元々は10秒

        Riddle riddle = new Riddle(1, "apple", "hint");

        when(gameProgressRepository.findById(userId)).thenReturn(Optional.of(progress));
        when(riddleRepository.findById(1)).thenReturn(Optional.of(riddle));

        // When
        // ★第3引数に「新しい経過時間 (120秒)」を渡す
        boolean result = gameService.checkAnswer(userId, "Apple", 120L);

        // Then
        assertTrue(result);
        assertEquals(2, progress.getCurrentStageId());
        assertEquals(120L, progress.getTotalElapsedSeconds(), "時間が更新されていること");
        
        verify(gameProgressRepository).save(progress);
    }
}