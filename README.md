# Project Enigma 🧩

## 概要 (Overview)
次世代謎解きプラットフォーム「Project Enigma」の実装。
Spring Bootをベースに、謎解きロジックとWebインターフェースを開発するプロジェクト。

## 技術スタック (Tech Stack)
* **Language**: Java 21
* **Framework**: Spring Boot 3.x
* **Template Engine**: Thymeleaf
* **Database**: H2 Database (In-Memory / Dev)
* **Utils**: Lombok, Spring Boot DevTools
* **Environment**: VSCode Dev Containers

## 開発環境のセットアップ (Dev Environment)

本プロジェクトは **VSCode Dev Containers** を使用して、Docker上で統一された開発環境を構築します。
以下の手順に従ってセットアップを行ってください。

### 1. 事前準備 (Prerequisites)
以下のツールがインストールされているか確認してください。
まだの場合は、リンク先からダウンロード・インストールをお願いします。

* **Docker Desktop** (必須)
    * [ダウンロードはこちら](https://www.docker.com/products/docker-desktop/)
    * ※インストール後、Dockerが起動していることを確認してください。
* **Visual Studio Code** (必須)
    * [ダウンロードはこちら](https://code.visualstudio.com/)
* **Dev Containers (VSCode拡張機能)** (必須)
    * [マーケットプレイスはこちら](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)
    * ※VSCodeの左側メニュー「拡張機能」から `ms-vscode-remote.remote-containers` で検索してもインストール可能です。

### 2. 起動手順 (How to Start)
環境構築は自動化されています。

1.  VSCodeでこのプロジェクトフォルダを開く。
2.  画面左下の緑色のボタン（または `F1` キー）をクリック。
3.  メニューから **"Dev Containers: Reopen in Container"** を選択。
4.  初回はDockerイメージのビルドが走るため、数分待機します。
5.  完了すると、Java 21等の必要な環境が全て整った状態でプロジェクトが開かれます。

### 3. アプリケーション起動 (Run)
コンテナ内のターミナルで以下を実行してください：
```bash
./mvnw spring-boot:run
```
起動後、ブラウザで`http://localhost:8080`にアクセスして画面が表示されれば成功です。

## データベース確認 (H2 Console)
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password: (空欄)

## ロードマップ (Roadmap)
- [ ] プロジェクト基盤の構築 (Dev Container完了)
- [ ] 謎解き回答判定ロジックの実装
- [ ] ステージ進行管理機能
- [ ] UI/UXプロトタイピング

## License
Private Project