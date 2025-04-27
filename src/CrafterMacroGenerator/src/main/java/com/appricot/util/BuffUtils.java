package com.appricot.util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;

/**
 * クラフト状態のバフ管理を支援するユーティリティクラス
 */
public class BuffUtils {
    
    private static final Pattern BUFF_PATTERN = Pattern.compile("^(.+?)(?::(\\d+))?$");
    
    /**
     * バフが存在するかチェック
     * @param state クラフト状態
     * @param buffName バフ名（プレフィックスのみ）
     * @return バフが存在する場合はtrue
     */
    public static boolean hasBuff(CraftingState state, String buffName) {
        for (String buff : state.getAppliedBuffs()) {
            Matcher matcher = BUFF_PATTERN.matcher(buff);
            if (matcher.matches() && matcher.group(1).equals(buffName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * バフの残りターン数を取得
     * @param state クラフト状態
     * @param buffName バフ名（プレフィックスのみ）
     * @return バフの残りターン数、バフが存在しない場合は0
     */
    public static int getBuffDuration(CraftingState state, String buffName) {
        for (String buff : state.getAppliedBuffs()) {
            Matcher matcher = BUFF_PATTERN.matcher(buff);
            if (matcher.matches() && matcher.group(1).equals(buffName)) {
                if (matcher.group(2) != null) {
                    return Integer.parseInt(matcher.group(2));
                } else {
                    return 1; // 持続時間が指定されていない場合は1と見なす
                }
            }
        }
        return 0;
    }
    
    /**
     * バフを追加
     * @param state クラフト状態
     * @param buffName バフ名（プレフィックスのみ）
     * @param duration 持続ターン数
     */
    public static void addBuff(CraftingState state, String buffName, int duration) {
        // 既存のバフを削除
        removeBuff(state, buffName);
        
        // 新しいバフを追加
        List<String> buffs = new ArrayList<>(state.getAppliedBuffs());
        buffs.add(buffName + ":" + duration);
        state.setAppliedBuffs(buffs);
    }
    
    /**
     * バフを削除
     * @param state クラフト状態
     * @param buffName バフ名（プレフィックスのみ）
     */
    public static void removeBuff(CraftingState state, String buffName) {
        List<String> newBuffs = new ArrayList<>();
        for (String buff : state.getAppliedBuffs()) {
            Matcher matcher = BUFF_PATTERN.matcher(buff);
            if (!matcher.matches() || !matcher.group(1).equals(buffName)) {
                newBuffs.add(buff);
            }
        }
        state.setAppliedBuffs(newBuffs);
    }
    
    /**
     * すべてのバフの持続時間を1ターン減少させる
     * @param state クラフト状態
     */
    public static void decrementBuffDurations(CraftingState state) {
        List<String> currentBuffs = state.getAppliedBuffs();
        List<String> newBuffs = new ArrayList<>();
        
        for (String buff : currentBuffs) {
            Matcher matcher = BUFF_PATTERN.matcher(buff);
            if (matcher.matches()) {
                String buffName = matcher.group(1);
                String durationStr = matcher.group(2);
                
                if (durationStr != null) {
                    int duration = Integer.parseInt(durationStr);
                    if (duration > 1) {
                        // 持続時間が残っている場合は減少させる
                        newBuffs.add(buffName + ":" + (duration - 1));
                    }
                    // 持続時間が1以下になった場合はバフを除去（何もしない）
                } else {
                    // 持続時間が指定されていないバフはそのまま維持
                    newBuffs.add(buff);
                }
            } else {
                // パターンに一致しないバフはそのまま維持
                newBuffs.add(buff);
            }
        }
        
        state.setAppliedBuffs(newBuffs);
    }
}
