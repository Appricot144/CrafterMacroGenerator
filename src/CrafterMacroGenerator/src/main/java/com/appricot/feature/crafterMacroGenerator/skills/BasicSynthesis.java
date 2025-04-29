package com.appricot.feature.crafterMacroGenerator.skills;

import com.appricot.feature.crafterMacroGenerator.entity.ActionType;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;

public class BasicSynthesis extends CraftingAction {
    
    private static final int PROGRESS_INCREASE = 120; // 基本効率
    
    public BasicSynthesis() {
        super("作業", 0, ActionType.PROGRESS, 3, 10);
    }

    @Override
    // TODO バフによる耐久消費値の更新
    public CraftingState apply(CraftingState currentState) {
        CraftingState newState = currentState.clone();
        
        // 作業進捗の計算
        int progressIncrease = calculateProgressIncrease(currentState);
        newState.setCurrentProgress(currentState.getCurrentProgress() + progressIncrease);
        
        // 耐久度消費
        newState.setRemainingDurability(currentState.getRemainingDurability() - 10);
        
        return newState;
    }

    @Override
    public boolean canExecute(CraftingState currentState) {
        return currentState.getRemainingDurability() > 0;
    }
    
    // TODO バフによる耐久消費値の更新
    private int calculateProgressIncrease(CraftingState state) {
        int base = PROGRESS_INCREASE;
        
        // 真価発揮バフがある場合は効果アップ
        if (state.getAppliedBuffs().contains("真価発揮")) {
            base = (int)(base * 1.5);
        }
        
        // 確信バフがある場合は効果アップ
        if (state.getAppliedBuffs().contains("確信")) {
            base = (int)(base * 1.2);
        }
        
        return base;
    }
}
