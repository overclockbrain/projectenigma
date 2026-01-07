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
 * @version 1.3 (リポジトリ隠蔽対応)
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
     * ユーザーの解答をチェックし、正解ならステージを進める。
     * 正解データはRiddleリポジトリから動的に取得する。
     *
     * @param userId ユーザーID
     * @param answer ユーザーが入力した解答
     * @return 正解なら true, 不正解または問題データなしなら false
     */
    @Transactional
    public boolean checkAnswer(String userId, String answer) {
        GameProgress progress = getProgress(userId);
        int currentStage = progress.getCurrentStageId();

        String correctAnswer = riddleRepository.findById(currentStage)
                .map(Riddle::getAnswer)
                .orElse(null);

        if (correctAnswer == null) {
            return false;
        }

        boolean isCorrect = correctAnswer.equalsIgnoreCase(answer.trim());

        if (isCorrect) {
            progress.setCurrentStageId(currentStage + 1);
            gameProgressRepository.save(progress);
        }

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