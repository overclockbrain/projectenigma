package com.example.projectenigma.cipher.service;

import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.Riddle;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.repository.RiddleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ゲームの進行管理や正誤判定を行うサービスクラス。
 * ユーザーの解答をDBのマスタデータと照合し、ステージ進行を制御する。
 *
 * @author R.Morioka
 * @version 1.4
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameProgressRepository gameProgressRepository;
    private final RiddleRepository riddleRepository;

    /**
     * 指定されたユーザーの現在のゲーム進捗状況を取得する。
     *
     * @param userId ユーザーID
     * @return ユーザーのゲーム進捗情報
     * @throws IllegalStateException ユーザーが存在しない場合
     */
    @Transactional(readOnly = true)
    public GameProgress getProgress(String userId) {
        return gameProgressRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));
    }

    /**
     * 指定されたユーザーの現在挑戦中の問題データを取得する。
     *
     * @param userId ユーザーID
     * @return 現在のステージの問題データ。存在しない場合（クリア後など）はnull
     */
    @Transactional(readOnly = true)
    public Riddle getCurrentRiddle(String userId) {
        GameProgress progress = getProgress(userId);
        return riddleRepository.findById(progress.getCurrentStageId())
                .orElse(null);
    }

    /**
     * ユーザーの解答をチェックし、正解ならステージを進め、経過時間を更新する。
     * 
     * @param userId ユーザーID
     * @param answer ユーザーの解答
     * @param elapsedSeconds クライアントから送られてきた現在の総経過時間（秒） // ★追加
     * @return 正解なら true
     */
    @Transactional
    public boolean checkAnswer(String userId, String answer, Long elapsedSeconds) {
        GameProgress progress = getProgress(userId);
        int currentStage = progress.getCurrentStageId();

        // ★ここで時間を更新！
        // (nullチェックは念のため入れてるけど、基本はフロントから送られてくる前提)
        if (elapsedSeconds != null) {
            progress.setTotalElapsedSeconds(elapsedSeconds);
        }

        String correctAnswer = riddleRepository.findById(currentStage)
                .map(Riddle::getAnswer)
                .orElse(null);

        if (correctAnswer == null) {
            // 不正解でも時間は進んでるから保存してもええかもしれんけど、
            // とりあえず今回は「進捗保存」としてここでsave呼んどくのが安全
            gameProgressRepository.save(progress); 
            return false;
        }

        boolean isCorrect = correctAnswer.equalsIgnoreCase(answer.trim());

        if (isCorrect) {
            progress.setCurrentStageId(currentStage + 1);
        }
        
        // 正解・不正解に関わらず、時間更新のためにsaveは必須
        gameProgressRepository.save(progress);

        return isCorrect;
    }

    /**
     * ユーザーの進捗を初期化（リスタート）する。
     * ステージを1に戻し、経過時間をリセットする。
     *
     * @param userId ユーザーID
     */
    @Transactional
    public void resetGame(String userId) {
        GameProgress progress = getProgress(userId);
        progress.setCurrentStageId(1);
        progress.setTotalElapsedSeconds(0L);
        gameProgressRepository.save(progress);
    }
}