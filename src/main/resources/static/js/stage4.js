/**
 * Stage 4: Gravity Architect
 * 簡易グリッド物理エンジン (自動送信停止 & 手動ANSWER版)
 */
document.addEventListener('DOMContentLoaded', () => {
    // === 初期設定 ===
    const gridData = stage4Config.grid || [];
    const startPos = stage4Config.start || [0, 0];
    const rows = gridData.length;
    const cols = gridData[0].length;

    let ball = { r: startPos[0], c: startPos[1] };
    let isRedFlipped = false;
    let isBlueFlipped = false;
    let gameInterval = null;
    let isPlaying = false;

    // DOM要素
    const boardEl = document.getElementById('grid-board');
    const btnRed = document.getElementById('btn-red');
    const btnBlue = document.getElementById('btn-blue');
    const btnStart = document.getElementById('btn-start');

    // CSSに合わせて60pxグリッドに設定
    boardEl.style.gridTemplateColumns = `repeat(${cols}, 60px)`;

    // === 盤面描画 ===
    function renderBoard() {
        boardEl.innerHTML = '';

        gridData.forEach((row, r) => {
            row.forEach((type, c) => {
                const tile = document.createElement('div');
                tile.classList.add('tile');
                tile.dataset.r = r;
                tile.dataset.c = c;

                let goRight = false; // \
                let goLeft = false; // /

                if (type === 1) { // 赤
                    tile.classList.add('tile-red');
                    if (!isRedFlipped) goLeft = true;
                    else goRight = true;
                }
                else if (type === 2) { // 青
                    tile.classList.add('tile-blue');
                    if (!isBlueFlipped) goRight = true;
                    else goLeft = true;
                }
                // ★追加: スタート地点 (8)
                else if (type === 8) {
                    tile.classList.add('tile-start');
                    tile.textContent = 'START';
                }
                else if (type === 9) {
                    tile.classList.add('tile-goal');
                    tile.textContent = 'G';
                }

                // 坂道の見た目クラス付与
                if (goRight) tile.classList.add('slope-right');
                if (goLeft) tile.classList.add('slope-left');

                // ボール描画
                if (ball.r === r && ball.c === c) {
                    const ballEl = document.createElement('div');
                    ballEl.classList.add('ball');
                    tile.appendChild(ballEl);
                }

                boardEl.appendChild(tile);
            });
        });
    }

    // === ゲームループ ===
    function update() {
        const currentTileType = gridData[ball.r][ball.c];

        let nextR = ball.r + 1;
        let nextC = ball.c;

        // 移動ロジック
        if (currentTileType === 1) {
            nextC = isRedFlipped ? ball.c + 1 : ball.c - 1;
        } else if (currentTileType === 2) {
            nextC = isBlueFlipped ? ball.c - 1 : ball.c + 1;
        }

        // 画面外判定（失敗）
        if (nextC < 0 || nextC >= cols || nextR >= rows) {
            gameOver();
            return;
        }

        ball.r = nextR;
        ball.c = nextC;
        renderBoard();

        // ゴール判定
        if (gridData[ball.r] && gridData[ball.r][ball.c] === 9) {
            gameClear();
        }
    }

    // ★修正: 自動送信を止めて、メッセージを出すだけにする
    function gameClear() {
        stopGame();

        // 答え(パスワード)を裏でセットする
        const answerInput = document.getElementById('answer-input');
        if (answerInput) {
            answerInput.value = "NEWTON";
        }

        // ここで送信せずに、成功メッセージを表示
        // ユーザーに「ANSWER」ボタンを押させる
        showCommonMessage("GOAL!! Press the ANSWER button!", "success");
    }

    function gameOver() {
        stopGame();
        showCommonMessage("Failed... The star is lost.", "error");

        setTimeout(() => {
            resetBall();
            hideCommonMessage();
        }, 1500);
    }

    // 共通メッセージ表示関数
    function showCommonMessage(text, type) {
        let alertBox = document.querySelector('.alert');

        if (!alertBox) {
            alertBox = document.createElement('div');
            alertBox.className = 'alert';
            const hr = document.querySelector('hr');
            if (hr) {
                hr.after(alertBox);
            } else {
                const container = document.querySelector('.container');
                if (container) container.prepend(alertBox);
            }
        }

        alertBox.textContent = text;
        // type: "success" (緑) or "error" (赤)
        alertBox.className = `alert ${type}`;
        alertBox.style.display = 'block';
    }

    function hideCommonMessage() {
        const alertBox = document.querySelector('.alert');
        if (alertBox) {
            alertBox.style.display = 'none';
        }
    }

    function stopGame() {
        isPlaying = false;
        clearInterval(gameInterval);
        btnStart.disabled = false;
    }

    function resetBall() {
        ball = { r: startPos[0], c: startPos[1] };
        renderBoard();
    }

    // イベントリスナー
    btnRed.addEventListener('click', () => {
        isRedFlipped = !isRedFlipped;
        renderBoard();
    });

    btnBlue.addEventListener('click', () => {
        isBlueFlipped = !isBlueFlipped;
        renderBoard();
    });

    btnStart.addEventListener('click', () => {
        if (isPlaying) return;
        hideCommonMessage();
        resetBall();
        isPlaying = true;
        btnStart.disabled = true;
        gameInterval = setInterval(update, 500);
    });

    renderBoard();
});