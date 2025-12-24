package com.example.projectenigma.cipher.config;

import com.example.projectenigma.cipher.repository.RiddleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DataInitializerの単体テスト。
 * 起動時のデータ投入ロジック（特に二重登録防止）を検証する。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private RiddleRepository riddleRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DataInitializer dataInitializer;

    /**
     * すでにDBにデータが存在する場合、ロード処理をスキップすることを確認。
     * @author R.Morioka
     * @version 1.0
     * @since 1.0
     */
    @Test
    @DisplayName("run: データが既に存在する場合、ロード処理を行わない")
    void testRun_DataExists() throws Exception {
        // 1. 準備: データ件数が1件以上ある状態をモック
        when(riddleRepository.count()).thenReturn(1L);

        // 2. 実行
        dataInitializer.run();

        // 3. 検証: saveAll は絶対に呼ばれてはいけない
        verify(riddleRepository, never()).saveAll(any());
    }
}