package com.appricot.feature.crafterMacroGenerator.skills;

import com.appricot.feature.crafterMacroGenerator.entity.ActionType;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;

public class BasicSynthesis extends CraftingAction {
    
    private static final int PROGRESS_INCREASE = 120; // 基本効率
    
    public BasicSynthesis() {
        super("作業", 0, ActionType.PROGRESS); // 作業スキル、CP消費0
    }

    @Override
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
        // CP要求なし、耐久度が0より大きければ実行可能
        return currentState.getRemainingDurability() > 0;
    }
    
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
