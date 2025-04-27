package com.appricot.feature.crafterMacroGenerator.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CraftingState {
    private int currentProgress;     // 現在の作業進捗
    private int currentQuality;      // 現在の品質
    private int remainingDurability; // 残り耐久度
    private int currentCP;           // 現在のクラフターポイント
    
    @Builder.Default
    private List<String> appliedBuffs = new ArrayList<>(); // 適用中のバフ
    
    @Builder.Default
    private List<String> usedActions = new ArrayList<>(); // 使用済みアクション

    // 状態のクローンを作成するためのメソッド
    public CraftingState clone() {
        CraftingState cloned = new CraftingState();
        cloned.setCurrentProgress(this.currentProgress);
        cloned.setCurrentQuality(this.currentQuality);
        cloned.setRemainingDurability(this.remainingDurability);
        cloned.setCurrentCP(this.currentCP);
        cloned.setAppliedBuffs(new ArrayList<>(this.appliedBuffs));
        cloned.setUsedActions(new ArrayList<>(this.usedActions));
        return cloned;
    }

    // メモ化のためのハッシュコード最適化
    @Override
    public int hashCode() {
        return Objects.hash(
            currentProgress, 
            currentQuality, 
            remainingDurability, 
            currentCP, 
            appliedBuffs, 
            usedActions.size() // アクション履歴の詳細ではなく数のみを使用
        );
    }

    // メモ化のための等価性判定最適化
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CraftingState other = (CraftingState) obj;
        return currentProgress == other.currentProgress &&
               currentQuality == other.currentQuality &&
               remainingDurability == other.remainingDurability &&
               currentCP == other.currentCP &&
               Objects.equals(appliedBuffs, other.appliedBuffs) &&
               usedActions.size() == other.usedActions.size(); // アクション数のみ比較
    }
}
