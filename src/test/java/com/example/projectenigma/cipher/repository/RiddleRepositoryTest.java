package com.example.projectenigma.cipher.repository;

import com.example.projectenigma.cipher.entity.Riddle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RiddleRepositoryの単体テストクラス。
 * DBへの保存、取得、およびEntityのフィールドマッピングが正しく行われるかを検証する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@DataJpaTest
class RiddleRepositoryTest {

    @Autowired
    private RiddleRepository riddleRepository;

    /**
     * 拡張データ(stageData)を含むRiddleを保存・取得できることを検証するテスト。
     * 期待値:
     * RiddleエンティティにJSON文字列などを想定した stageData をセットできること
     * 保存後にID指定で取得した際、セットした stageData がそのまま取得できること
     * 
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Test
    @DisplayName("拡張データ(stageData)を含むRiddleを保存・取得できること")
    void testSaveAndLoad_StageData() {
        // Given
        String jsonConfig = "{\"grid\": [[0,1], [1,9]], \"start\": [0,0]}";
        Riddle riddle = new Riddle(4, "GRAVITY", "Hint text");
        
        // ★ここでコンパイルエラーになる場合は Entity にフィールドを追加する必要あり
        riddle.setStageData(jsonConfig); 

        // When
        riddleRepository.save(riddle);
        Optional<Riddle> result = riddleRepository.findById(4);

        // Then
        assertTrue(result.isPresent());
        assertEquals("GRAVITY", result.get().getAnswer());
        assertEquals(jsonConfig, result.get().getStageData(), "保存したJSONデータが正しく取得できること");
    }
}