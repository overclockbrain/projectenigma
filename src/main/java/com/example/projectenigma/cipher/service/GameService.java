package com.example.projectenigma.cipher.service;

import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ゲームの進行や正誤判定を行うサービス。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameProgressRepository gameProgressRepository;

    /**
     * 解答をチェックして、正解ならステージを進めます。
     *
     * @param userId ユーザーID
     * @param answer ユーザーの解答
     * @return 正解なら true, 不正解なら false
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Transactional
    public boolean checkAnswer(String userId, String answer) {
        // ユーザーの進捗を取得
        GameProgress progress = gameProgressRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        int currentStage = progress.getCurrentStageId();
        boolean isCorrect = false;

        // ★正解ロジック (今はStage 1: apple だけ)
        // equalsIgnoreCase で大文字小文字を区別しない (Apple も apple もOK)
        if (currentStage == 1 && "apple".equalsIgnoreCase(answer.trim())) {
            isCorrect = true;
        } 
        
        // 正解なら次のステージへ進める
        if (isCorrect) {
            progress.setCurrentStageId(currentStage + 1);
            gameProgressRepository.save(progress);
        }

        return isCorrect;
    }
}