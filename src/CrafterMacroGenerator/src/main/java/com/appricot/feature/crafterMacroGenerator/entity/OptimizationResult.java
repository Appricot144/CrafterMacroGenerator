package com.appricot.feature.crafterMacroGenerator.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResult {
    private List<CraftingAction> actionPath;
    private double score;
    private int finalQuality;
    private int finalProgress;
    private int usedCP;
    private int totalActions;
}
