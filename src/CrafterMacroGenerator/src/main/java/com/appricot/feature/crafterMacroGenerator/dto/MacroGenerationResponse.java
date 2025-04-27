package com.appricot.feature.crafterMacroGenerator.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MacroGenerationResponse {
    private String macroText;          // ゲーム内で使用可能なマクロテキスト
    private List<String> actionSequence; // アクションの配列（名前のみ）
    
    private int finalQuality;          // 最終品質
    private int finalProgress;         // 最終進捗
    private int totalCPUsed;           // 使用したCP
    private int durabilityRemaining;   // 残り耐久度
    
    private int qualityPercentage;     // 品質達成率（%）
    private boolean progressComplete;  // 作業進捗が目標を達成したか
    
    private long calculationTimeMs;    // 計算にかかった時間（ミリ秒）
    private int exploredStates;        // 探索した状態数
}
