package com.appricot.feature.crafterMacroGenerator.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;
import com.appricot.feature.crafterMacroGenerator.entity.OptimizationResult;
import com.appricot.feature.crafterMacroGenerator.entity.Recipe;

import lombok.extern.slf4j.Slf4j;

/**
 * ビームサーチを使用したマクロ最適化アルゴリズムの実装
 * TODO read code
 */
@Slf4j
@Component
public class BeamSearchOptimizer implements MacroOptimizer {

    // ビーム幅（探索する状態数の上限）
    private static final int BEAM_WIDTH = 1000;
    
    // 最大探索深度（最大アクション数）
    private static final int MAX_DEPTH = 30;
    
    // 探索状態数カウンター
    private int exploredStatesCount;

    @Override
    public OptimizationResult findOptimalMacroPath(
            CraftingState initialState, 
            List<CraftingAction> availableActions, 
            Recipe recipe, 
            boolean qualityFocus, 
            boolean durabilityConstraint) {
        
        // カウンターの初期化
        exploredStatesCount = 0;
        
        // 現在のビーム（現在の探索対象状態群）
        List<BeamNode> currentBeam = new ArrayList<>();
        currentBeam.add(new BeamNode(initialState, new ArrayList<>(), 0.0));
        
        // 最良結果
        BeamNode bestNode = null;
        
        // 探索深度ごとに繰り返す
        for (int depth = 0; depth < MAX_DEPTH; depth++) {
            // ビームが空なら終了
            if (currentBeam.isEmpty()) {
                break;
            }
            
            // 次のビーム
            List<BeamNode> nextBeam = new ArrayList<>();
            
            // 現在のビームの各ノードに対して
            for (BeamNode node : currentBeam) {
                // 終了条件チェック（作業進捗達成）
                if (node.state.getCurrentProgress() >= recipe.getRequiredProgress()) {
                    // 最良結果の更新
                    if (bestNode == null || 
                        calculateScore(node.state, recipe, qualityFocus) > calculateScore(bestNode.state, recipe, qualityFocus)) {
                        bestNode = node;
                    }
                    continue;
                }
                
                // 利用可能な各アクションに対して
                for (CraftingAction action : availableActions) {
                    // アクションが実行可能かチェック
                    if (!action.canExecute(node.state)) {
                        continue;
                    }
                    
                    // 耐久度制約チェック（必要であれば）
                    if (durabilityConstraint) {
                        CraftingState nextState = action.apply(node.state.clone());
                        if (nextState.getRemainingDurability() <= 0) {
                            continue;
                        }
                    }
                    
                    // アクションを適用した次の状態を取得
                    CraftingState nextState = action.apply(node.state.clone());
                    
                    // アクション履歴を更新
                    List<CraftingAction> newPath = new ArrayList<>(node.path);
                    newPath.add(action);
                    
                    // スコア計算
                    double score = calculateScore(nextState, recipe, qualityFocus);
                    
                    // 新しいノードをビームに追加
                    nextBeam.add(new BeamNode(nextState, newPath, score));
                    
                    // 探索状態数をカウント
                    exploredStatesCount++;
                }
            }
            
            // 次のビームを上位BEAM_WIDTH個に制限
            currentBeam = nextBeam.stream()
                    .sorted(Comparator.comparingDouble((BeamNode n) -> n.score).reversed())
                    .limit(BEAM_WIDTH)
                    .collect(Collectors.toList());
        }
        
        // 最良結果が見つからなかった場合
        if (bestNode == null) {
            // 現在のビームから最良ノードを選択
            bestNode = currentBeam.stream()
                    .max(Comparator.comparingDouble(n -> calculateScore(n.state, recipe, qualityFocus)))
                    .orElse(new BeamNode(initialState, new ArrayList<>(), 0.0));
        }
        
        // 結果を構築して返す
        return OptimizationResult.builder()
                .actionPath(bestNode.path)
                .score(bestNode.score)
                .finalQuality(bestNode.state.getCurrentQuality())
                .finalProgress(bestNode.state.getCurrentProgress())
                .usedCP(initialState.getCurrentCP() - bestNode.state.getCurrentCP())
                .totalActions(bestNode.path.size())
                .build();
    }
    
    /**
     * 状態のスコアを計算
     */
    private double calculateScore(CraftingState state, Recipe recipe, boolean qualityFocus) {
        double progressScore = (double) state.getCurrentProgress() / recipe.getRequiredProgress();
        double qualityScore = (double) state.getCurrentQuality() / recipe.getMaxQuality();
        double cpEfficiencyScore = (double) state.getCurrentCP() / 500; // CP効率の最大値を500と仮定
        double durabilityScore = (double) state.getRemainingDurability() / 70; // 耐久効率の最大値を70と仮定
        
        if (qualityFocus) {
            // 品質優先の場合
            if (progressScore >= 1.0) {
                // 進捗達成時は品質を最大化
                return qualityScore * 10 + cpEfficiencyScore + durabilityScore;
            } else {
                // 進捗未達成時はペナルティ
                return progressScore - 10;
            }
        } else {
            // 進捗優先の場合
            if (progressScore >= 1.0) {
                // 進捗達成時は進捗+品質
                return progressScore + qualityScore + cpEfficiencyScore + durabilityScore;
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
    
    /**
     * ビームサーチのノード（状態と経路を保持）
     */
    private static class BeamNode {
        final CraftingState state;
        final List<CraftingAction> path;
        final double score;
        
        BeamNode(CraftingState state, List<CraftingAction> path, double score) {
            this.state = state;
            this.path = path;
            this.score = score;
        }
    }
}
