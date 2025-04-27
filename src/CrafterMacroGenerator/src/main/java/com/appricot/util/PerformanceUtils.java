package com.appricot.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;
import java.util.function.Supplier;

import com.appricot.feature.crafterMacroGenerator.entity.CraftingState;

/**
 * パフォーマンス最適化のためのユーティリティクラス
 */
// TODO read code
public class PerformanceUtils {
    
    // 並列処理用のForkJoinPool
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
    
    /**
     * メモ化されたコンピューテーション
     * @param <T> 入力タイプ
     * @param <R> 結果タイプ
     * @param function 計算関数
     * @return メモ化された関数
     */
    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        ConcurrentHashMap<T, R> cache = new ConcurrentHashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }
    
    /**
     * 並列化された探索を実行
     * @param task 実行するタスク
     * @param <T> 結果タイプ
     * @return 計算結果
     */
    public static <T> T runParallel(ForkJoinTask<T> task) {
        return FORK_JOIN_POOL.invoke(task);
    }
    
    /**
     * 計算時間を測定
     * @param operation 実行する処理
     * @param <T> 結果タイプ
     * @return 実行結果と計算時間のペア
     */
    public static <T> ExecutionResult<T> measureExecutionTime(Supplier<T> operation) {
        long startTime = System.currentTimeMillis();
        T result = operation.get();
        long endTime = System.currentTimeMillis();
        
        return new ExecutionResult<>(result, endTime - startTime);
    }
    
    /**
     * 実行結果とパフォーマンス情報を保持するクラス
     * @param <T> 結果タイプ
     */
    public static class ExecutionResult<T> {
        private final T result;
        private final long executionTimeMs;
        
        public ExecutionResult(T result, long executionTimeMs) {
            this.result = result;
            this.executionTimeMs = executionTimeMs;
        }
        
        public T getResult() {
            return result;
        }
        
        public long getExecutionTimeMs() {
            return executionTimeMs;
        }
    }
    
    /**
     * クラフト状態のハッシュコードを最適化
     * 状態を効率的にメモ化するために使用
     * @param state クラフト状態
     * @return 最適化されたハッシュコード
     */
    public static int optimizedStateHash(CraftingState state) {
        // 重要な状態の要素のみを使用したハッシュ計算
        int result = 17;
        result = 31 * result + state.getCurrentProgress();
        result = 31 * result + state.getCurrentQuality();
        result = 31 * result + state.getRemainingDurability();
        result = 31 * result + state.getCurrentCP();
        
        // バフのハッシュ（文字列連結の代わりにハッシュを直接計算）
        for (String buff : state.getAppliedBuffs()) {
            result = 31 * result + buff.hashCode();
        }
        
        // アクション履歴はサイズのみを使用
        result = 31 * result + state.getUsedActions().size();
        
        return result;
    }
    
    /**
     * 並列探索用のタスク基底クラス
     * @param <T> 結果タイプ
     */
    public static abstract class ParallelSearchTask<T> extends RecursiveTask<T> {
        private static final int THRESHOLD = 10; // 並列化の閾値
        
        protected boolean shouldComputeDirectly(int workSize) {
            return workSize <= THRESHOLD;
        }
    }
}
