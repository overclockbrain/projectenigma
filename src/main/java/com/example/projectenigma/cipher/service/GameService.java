package com.example.projectenigma.cipher.service;

import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.Riddle;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.repository.RiddleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ゲームの進行管理や正誤判定を行うサービス。
 * ユーザーの解答をDBのマスタデータと照合し、ステージ進行を制御する。
 *
 * @author R.Morioka
 * @version 1.1 (DB連携対応)
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameProgressRepository gameProgressRepository;
    private final RiddleRepository riddleRepository;

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
        // ユーザーの進捗を取得
        GameProgress progress = gameProgressRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        int currentStage = progress.getCurrentStageId();
        
        // DBからそのステージの正解を取得
        String correctAnswer = riddleRepository.findById(currentStage)
                .map(Riddle::getAnswer)
                .orElse(null);

        // 問題データがない場合は不正解扱い
        if (correctAnswer == null) {
            return false;
        }

        // 大文字小文字を無視して比較
        boolean isCorrect = correctAnswer.equalsIgnoreCase(answer.trim());

        // 正解なら次のステージへ進める
        if (isCorrect) {
            progress.setCurrentStageId(currentStage + 1);
            gameProgressRepository.save(progress);
        }

        return isCorrect;
    }
}