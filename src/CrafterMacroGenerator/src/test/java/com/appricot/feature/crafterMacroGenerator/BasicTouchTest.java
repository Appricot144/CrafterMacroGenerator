package com.appricot.feature.crafterMacroGenerator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;

class BasicTouchTest {
    
    private BasicTouch basicTouch;
    private CraftingState initialState;
    
    @BeforeEach
    void setUp() {
        basicTouch = new BasicTouch();
        initialState = CraftingState.builder()
                .currentProgress(0)
                .currentQuality(0)
                .remainingDurability(40)
                .currentCP(400)
                .appliedBuffs(new ArrayList<>())
                .usedActions(new ArrayList<>())
                .build();
    }
    
    @Test
    void testApply_NoBuffs() {
        // 実行
        CraftingState result = basicTouch.apply(initialState);
        
        // 検証
        assertEquals(0, result.getCurrentProgress()); // 進捗は変化なし
        assertEquals(100, result.getCurrentQuality()); // 基本効率100
        assertEquals(30, result.getRemainingDurability()); // 耐久度-10
        assertEquals(382, result.getCurrentCP()); // CP消費18
    }
    
    @Test
    void testApply_WithInnovationBuff() {
        // 改革バフ（加工効率+50%）を設定
        initialState.setAppliedBuffs(Arrays.asList("改革:3"));
        
        // 実行
        CraftingState result = basicTouch.apply(initialState);
        
        // 検証
        assertEquals(0, result.getCurrentProgress());
        assertEquals(150, result.getCurrentQuality()); // 100 * 1.5 = 150
        assertEquals(30, result.getRemainingDurability());
        assertEquals(382, result.getCurrentCP());
    }
    
    @Test
    void testApply_WithInnerQuietBuff() {
        // 内静バフ（スタック1）を設定
        initialState.setAppliedBuffs(Arrays.asList("内静:1"));
        
        // 実行
        CraftingState result = basicTouch.apply(initialState);
        
        // 検証
        assertEquals(0, result.getCurrentProgress());
        assertEquals(110, result.getCurrentQuality()); // 100 * (1 + 0.1) = 110（内静スタック1）
        assertEquals(30, result.getRemainingDurability());
        assertEquals(382, result.getCurrentCP());
        
        // 内静スタックが増加していることを確認
        boolean hasIncreasedStack = false;
        for (String buff : result.getAppliedBuffs()) {
            if (buff.equals("内静:2")) {
                hasIncreasedStack = true;
                break;
            }
        }
        assertTrue(hasIncreasedStack, "内静スタックが増加していません");
    }
    
    @Test
    void testApply_WithInnerQuietMaxStacks() {
        // 内静バフ（最大スタック11）を設定
        initialState.setAppliedBuffs(Arrays.asList("内静:11"));
        
        // 実行
        CraftingState result = basicTouch.apply(initialState);
        
        // 検証
        assertEquals(0, result.getCurrentProgress());
        assertEquals(210, result.getCurrentQuality()); // 100 * (1 + 1.1) = 210（内静スタック11）
        assertEquals(30, result.getRemainingDurability());
        assertEquals(382, result.getCurrentCP());
        
        // 内静スタックが最大値で維持されていることを確認
        boolean hasMaxStack = false;
        for (String buff : result.getAppliedBuffs()) {
            if (buff.equals("内静:11")) {
                hasMaxStack = true;
                break;
            }
        }
        assertTrue(hasMaxStack, "内静スタックが最大値で維持されていません");
    }
    
    @Test
    void testCanExecute_WithSufficientCPAndDurability() {
        // CP、耐久度が十分ある場合
        initialState.setCurrentCP(20);
        initialState.setRemainingDurability(10);
        
        // 検証
        assertTrue(basicTouch.canExecute(initialState));
    }
    
    @Test
    void testCanExecute_WithInsufficientCP() {
        // CPが不足している場合
        initialState.setCurrentCP(17);
        
        // 検証
        assertFalse(basicTouch.canExecute(initialState));
    }
    
    @Test
    void testCanExecute_WithZeroDurability() {
        // 耐久度が0の場合
        initialState.setRemainingDurability(0);
        
        // 検証
        assertFalse(basicTouch.canExecute(initialState));
    }
}
