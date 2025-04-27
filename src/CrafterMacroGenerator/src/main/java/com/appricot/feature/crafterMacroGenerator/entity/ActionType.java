package com.appricot.feature.crafterMacroGenerator.entity;

public enum ActionType {
    PROGRESS,    // 作業進捗を上げるアクション
    QUALITY,     // 品質を上げるアクション
    BUFF,        // バフを付与するアクション
    REPAIR,      // 耐久度を回復するアクション
    CP_RECOVERY, // CPを回復するアクション
    COMBO        // 複合効果を持つアクション
}
