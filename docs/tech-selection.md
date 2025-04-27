# CafterMacroGenerator 技術選定

## 概要

このドキュメントはFF14のクラフターマクロを自動生成するWebアプリケーション「CafterMacroGenerator」の技術選定に関する内容をまとめたものです。

## システム構成図

```
[ユーザー] ⟷ [フロントエンド] ⟷ [バックエンド] ⟷ [データベース]
```

## バックエンド技術

### 主要技術
- **フレームワーク**: Spring Boot
- **言語**: Java
- **API形式**: RESTful API

### 選定理由
- 既存のJavaベースのアルゴリズム実装を再利用可能
- 高いパフォーマンスと堅牢性
- 豊富なエコシステムと拡張性
- JPA/Hibernateによる効率的なデータアクセス

## フロントエンド技術

### 主要技術
- **フレームワーク**: React
- **言語**: TypeScript
- **ビルドツール**: Vite
- **ルーティング**: React Router
- **スタイリング**: Tailwind CSS
- **アイコン**: Phosphor Icons

### 状態管理・データフェッチング
- **状態管理**: Zustand
- **フォーム管理**: React Hook Form + Zod
- **APIクライアント**: SWR

### UI拡張
- **コンポーネントライブラリ**: Radix UI

### テスト
- **テストフレームワーク**: Jest
- **UI/コンポーネントテスト**: React Testing Library

### 開発者体験(DX)向上ツール
- **コード品質**: ESLint
- **コード整形**: Prettier

### 選定理由
- Reactの宣言的UIと豊富なエコシステム
- TypeScriptによる型安全性の確保
- Viteによる高速な開発・ビルド体験
- Zustandによる簡潔で効率的な状態管理
- Radix UIによるアクセシブルなUI構築
- React Hook Form + Zodによる型安全なフォーム管理
- SWRによる効率的なデータフェッチングとキャッシュ管理
- Tailwind CSSによる迅速なUI開発
- Jest + React Testing Libraryによる堅牢なテスト環境

## 開発環境

### フロントエンド開発環境
```bash
# プロジェクト作成
npm create vite@latest cafterMacroGenerator-frontend -- --template react-ts

# 必要パッケージのインストール
npm install react-router-dom zustand @radix-ui/react-* phosphor-react
npm install tailwindcss postcss autoprefixer
npm install react-hook-form zod @hookform/resolvers
npm install swr

# 開発ツールのインストール
npm install -D eslint prettier eslint-config-prettier
npm install -D jest @testing-library/react @testing-library/jest-dom
npm install -D @types/jest

# Tailwind CSS 初期化
npx tailwindcss init -p
```

### バックエンド開発環境
```bash
# Spring Initializrを使用してプロジェクト作成
# 依存関係:
# - Spring Web
# - Spring Data JPA
# - Spring Boot DevTools
# - Lombok
# - Validation
```

## データフロー

1. ユーザーが入力（プレイヤーステータス、レシピ情報など）
2. フロントエンドがバックエンドAPIに必要データを送信
3. バックエンドでマクロ生成アルゴリズムを実行
4. 生成されたマクロをフロントエンドに返却
5. フロントエンドでユーザーにマクロを表示・編集・保存機能を提供

## ディレクトリ構造

### フロントエンド
```
/src
  /assets        - 静的ファイル
  /components    - 再利用可能なUIコンポーネント
  /hooks         - カスタムReactフック
  /pages         - 各ページコンポーネント
  /services      - API通信などのサービス
  /stores        - Zustandストア
  /types         - TypeScript型定義
  /utils         - ユーティリティ関数
  /validation    - Zodバリデーションスキーマ
```

### バックエンド
```
/src/main/java/com/cafterMacroGenerator
  /config        - 設定クラス
  /controller    - REST APIコントローラー
  /service       - ビジネスロジック
  /repository    - データアクセス
  /entity        - データモデル
  /dto           - データ転送オブジェクト
  /algorithm     - マクロ生成アルゴリズム
  /exception     - 例外処理
  /util          - ユーティリティクラス
```

## 今後の検討事項

1. **データベース**:
   - 現段階では未選定
   - PostgreSQL, MySQLなどの関係データベースが有力候補

2. **認証・認可**:
   - ユーザー管理機能の要否に応じて追加検討
   - JWT、OAuth2などの選択肢

3. **デプロイメント・CI/CD**:
   - 現段階では未選定
   - AWSなどのクラウドサービスでの展開が有力候補

4. **モニタリング・ロギング**:
   - 本番環境での監視体制

5. **国際化（i18n）**:
   - 多言語対応の必要性に応じて検討

6. **アクセシビリティ**:
   - より包括的なアクセシビリティ対応

## 結論

本プロジェクトでは、バックエンドにSpring Boot、フロントエンドにReact/TypeScriptを採用し、モダンな開発環境を構築します。この技術スタックにより、高性能なマクロ生成エンジンと使いやすいユーザーインターフェースを兼ね備えたアプリケーションの開発が可能になります。
