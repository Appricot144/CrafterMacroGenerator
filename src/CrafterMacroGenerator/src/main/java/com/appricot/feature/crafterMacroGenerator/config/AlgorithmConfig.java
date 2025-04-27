package com.appricot.feature.crafterMacroGenerator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.appricot.feature.crafterMacroGenerator.algorithm.BeamSearchOptimizer;
import com.appricot.feature.crafterMacroGenerator.algorithm.DynamicProgrammingOptimizer;
import com.appricot.feature.crafterMacroGenerator.algorithm.MacroOptimizer;

/**
 * アルゴリズム関連の設定クラス
 */
@Configuration
public class AlgorithmConfig {

    /**
     * デフォルトの最適化アルゴリズムを提供
     * @param dpOptimizer 動的計画法オプティマイザー
     * @param beamSearchOptimizer ビームサーチオプティマイザー
     * @return 最適化アルゴリズム
     */
    @Bean
    @Primary
    public MacroOptimizer defaultOptimizer(
            DynamicProgrammingOptimizer dpOptimizer,
            BeamSearchOptimizer beamSearchOptimizer) {
        
        // 設定やレシピの複雑さに応じて切り替えることが可能
        // デフォルトではビームサーチを使用（速度と品質のバランスが良い）
        return beamSearchOptimizer;
    }
    
    /**
     * アルゴリズム設定を提供
     * @return アルゴリズム設定
     */
    @Bean
    public AlgorithmSettings algorithmSettings() {
        return AlgorithmSettings.builder()
                .maxRecursionDepth(50)
                .beamWidth(1000)
                .useParallelization(true)
                .maxExecutionTimeMs(10000) // 最大実行時間 10秒
                .build();
    }
    
    /**
     * アルゴリズム設定クラス
     */
    public static class AlgorithmSettings {
        private final int maxRecursionDepth;
        private final int beamWidth;
        private final boolean useParallelization;
        private final long maxExecutionTimeMs;
        
        private AlgorithmSettings(Builder builder) {
            this.maxRecursionDepth = builder.maxRecursionDepth;
            this.beamWidth = builder.beamWidth;
            this.useParallelization = builder.useParallelization;
            this.maxExecutionTimeMs = builder.maxExecutionTimeMs;
        }
        
        public int getMaxRecursionDepth() {
            return maxRecursionDepth;
        }
        
        public int getBeamWidth() {
            return beamWidth;
        }
        
        public boolean isUseParallelization() {
            return useParallelization;
        }
        
        public long getMaxExecutionTimeMs() {
            return maxExecutionTimeMs;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private int maxRecursionDepth = 50;
            private int beamWidth = 1000;
            private boolean useParallelization = true;
            private long maxExecutionTimeMs = 10000;
            
            public Builder maxRecursionDepth(int maxRecursionDepth) {
                this.maxRecursionDepth = maxRecursionDepth;
                return this;
            }
            
            public Builder beamWidth(int beamWidth) {
                this.beamWidth = beamWidth;
                return this;
            }
            
            public Builder useParallelization(boolean useParallelization) {
                this.useParallelization = useParallelization;
                return this;
            }
            
            public Builder maxExecutionTimeMs(long maxExecutionTimeMs) {
                this.maxExecutionTimeMs = maxExecutionTimeMs;
                return this;
            }
            
            public AlgorithmSettings build() {
                return new AlgorithmSettings(this);
            }
        }
    }
}
