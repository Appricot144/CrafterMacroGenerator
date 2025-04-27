package com.appricot.feature.crafterMacroGenerator.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.appricot.feature.crafterMacroGenerator.entity.PlayerStatus;
import com.appricot.feature.crafterMacroGenerator.entity.Recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MacroGenerationRequest {
    
    @NotNull(message = "プレイヤーステータスは必須です")
    @Valid
    private PlayerStatus playerStatus;
    
    @NotNull(message = "レシピ情報は必須です")
    @Valid
    private Recipe recipe;
    
    @Builder.Default
    private List<String> availableSkills = null; // null の場合、すべてのスキルが使用可能とみなす
    
    @Builder.Default
    private boolean qualityFocus = true; // true: 品質優先, false: 進捗優先
    
    @Builder.Default
    private boolean durabilityConstraint = true; // 耐久度が0以下にならないよう制約を守るかどうか
}
