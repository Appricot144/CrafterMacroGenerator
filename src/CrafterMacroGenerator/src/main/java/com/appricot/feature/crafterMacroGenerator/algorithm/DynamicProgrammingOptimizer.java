package com.appricot.feature.crafterMacroGenerator.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;
import com.appricot.feature.crafterMacroGenerator.entity.OptimizationResult;
import com.appricot.feature.crafterMacroGenerator.entity.Recipe;

import lombok.extern.slf4j.Slf4j;

/**
 * 動的計画法を使用したマクロ最適化アルゴリズムの実装
 * TODO read code
 */
@Slf4j
@Component
public class DynamicProgrammingOptimizer implements MacroOptimizer {

    // メモ化用のマップ
    private Map<CraftingState, OptimizationResult> memo;
    
    // 探索した状態数のカウンター
    private int exploredStatesCount;
    
    // 最大再帰深度（マクロの最大長に相当）
    private static final int MAX_RECURSION_DEPTH = 50;

    @Override
    public OptimizationResult findOptimalMacroPath(
            CraftingState initialState, 
            List<CraftingAction> availableActions, 
            Recipe recipe, 
            boolean qualityFocus, 
            boolean durabilityConstraint) {
        
        // メモとカウンターの初期化
        memo = new HashMap<>();
        exploredStatesCount = 0;
        
        // 最適化実行
        return findOptimalPath(
                initialState, 
                availableActions, 
                recipe, 
                qualityFocus, 
                durabilityConstraint, 
                0);
    }
    
    /**
     * 再帰的に最適パスを探索する内部メソッド
     */
    private OptimizationResult findOptimalPath(
            CraftingState currentState, 
            List<CraftingAction> availableActions, 
            Recipe recipe, 
            boolean qualityFocus, 
            boolean durabilityConstraint, 
            int depth) {
        
        // 探索状態数をカウント
        exploredStatesCount++;
        
        // メモ化チェック（同じ状態が既に計算済みかどうか）
        if (memo.containsKey(currentState)) {
            return memo.get(currentState);
        }
        
        // 再帰の深さ（アクション数）制限をチェック
        if (depth >= MAX_RECURSION_DEPTH) {
            return createTerminalResult(currentState, recipe);
        }
        
        // 終了条件：作業進捗が目標を達成した場合
        if (currentState.getCurrentProgress() >= recipe.getRequiredProgress()) {
            return createTerminalResult(currentState, recipe);
        }
        
        // 利用可能なアクションを評価
        OptimizationResult bestResult = null;
        CraftingState initialState = currentState;
        
        for (CraftingAction action : availableActions) {
            // アクションが実行可能かチェック
            if (!action.canExecute(currentState)) {
                continue;
            }
            
            // 耐久度制約チェック（必要であれば）
            if (durabilityConstraint) {
                CraftingState nextState = action.apply(currentState.clone());
                if (nextState.getRemainingDurability() <= 0) {
                    continue;
                }
            }
            
            // アクションを適用した次の状態を取得
            CraftingState nextState = action.apply(currentState.clone());
            nextState.getUsedActions().add(action.getName());
            
            // 次の状態から最適結果を再帰的に計算
            OptimizationResult subResult = findOptimalPath(
                    nextState, 
                    availableActions, 
                    recipe, 
                    qualityFocus, 
                    durabilityConstraint, 
                    depth + 1);
            
            // サブパスにこのアクションを追加
            List<CraftingAction> actionPath = new ArrayList<>();
            actionPath.add(action);
            if (subResult.getActionPath() != null) {
                actionPath.addAll(subResult.getActionPath());
            }
            
            // 結果スコアの更新
            double score = calculateScore(nextState, recipe, qualityFocus);
            
            // 結果オブジェクトの作成
            OptimizationResult result = OptimizationResult.builder()
                    .actionPath(actionPath)
                    .score(score)
                    .finalQuality(nextState.getCurrentQuality())
                    .finalProgress(nextState.getCurrentProgress())
                    .usedCP(initialState.getCurrentCP() - nextState.getCurrentCP())
                    .totalActions(actionPath.size())
                    .build();
            
            // 現在の最良結果と比較して更新
            if (bestResult == null || result.getScore() > bestResult.getScore()) {
                bestResult = result;
            }
        }
        
        // 有効なアクションがない場合は終端結果を返す
        if (bestResult == null) {
            bestResult = createTerminalResult(currentState, recipe);
        }
        
        // 結果をメモ化して返す
        memo.put(currentState, bestResult);
        return bestResult;
    }
    
    /**
     * 終端状態の結果オブジェクトを作成
     */
    private OptimizationResult createTerminalResult(CraftingState state, Recipe recipe) {
        return OptimizationResult.builder()
                .actionPath(new ArrayList<>())
                .score(calculateScore(state, recipe, true))
                .finalQuality(state.getCurrentQuality())
                .finalProgress(state.getCurrentProgress())
                .usedCP(0)
                .totalActions(0)
                .build();
    }
    
    /**
     * 状態のスコアを計算
     */
    private double calculateScore(CraftingState state, Recipe recipe, boolean qualityFocus) {
        double progressScore = (double) state.getCurrentProgress() / recipe.getRequiredProgress();
        double qualityScore = (double) state.getCurrentQuality() / recipe.getMaxQuality();
        double cpEfficiencyScore = (double) state.getCurrentCP() / 500; // CP効率の最大値を500と仮定
        
        if (qualityFocus) {
            // 品質優先の場合
            if (progressScore >= 1.0) {
                // 進捗達成時は品質を最大化
                return qualityScore * 10 + cpEfficiencyScore;
            } else {
                // 進捗未達成時はペナルティ
                return progressScore - 10;
            }
        } else {
            // 進捗優先の場合
            if (progressScore >= 1.0) {
                // 進捗達成時は進捗+品質
                return progressScore + qualityScore + cpEfficiencyScore;
            } else {
                // 進捗未達成時は進捗のみ
                return progressScore;
            }
        }
    }

    @Override
    public int getExploredStatesCount() {
        return exploredStatesCount;
    }
}