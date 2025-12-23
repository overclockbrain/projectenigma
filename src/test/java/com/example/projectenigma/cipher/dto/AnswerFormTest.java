package com.example.projectenigma.cipher.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AnswerFormのテスト。
 * DTOなので、LombokによるGetter/Setterが機能しているかだけ確認。
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
class AnswerFormTest {

    @Test
    @DisplayName("Getter/Setterの動作確認")
    void testGetterSetter() {
        // 1. 準備 & 実行
        AnswerForm form = new AnswerForm();
        form.setAnswer("apple");

        // 2. 検証
        assertEquals("apple", form.getAnswer());
        
        // toStringとかequalsも呼んでエラー出ないか確認（おまけ）
        assertNotNull(form.toString());
        assertEquals(form, form); 
    }
}