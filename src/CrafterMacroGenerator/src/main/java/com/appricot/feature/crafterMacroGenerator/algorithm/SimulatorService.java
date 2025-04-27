package com.appricot.feature.crafterMacroGenerator.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.appricot.exception.MacroGenerationException;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingAction;
import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;
import com.appricot.feature.crafterMacroGenerator.entity.Recipe;

/**
 * クラフトシミュレーターサービス
 * 一連のアクションをシミュレートして結果を計算
 * TODO read code
 */
@Service
public class SimulatorService {
    
    /**
     * アクションシーケンスをシミュレート
     * @param initialState 初期状態
     * @param actions シミュレートするアクション配列
     * @param recipe レシピ情報
     * @return シミュレーション結果（各ステップの状態リスト）
     */
    public List<CraftingState> simulateActionSequence(
            CraftingState initialState,
            List<CraftingAction> actions,
            Recipe recipe) {
        
        List<CraftingState> stateHistory = new ArrayList<>();
        
        // 初期状態をコピー
        CraftingState currentState = initialState.clone();
        stateHistory.add(currentState.clone());
        
        // 各アクションを順に適用
        for (CraftingAction action : actions) {
            // アクションが実行可能かチェック
            if (!action.canExecute(currentState)) {
                throw new MacroGenerationException(
                        "アクション '" + action.getName() + "' は現在の状態では実行できません");
            }
            
            // アクションを適用
            currentState = action.apply(currentState);
            
            // バフの持続時間を減少
            decrementBuffDurations(currentState);
            
            // 使用したアクションを記録
            currentState.getUsedActions().add(action.getName());
            
            // 状態履歴に追加
            stateHistory.add(currentState.clone());
            
            // 耐久度チェック
            if (currentState.getRemainingDurability() <= 0) {
                break;
            }
            
            // 作業進捗達成チェック
            if (currentState.getCurrentProgress() >= recipe.getRequiredProgress()) {
                break;
            }
        }
        
        return stateHistory;
    }
    
    /**
     * 指定されたスキル名のシーケンスをシミュレート
     * @param initialState 初期状態
     * @param skillNames シミュレートするスキル名配列
     * @param availableSkills 利用可能なスキルマップ（名前→アクション）
     * @param recipe レシピ情報
     * @return シミュレーション結果（各ステップの状態リスト）
     */
    public List<CraftingState> simulateSkillNameSequence(
            CraftingState initialState,
            List<String> skillNames,
            Map<String, CraftingAction> availableSkills,
            Recipe recipe) {
        
        // スキル名からアクションリストを作成
        List<CraftingAction> actions = new ArrayList<>();
        for (String skillName : skillNames) {
            CraftingAction action = availableSkills.get(skillName);
            if (action == null) {
                throw new MacroGenerationException("スキル '" + skillName + "' は存在しません");
            }
            actions.add(action);
        }
        
        // アクションシーケンスをシミュレート
        return simulateActionSequence(initialState, actions, recipe);
    }
    
    /**
     * バフの持続時間を減少させる
     * @param state 現在の状態
     */
    private void decrementBuffDurations(CraftingState state) {
        List<String> currentBuffs = state.getAppliedBuffs();
        List<String> newBuffs = new ArrayList<>();
        
        // 各バフを処理
        for (String buff : currentBuffs) {
            // バフ名と持続時間（存在する場合）を分離
            String[] parts = buff.split(":");
            String buffName = parts[0];
            
            if (parts.length > 1) {
                // 持続時間のあるバフ
                int duration = Integer.parseInt(parts[1]);
                if (duration > 1) {
                    // 持続時間を減少して追加
                    newBuffs.add(buffName + ":" + (duration - 1));
                }
                // 持続時間が1以下になった場合はバフを削除（何もしない）
            } else {
                // 持続時間のないバフはそのまま維持
                newBuffs.add(buff);
            }
        }
        
        state.setAppliedBuffs(newBuffs);
    }
    
    /**
     * マクロテキストとシミュレーション結果から最終状態を計算
     * @param initialState 初期状態
     * @param macroText マクロテキスト
     * @param availableSkills 利用可能なスキルマップ
     * @param recipe レシピ情報
     * @return 最終状態
     */
    public CraftingState calculateFinalStateFromMacroText(
            CraftingState initialState,
            String macroText,
            Map<String, CraftingAction> availableSkills,
            Recipe recipe) {
        
        // マクロテキストからスキル名のリストを抽出
        List<String> skillNames = extractSkillNamesFromMacroText(macroText);
        
        // スキルシーケンスをシミュレート
        List<CraftingState> stateHistory = simulateSkillNameSequence(
                initialState, skillNames, availableSkills, recipe);
        
        // 最終状態を返す
        return stateHistory.get(stateHistory.size() - 1);
    }
    
    /**
     * マクロテキストからスキル名を抽出
     * @param macroText マクロテキスト
     * @return 抽出されたスキル名のリスト
     */
    private List<String> extractSkillNamesFromMacroText(String macroText) {
        List<String> skillNames = new ArrayList<>();
        
        // 各行を処理
        for (String line : macroText.split("\n")) {
            // /ac コマンドを探す
            if (line.startsWith("/ac ")) {
                // スキル名を抽出（"..."の間）
                int startQuote = line.indexOf('"');
                int endQuote = line.lastIndexOf('"');
                
                if (startQuote >= 0 && endQuote > startQuote) {
                    String skillName = line.substring(startQuote + 1, endQuote);
                    skillNames.add(skillName);
                }
            }
        }
        
        return skillNames;
    }
    
    /**
     * スキル名からアクションのマップを作成
     * @param actions アクションのリスト
     * @return スキル名→アクションのマップ
     */
    public Map<String, CraftingAction> createSkillNameToActionMap(List<CraftingAction> actions) {
        Map<String, CraftingAction> map = new HashMap<>();
        for (CraftingAction action : actions) {
            map.put(action.getName(), action);
        }
        return map;
    }
}
