package com.appricot.feature.crafterMacroGenerator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;
import com.appricot.feature.crafterMacroGenerator.skills.BasicSynthesis;

class BasicSynthesisTest {
    
    private BasicSynthesis basicSynthesis;
    private CraftingState initialState;
    
    @BeforeEach
    void setUp() {
        basicSynthesis = new BasicSynthesis();
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
        CraftingState result = basicSynthesis.apply(initialState);
        
        // 検証
        assertEquals(120, result.getCurrentProgress()); // 基本効率120
        assertEquals(0, result.getCurrentQuality()); // 品質は変化なし
        assertEquals(30, result.getRemainingDurability()); // 耐久度-10
        assertEquals(400, result.getCurrentCP()); // CP消費なし
    }
    
    @Test
    void testApply_WithVenerationBuff() {
        // 確信バフ（作業効率+20%）を設定
        initialState.setAppliedBuffs(Arrays.asList("確信:3"));
        
        // 実行
        CraftingState result = basicSynthesis.apply(initialState);
        
        // 検証
        assertEquals(144, result.getCurrentProgress()); // 120 * 1.2 = 144
        assertEquals(0, result.getCurrentQuality()); // 品質は変化なし
        assertEquals(30, result.getRemainingDurability()); // 耐久度-10
        assertEquals(400, result.getCurrentCP()); // CP消費なし
    }
    
    @Test
    void testApply_WithMuscleMemoryBuff() {
        // 真価発揮バフ（作業効率+50%）を設定
        initialState.setAppliedBuffs(Arrays.asList("真価発揮:5"));
        
        // 実行
        CraftingState result = basicSynthesis.apply(initialState);
        
        // 検証
        assertEquals(180, result.getCurrentProgress()); // 120 * 1.5 = 180
        assertEquals(0, result.getCurrentQuality()); // 品質は変化なし
        assertEquals(30, result.getRemainingDurability()); // 耐久度-10
        assertEquals(400, result.getCurrentCP()); // CP消費なし
    }
    
    @Test
    void testApply_WithBothBuffs() {
        // 確信バフと真価発揮バフの両方を設定
        initialState.setAppliedBuffs(Arrays.asList("確信:3", "真価発揮:5"));
        
        // 実行
        CraftingState result = basicSynthesis.apply(initialState);
        
        // 検証 (120 * 1.5 * 1.2 = 216)
        assertEquals(216, result.getCurrentProgress());
        assertEquals(0, result.getCurrentQuality());
        assertEquals(30, result.getRemainingDurability());
        assertEquals(400, result.getCurrentCP());
    }
    
    @Test
    void testCanExecute_WithSufficientDurability() {
        // 耐久度が十分ある場合
        initialState.setRemainingDurability(10);
        
        // 検証
        assertTrue(basicSynthesis.canExecute(initialState));
    }
    
    @Test
    void testCanExecute_WithZeroDurability() {
        // 耐久度が0の場合
        initialState.setRemainingDurability(0);
        
        // 検証
        assertFalse(basicSynthesis.canExecute(initialState));
    }
}
