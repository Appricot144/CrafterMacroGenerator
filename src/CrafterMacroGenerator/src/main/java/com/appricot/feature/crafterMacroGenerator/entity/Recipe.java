package com.appricot.feature.crafterMacroGenerator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    private String name;
    private int requiredProgress;  // 必要作業進捗値
    private int maxQuality;        // 最大品質値
    private int baseDurability;    // 基本耐久度
    private DifficultyRank difficulty;
}
