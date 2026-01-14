package com.example.projectenigma.cipher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stage 2: 容疑者の定義クラス。
 * 12人の容疑者のID、見た目、証言キーを管理する。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suspect {
    private String id;        // ID (A, B, C...)
    private String icon;      // アイコン (👨‍🌾, 👱‍♂️...)
    private String quoteKey;  // messages.propertiesのキー (stage2.quote.a...)
}