package com.appricot.feature.crafterMacroGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.appricot.feature.crafterMacroGenerator.algorithm.MacroOptimizer;
import com.appricot.feature.crafterMacroGenerator.dto.MacroGenerationRequest;
import com.appricot.feature.crafterMacroGenerator.dto.MacroGenerationResponse;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;
import com.appricot.feature.crafterMacroGenerator.entity.OptimizationResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MacroGeneratorService {

    private final MacroOptimizer macroOptimizer;
    private final SkillService skillService;

    public MacroGenerationResponse generateOptimalMacro(MacroGenerationRequest request) {
        long startTimeMs = System.currentTimeMillis();

        // 初期状態を設定
        CraftingState initialState = createInitialState(request);
        
        // 利用可能なアクションのリストを取得
        List<CraftingAction> availableActions = getAvailableActions(request);
        
        // 最適化実行
        OptimizationResult result = macroOptimizer.findOptimalMacroPath(
                initialState, 
                availableActions, 
                request.getRecipe(),
                request.isQualityFocus(),
                request.isDurabilityConstraint()
        );
        
        // 実行時間計算
        long calculationTimeMs = System.currentTimeMillis() - startTimeMs;
        
        // 最終状態を取得
        CraftingState finalState = applyActions(initialState, result.getActionPath());
        
        // マクロテキスト生成
        String macroText = generateMacroText(result.getActionPath());
        
        // アクション名のリスト作成
        List<String> actionSequence = result.getActionPath()
                .stream()
                .map(CraftingAction::getName)
                .collect(Collectors.toList());
        
        // 品質達成率計算
        int qualityPercentage = (int) Math.round(100.0 * finalState.getCurrentQuality() / request.getRecipe().getMaxQuality());
        
        // レスポンス作成
        return MacroGenerationResponse.builder()
                .macroText(macroText)
                .actionSequence(actionSequence)
                .finalQuality(finalState.getCurrentQuality())
                .finalProgress(finalState.getCurrentProgress())
                .totalCPUsed(request.getPlayerStatus().getCp() - finalState.getCurrentCP())
                .durabilityRemaining(finalState.getRemainingDurability())
                .qualityPercentage(qualityPercentage)
                .progressComplete(finalState.getCurrentProgress() >= request.getRecipe().getRequiredProgress())
                .calculationTimeMs(calculationTimeMs)
                .exploredStates(macroOptimizer.getExploredStatesCount())
                .build();
    }
    
    private CraftingState createInitialState(MacroGenerationRequest request) {
        return CraftingState.builder()
                .currentProgress(0)
                .currentQuality(0)
                .remainingDurability(request.getRecipe().getBaseDurability())
                .currentCP(request.getPlayerStatus().getCp())
                .appliedBuffs(new ArrayList<>())
                .usedActions(new ArrayList<>())
                .build();
    }
    
    private List<CraftingAction> getAvailableActions(MacroGenerationRequest request) {
        // プレイヤーレベルに基づいて利用可能なスキルを取得
        List<CraftingAction> availableActions = skillService.getAllSkills();
        
        // リクエストで指定されたスキルのみに制限する場合
        if (request.getAvailableSkills() != null && !request.getAvailableSkills().isEmpty()) {
            Set<String> allowedSkillNames = new HashSet<>(request.getAvailableSkills());
            availableActions = availableActions.stream()
                    .filter(action -> allowedSkillNames.contains(action.getName()))
                    .collect(Collectors.toList());
        }
        
        return availableActions;
    }
    
    private CraftingState applyActions(CraftingState initialState, List<CraftingAction> actions) {
        CraftingState currentState = initialState.clone();
        
        for (CraftingAction action : actions) {
            if (action.canExecute(currentState)) {
                currentState = action.apply(currentState);
            } else {
                // エラー処理（実行できないアクションがある場合）
                throw new IllegalStateException("Cannot execute action: " + action.getName());
            }
        }
        
        return currentState;
    }
    
    // TODO wait time
    private String generateMacroText(List<CraftingAction> actions) {
        StringBuilder sb = new StringBuilder();
        int macroCount = 1;
        int actionIndex = 0;

        while (actionIndex < actions.size()) {
            sb.append("/micon クラフターマクロ").append(macroCount).append("\n");

            int limit = Math.min(15, actions.size() - actionIndex);
            for (int i = 0; i < limit; i++) {
                CraftingAction action = actions.get(actionIndex + i);
                sb.append("/ac \"").append(action.getName()).append("\" <wait.3>\n");
            }

            if (actionIndex + limit < actions.size()) {
                sb.append("\n");  // マクロ間に空行を挿入
            }
            
            actionIndex += limit;
            macroCount++;
        }

        return sb.toString();
    }
    
    public List<String> getAvailableSkills(int level) {
        return skillService.getSkillsByLevel(level)
                .stream()
                .map(CraftingAction::getName)
                .collect(Collectors.toList());
    }
}