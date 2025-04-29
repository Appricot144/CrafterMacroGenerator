# TODO

## quick

- [ ] クラフターデータの収集 (xivapi)
  - [ ] actions
  - [ ] buff effects
- [ ] implement Java
  - [ ] 各種アクション CrafterAction クラス実装
  - [ ] 各種バフ Effects クラス実装
  - [ ] sample recipe data の作成（一つ）
- [ ] バックエンドのみのシミュレーションの実装

## backend

### Crafter data-set

- [ ] クラフターデータの収集 (xivapi)
  - [ ] 各種アクションの作成処理
  - [ ] 各種バフ Effects の作成処理
  - [ ] sample recipe data の作成（一つ）
  - [ ] 固定のデータセットの Java 実装
    - [ ] actions
    - [ ] buff effects
    - [ ] recipes
    - [ ] meals
    - [ ] potions

### simulation

- [ ] バックエンドのみのシミュレーションの実装

## frontend

- [ ] response JSON データ構造の決定
- [ ] request JSON データ構造の決定
- [ ] 画面作成
- [ ] 疎通確認

## 優先度低めの改善

### data-set updater

- [ ] データセットのアップデート処理の実装

### A\*algorithm

- [ ] ヒューリスティック関数の調整
  - [ ] 階層的アプローチの実装（進捗 > 品質 > 残 cp,耐久値）
