# CafterMacroGenerator バックエンド

FF14 のクラフターマクロを自動生成する Web アプリケーション「CafterMacroGenerator」のバックエンド部分です。

## 技術スタック

- **フレームワーク**: Spring Boot
- **言語**: Java
- **API 形式**: RESTful API

## プロジェクト構造

```
/src/main/java/com/cafterMacroGenerator
  /config        - 設定クラス
  /controller    - REST APIコントローラー
  /service       - ビジネスロジック
  /entity        - データモデル
  /dto           - データ転送オブジェクト
  /algorithm     - マクロ生成アルゴリズム
    /skills      - スキル実装
  /exception     - 例外処理
  /util          - ユーティリティクラス
```

## API エンドポイント

### マクロ生成

```
POST /api/macro/generate
```

リクエスト例:

```json
{
  "playerStatus": {
    "craftingLevel": 80,
    "craftsmanship": 2800,
    "control": 2700,
    "cp": 550
  },
  "recipe": {
    "name": "クモモドキの金糸",
    "requiredProgress": 3500,
    "maxQuality": 9000,
    "baseDurability": 70,
    "difficulty": "EXPERT"
  },
  "availableSkills": [
    "作業",
    "模範作業",
    "加工",
    "内静",
    "確信",
    "改革",
    "ビエルゴの祝福"
  ],
  "qualityFocus": true,
  "durabilityConstraint": true
}
```

レスポンス例:

```json
{
  "macroText": "/micon クラフターマクロ1\n/ac \"内静\" <wait.3>\n/ac \"確信\" <wait.3>\n...",
  "actionSequence": ["内静", "確信", "模範作業", ...],
  "finalQuality": 7200,
  "finalProgress": 3500,
  "totalCPUsed": 480,
  "durabilityRemaining": 10,
  "qualityPercentage": 80,
  "progressComplete": true,
  "calculationTimeMs": 253,
  "exploredStates": 4289
}
```

### 利用可能スキル取得

```
GET /api/macro/available-skills?level=80
```

レスポンス例:

```json
[
  "作業",
  "模範作業",
  "グラウンドワーク",
  "加工",
  "中級加工",
  "ビエルゴの祝福",
  "内静",
  "確信",
  "改革",
  "マスターズメンド",
  ...
]
```

## アルゴリズム概要

本システムでは、動的計画法とヒューリスティックアプローチを組み合わせたアルゴリズムを使用しています。

1. **状態空間の定義**: クラフトの進行状況（進捗、品質、耐久度、CP 残量、適用中のバフ）を状態として定義
2. **メモ化による最適化**: 同じ状態の再計算を避けるためにメモ化を使用
3. **評価関数**: 各状態の価値を品質、進捗、CP 効率などの要素から計算
4. **ヒューリスティック探索**: 現実的な時間内で最適解に近い結果を得るための探索空間制限
5. **バフ管理**: スキル効果とバフの相互作用を考慮した状態遷移処理

### セットアップ

```bash
# リポジトリをクローン
git clone https://github.com/yourusername/cafterMacroGenerator-backend.git
cd cafterMacroGenerator-backend

# Mavenでビルド
./mvnw clean install

# 開発サーバー起動
./mvnw spring-boot:run
```

サーバーは`http://localhost:8080/api`で起動します。

## テスト実行

```bash
# 全テスト実行
./mvnw test

# 特定のテストクラスのみ実行
./mvnw test -Dtest=BasicSynthesisTest
```

## ライセンス

このプロジェクトは MIT ライセンスのもとで公開されています。詳細は[LICENSE](LICENSE)ファイルを参照してください。
