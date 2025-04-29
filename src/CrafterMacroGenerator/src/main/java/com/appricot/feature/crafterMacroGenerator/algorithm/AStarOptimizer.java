package com.appricot.feature.crafterMacroGenerator.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;
import com.appricot.feature.crafterMacroGenerator.entity.OptimizationResult;
import com.appricot.feature.crafterMacroGenerator.entity.Recipe;

import lombok.extern.slf4j.Slf4j;

/**
 * A*アルゴリズムを使用したマクロ最適化の実装
 * A*は「実コスト(g)」と「推定残りコスト(h)」の合計で状態を評価し、
 * 最も有望な状態から優先的に探索を行うアルゴリズム
 * TODO read code
 */
@Slf4j
@Component
public class AStarOptimizer implements MacroOptimizer {

    // 最大探索ノード数
    private static final int MAX_EXPLORED_NODES = 100000;
    
    // 最大アクション数
    private static final int MAX_ACTIONS = 90;
    
    // 探索状態数カウンター
    private int exploredStatesCount;
    
    // 目標達成した最良ノード
    private AStarNode bestCompleteNode;
    
    // レシピ情報（ヒューリスティック計算用）
    private Recipe recipe;
    
    // 品質優先フラグ
    private boolean qualityFocus;

    @Override
    public OptimizationResult findOptimalMacroPath(
            CraftingState initialState, 
            List<CraftingAction> availableActions, 
            Recipe recipe, 
            boolean qualityFocus, 
            boolean durabilityConstraint) {
        
        // フィールドの初期化
        this.exploredStatesCount = 0;
        this.bestCompleteNode = null;
        this.recipe = recipe;
        this.qualityFocus = qualityFocus;
        
        // 優先度付きキュー（f値が小さい順）
        // A*アルゴリズムでは、f = g + h (g=実コスト、h=ヒューリスティック推定)
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(AStarNode::getFValue));
        
        // 探索済みの状態を記録するセット
        Set<String> closedSet = new HashSet<>();
        
        // 開始ノードを作成して優先度キューに追加
        AStarNode startNode = new AStarNode(
                initialState,
                new ArrayList<>(),
                0.0,
                calculateHeuristic(initialState));
                
        openSet.add(startNode);
        
        // A*の主ループ
        while (!openSet.isEmpty() && exploredStatesCount < MAX_EXPLORED_NODES) {
            // 最も有望なノードを取得
            AStarNode currentNode = openSet.poll();
            exploredStatesCount++;
            
            // 状態のハッシュを生成
            String stateHash = getStateHash(currentNode.getState());
            
            // 既に探索済みの状態はスキップ
            if (closedSet.contains(stateHash)) {
                continue;
            }
            
            // 探索済みとしてマーク
            closedSet.add(stateHash);
            
            // 目標達成チェック（作業進捗が目標以上）
            if (currentNode.getState().getCurrentProgress() >= recipe.getRequiredProgress()) {
                // 目標達成した場合、最良ノードを更新
                if (bestCompleteNode == null || 
                    evaluateCompletedState(currentNode.getState(), recipe) > 
                    evaluateCompletedState(bestCompleteNode.getState(), recipe)) {
                    
                    bestCompleteNode = currentNode;
                    
                    // 品質優先の場合、より良い解が見つかるまで探索を続ける
                    if (!qualityFocus) {
                        // 進捗優先の場合は最初に目標達成した解で終了
                        break;
                    }
                }
            }
            
            // アクション数の上限チェック
            if (currentNode.getPath().size() >= MAX_ACTIONS) {
                continue;
            }
            
            // 各アクションを試す
            for (CraftingAction action : availableActions) {
                // アクションが実行可能かチェック
                if (!action.canExecute(currentNode.getState())) {
                    continue;
                }
                
                // 耐久度制約のチェック
                if (durabilityConstraint) {
                    CraftingState nextState = action.apply(currentNode.getState().clone());
                    if (nextState.getRemainingDurability() <= 0) {
                        continue;
                    }
                }
                
                // アクションを適用した次の状態を取得
                CraftingState nextState = action.apply(currentNode.getState().clone());
                
                // 新しいパスを作成
                List<CraftingAction> newPath = new ArrayList<>(currentNode.getPath());
                newPath.add(action);
                
                // 新しい実コスト（g値）を計算
                // コストは「必要CP」と「アクション数」のバランスを考慮
                double newGCost = currentNode.getGCost() + calculateActionCost(action, currentNode.getState());
                
                // ヒューリスティック（h値）を計算
                double heuristic = calculateHeuristic(nextState);
                
                // 新しいノードを作成
                AStarNode newNode = new AStarNode(nextState, newPath, newGCost, heuristic);
                
                // 優先度キューに追加
                openSet.add(newNode);
            }
        }
        
        log.debug("A* 探索: 探索ノード数 = {}", exploredStatesCount);
        
        // 目標達成したノードが見つからなかった場合
        if (bestCompleteNode == null) {
            // 現在の最良ノードを探す
            AStarNode bestIncompleteNode = findBestIncompleteNode(openSet, closedSet);
            
            // 結果を構築して返す
            return createResult(bestIncompleteNode, initialState);
        }
        
        // 目標達成した最良ノードから結果を構築
        return createResult(bestCompleteNode, initialState);
    }
    
    /**
     * 状態のハッシュ文字列を生成
     * メモリ効率のため、主要な状態情報のみを含む
     */
    private String getStateHash(CraftingState state) {
        return state.getCurrentProgress() + ":" + 
               state.getCurrentQuality() + ":" + 
               state.getRemainingDurability() + ":" + 
               state.getCurrentCP() + ":" + 
               state.getAppliedBuffs().toString();
    }
    
    /**
     * アクションの実コスト（g値の増分）を計算
     */
    private double calculateActionCost(CraftingAction action, CraftingState state) {
        // 基本コストはCP消費
        double cost = action.getCpCost();
        
        // CP消費がない場合（例: 作業）は最小コスト1を設定
        if (cost == 0) {
            cost = 1;
        }
        
        // 耐久度消費も考慮（耐久度は貴重なリソース）
        // 実際のコスト計算は使用するスキル実装に依存
        cost += 2;  // 仮の耐久度コスト
        
        return cost;
    }
    
    /**
     * ヒューリスティック関数（h値、目標までの推定コスト）
     * TODO 調整 方針は未定
     */
    private double calculateHeuristic(CraftingState state) {
        // 作業進捗に関するヒューリスティック
        double progressHeuristic = 0;
        if (state.getCurrentProgress() < recipe.getRequiredProgress()) {
            // 残りの作業進捗を達成するのに必要な「作業」の回数を推定
            int progressRemaining = recipe.getRequiredProgress() - state.getCurrentProgress();
            int basicSynthesisValue = 120; // TODO 作業の基本効率（実際は状態依存）
            
            progressHeuristic = (double) progressRemaining / basicSynthesisValue * 10;
        }
        
        // 品質に関するヒューリスティック（品質優先の場合）
        double qualityHeuristic = 0;
        if (qualityFocus && state.getCurrentProgress() >= recipe.getRequiredProgress()) {
            // 最大品質からの距離に基づくヒューリスティック
            qualityHeuristic = (1.0 - (double) state.getCurrentQuality() / recipe.getMaxQuality()) * 100;
        }
        
        // 品質優先の場合、優先度を調整
        if (qualityFocus) {
            // 進捗未達成の場合は進捗を最優先
            if (state.getCurrentProgress() < recipe.getRequiredProgress()) {
                return progressHeuristic * 10;
            } else {
                // 進捗達成済みの場合は品質を優先
                return qualityHeuristic;
            }
        } else {
            // 進捗優先の場合
            return progressHeuristic + qualityHeuristic * 0.1;
        }
    }
    
    /**
     * 目標達成状態の評価関数
     * TODO マクロ長の考慮
     */
    private double evaluateCompletedState(CraftingState state, Recipe recipe) {
        // 品質スコア（0～1.0）
        double qualityScore = (double) state.getCurrentQuality() / recipe.getMaxQuality();
        
        // CP効率スコア
        double cpEfficiencyScore = (double) state.getCurrentCP() / 1000; // 仮の最大CP値
        
        // 耐久度効率スコア
        double durabilityScore = (double) state.getRemainingDurability() / recipe.getBaseDurability(); // 仮の初期耐久度
        
        if (qualityFocus) {
            // 品質優先の場合、品質を高く評価
            return qualityScore * 10 + cpEfficiencyScore + durabilityScore;
        } else {
            // 進捗優先の場合、CP効率と耐久度効率を高く評価
            return qualityScore * 2 + cpEfficiencyScore * 5 + durabilityScore * 3;
        }
    }
    
    /**
     * 目標未達成時の最良ノードを探す
     */
    private AStarNode findBestIncompleteNode(PriorityQueue<AStarNode> openSet, Set<String> closedSet) {
        // 探索途中のノードから最良のものを選択
        AStarNode bestNode = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        
        // 残りの開いているノードを評価
        for (AStarNode node : openSet) {
            double score = evaluateIncompleteState(node.getState());
            if (bestNode == null || score > bestScore) {
                bestNode = node;
                bestScore = score;
            }
        }
        
        return bestNode;
    }
    
    /**
     * 目標未達成状態の評価関数
     * TODO 階層的アプローチの実装　(品質 > 進捗 > 残CP,残耐久値)
     */
    private double evaluateIncompleteState(CraftingState state) {
        // 進捗達成率
        double progressScore = (double) state.getCurrentProgress() / recipe.getRequiredProgress();
        
        // 品質スコア
        double qualityScore = (double) state.getCurrentQuality() / recipe.getMaxQuality();
        
        // 進捗が最優先
        return progressScore * 10 + qualityScore;
    }
    
    /**
     * 最適化結果オブジェクトを作成
     */
    private OptimizationResult createResult(AStarNode node, CraftingState initialState) {
        // ノードがnullの場合（まれなケース）は空の結果を返す
        if (node == null) {
            return OptimizationResult.builder()
                    .actionPath(new ArrayList<>())
                    .score(0)
                    .finalQuality(initialState.getCurrentQuality())
                    .finalProgress(initialState.getCurrentProgress())
                    .usedCP(0)
                    .totalActions(0)
                    .build();
        }
        
        return OptimizationResult.builder()
                .actionPath(node.getPath())
                .score(evaluateCompletedState(node.getState(), recipe))
                .finalQuality(node.getState().getCurrentQuality())
                .finalProgress(node.getState().getCurrentProgress())
                .usedCP(initialState.getCurrentCP() - node.getState().getCurrentCP())
                .totalActions(node.getPath().size())
                .build();
    }

    @Override
    public int getExploredStatesCount() {
        return exploredStatesCount;
    }
    
    /**
     * A*アルゴリズムのノードクラス
     */
    private static class AStarNode {
        private final CraftingState state;
        private final List<CraftingAction> path;
        private final double gCost; // 実コスト
        private final double hCost; // ヒューリスティック推定
        
        public AStarNode(CraftingState state, List<CraftingAction> path, double gCost, double hCost) {
            this.state = state;
            this.path = path;
            this.gCost = gCost;
            this.hCost = hCost;
        }
        
        public CraftingState getState() {
            return state;
        }
        
        public List<CraftingAction> getPath() {
            return path;
        }
        
        public double getGCost() {
            return gCost;
        }
        
        public double getHCost() {
            return hCost;
        }
        
        // f値 = g値 + h値（A*の評価値）
        public double getFValue() {
            return gCost + hCost;
        }
    }
}
