package com.appricot.feature.crafterMacroGenerator.algorithm;

import java.util.List;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;
import com.appricot.feature.crafterMacroGenerator.entity.OptimizationResult;
import com.appricot.feature.crafterMacroGenerator.entity.Recipe;

/**
 * マクロ生成のための最適化アルゴリズムのインターフェース
 */
public interface MacroOptimizer {
    
    /**
     * 最適なマクロパスを探索する
     * @param initialState 初期状態
     * @param availableActions 利用可能なアクション
     * @param recipe レシピ情報
     * @param qualityFocus 品質優先フラグ
     * @param durabilityConstraint 耐久度制約フラグ
     * @return 最適化結果
     */
    OptimizationResult findOptimalMacroPath(
            CraftingState initialState, 
            List<CraftingAction> availableActions, 
            Recipe recipe, 
            boolean qualityFocus, 
            boolean durabilityConstraint);
    
    /**
     * 探索した状態の数を取得
     * @return 探索状態数
     */
    int getExploredStatesCount();
}