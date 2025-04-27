# FF14 クラフターマクロ自動生成システム 仕様書

## 1. システム概要

FF14 のクラフティングシステムにおいて最適なマクロを動的計画法とヒューリスティックアプローチを用いて自動生成するシステム。プレイヤーのステータス、レシピ情報、使用可能なスキルを入力として、品質と効率を最大化するマクロシーケンスを生成する。

## 2. 主要コンポーネント

### 2.1 マクロ探索エンジン

動的計画法を用いた最適マクロ探索の中核エンジン

### 2.2 スキル評価モジュール

各スキルの効果を計算し評価するモジュール

### 2.3 状態管理システム

クラフティング進行中の状態表現と遷移を管理

### 2.4 最適化アルゴリズム

最適なマクロシーケンスを導出するアルゴリズム

### 2.5 結果生成モジュール

最適化結果をゲーム内で使用可能なマクロテキストに変換

## 3. データモデル

### 3.1 CraftingState (クラフト状態)

```java
public class CraftingState {
    private int currentProgress;     // 現在の作業進捗
    private int currentQuality;      // 現在の品質
    private int remainingDurability; // 残り耐久度
    private int currentCP;           // 現在のクラフターポイント
    private List<String> appliedBuffs; // 適用中のバフ
    private List<String> usedActions; // 使用済みアクション
}
```

### 3.2 CraftingAction (クラフトアクション)

```java
public abstract class CraftingAction {
    private String name;
    private int cpCost;
    private ActionType type;

    public abstract CraftingState apply(CraftingState currentState);
    public abstract boolean canExecute(CraftingState currentState);
}
```

### 3.3 PlayerStatus (プレイヤーステータス)

```java
public class PlayerStatus {
    private int craftingLevel;
    private int craftsmanship;
    private int control;
    private int cp;
}
```

### 3.4 Recipe (レシピ)

```java
public class Recipe {
    private String name;
    private int requiredProgress;  // 必要作業進捗値
    private int maxQuality;         // 最大品質値
    private int baseDurability;     // 基本耐久度
    private DifficultyRank difficulty;
}
```

## 4. アルゴリズム詳細

### 4.1 動的計画法による最適化

- 状態空間を探索し、最適なアクション系列を発見
- メモ化により重複計算を排除
- 終了条件：必要作業値達成、かつ品質の最大化

### 4.2 評価関数

- 主評価指標：最終品質値（最大値以内で最大化）
- 副評価指標：CP 効率、アクション数

### 4.3 探索空間の制約

- CP 制約：利用可能な CP の範囲内
- 耐久度制約：0 以下にならないこと
- マクロ長制約：最大 15 アクション/マクロセット

## 5. ヒューリスティック最適化

### 5.1 状態評価ヒューリスティック

- **進捗/CP 効率スコア**: 各アクションの作業進捗 ÷CP 消費
- **品質/CP 効率スコア**: 各アクションの品質上昇 ÷CP 消費
- **終了状態予測**: 現在の状態から目標達成までの見積もりコスト

### 5.2 探索空間最適化

- **ビームサーチ**: 常に最も有望な K 個の状態のみを保持
- **モンテカルロ木探索**: ランダムサンプリングによる部分探索
- **A\*アルゴリズム**: 実コスト+推定残りコストによる最適経路探索

### 5.3 パターン認識

- **既知の効果的シーケンス**: 定番の組み合わせを優先的に評価
- **フェーズベース戦略**: 作業工程の段階に応じた特化ヒューリスティック
- **レシピ類似性活用**: 類似レシピの成功マクロを参考

## 6. 実装詳細

### 6.1 メモ化実装

```java
Map<CraftingState, OptimizationResult> memo = new HashMap<>();
```

### 6.2 最適マクロ探索

```java
private List<CraftingAction> findOptimalMacroPath(
    CraftingState currentState,
    Map<CraftingState, OptimizationResult> memo
) {
    // メモ化チェック
    if (memo.containsKey(currentState)) {
        return memo.get(currentState).actionPath;
    }

    // 終了条件チェック
    if (isCompletedRecipe(currentState)) {
        return new ArrayList<>();
    }

    // アクション探索ロジック
    // ...

    // メモ化して結果を返却
    memo.put(currentState, new OptimizationResult(bestPath, bestScore));
    return bestPath;
}
```

### 6.3 マクロテキスト生成

```java
public String generateMacroText(List<CraftingAction> actions) {
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

        actionIndex += limit;
        macroCount++;
    }

    return sb.toString();
}
```

## 7. パフォーマンス最適化

### 7.1 状態表現の最適化

- ビット圧縮による状態表現
- ハッシュ計算の最適化

### 7.2 並列処理

- 複数の開始状態からの並列探索
- 多スレッドによる探索空間分割

### 7.3 キャッシング戦略

- 頻出パターンの事前計算
- レシピ特性ごとの最適戦略キャッシュ

## 8. 今後の改善計画

### 8.1 アルゴリズム改善

- **強化学習アプローチ**: マクロ生成を強化学習問題として再定義
- **遺伝的アルゴリズム**: 複数の候補マクロを交叉・突然変異で進化させる
- **ニューラルネットワークによる状態評価**: 状態の有望度を学習ベースで評価

#### **パターン認識と事前知識**

- **既知の効果的シーケンス**: 定番の組み合わせ（例:「インナークワイエット」→「倹約加工」）を優先
- **フェーズベースの戦略**: 作業工程を「初期」「中盤」「仕上げ」に分けた特化ヒューリスティック
- **レシピ類似性活用**: 類似レシピで成功したマクロ構造を初期解として利用

#### ハイブリッドアプローチ

- **A\*アルゴリズム**: 実コスト+推定残りコストによる最適経路探索
- **遺伝的アルゴリズム**: 複数のマクロ候補を交叉・突然変異で進化させる
- **深さ制限探索**: マクロ長制限内で最適解を探索する反復深化

### 8.2 ユーザビリティ改善

- **GUI インターフェース**: 直感的な操作環境の提供
- **マクロシミュレーター**: 生成マクロの視覚的シミュレーション
- **複数マクロ候補提案**: 異なる戦略の複数候補の提示

### 8.3 データ連携

- **レシピデータベース自動更新**: パッチごとの自動データ更新
