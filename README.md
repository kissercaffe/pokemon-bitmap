# Pokemon Bitmap Search

ポケモン図鑑をビットマップインデックスを使って高速検索するScalaアプリケーションです。論理式を使ってポケモンのタイプによる検索が可能です。

## 必要な環境

- Scala 3.3.7
- sbt (Scala Build Tool)
- Java 11以上

## セットアップ

1. ポケモンCSVデータを配置:
データ取得先
https://www.kaggle.com/datasets/abcsds/pokemon

取得したらプロジェクト直下にdatasetディレクトリを作成してそこに格納する

## 使い方

### アプリケーションの実行

```bash
sbt run
```

### 検索クエリの例

アプリケーション起動後、以下のような検索クエリを入力できます:

- `Normal` - Normalタイプのポケモンを検索
- `Water & Fire` - WaterタイプかつFireタイプのポケモンを検索
- `Normal | Water` - NormalタイプまたはWaterタイプのポケモンを検索
- `Normal & (Water | Fire)` - Normalタイプかつ（WaterタイプまたはFireタイプ）のポケモンを検索
- `!Normal` - Normalタイプ以外のポケモンを検索
- `Water - Fire` - WaterタイプからFireタイプを除いたポケモンを検索
- `Water ^ Fire` - WaterタイプとFireタイプの排他的論理和

### 対応している演算子

- `&` または `and` または `AND`: 論理積（AND）
- `|` または `or` または `OR`: 論理和（OR）
- `!` または `not` または `NOT`: 否定（NOT）
- `-` または `diff` または `DIFF`: 差集合（A - B）
- `^` または `xor` または `XOR`: 排他的論理和（XOR）

### 終了方法

`quit` または `exit` と入力するとアプリケーションが終了します。

## プロジェクト構造

```
pokemon-bitmap/
├── src/
│   ├── main/
│   │   └── scala/
│   │       ├── app/
│   │       │   └── PokemonSearch.scala      # メインアプリケーション
│   │       ├── bitmap/
│   │       │   └── BitMap.scala             # ビットマップインデックス実装
│   │       ├── model/
│   │       │   └── Pokemon.scala            # ポケモンデータモデル
│   │       ├── parser/
│   │       │   └── LogicalExprParser.scala  # 論理式パーサー
│   │       └── zukan/
│   │           └── PokemonZukan.scala       # ポケモン図鑑クラス
│   └── test/
│       └── scala/
│           └── example/
│               └── HelloSpec.scala
├── build.sbt                                 # ビルド設定
└── README.md
```




