/**
 * Stage 1: Balance Scale Logic
 * 天秤のシミュレーションとドラッグ＆ドロップ制御を行う。
 * @author R.Morioka
 * @version 1.1 (マジックナンバー排除・Fix版)
 */
document.addEventListener("DOMContentLoaded", function () {
    // === 🔧 CONFIG (設定値) ===
    const CONFIG = {
        MAX_ANGLE: 45,          // 天秤の最大傾き（度）
        SENSITIVITY: 0.5,       // 重さの差に対する傾きの感度
        TARGET_WEIGHT: 50,      // 目標とする片側の重さ
        ANIMATION_SPEED: 0.3,   // アニメーション秒数
    };

    const scaleArm = document.getElementById("scale-arm");
    const dropZones = document.querySelectorAll(".drop-zone");
    const items = document.querySelectorAll(".draggable-item[draggable='true']");
    const answerInput = document.getElementById("answer-input");
    const submitBtn = document.getElementById("submit-btn");

    // 要素が足りない場合は実行しない
    if (!scaleArm) return;

    let currentDraggedItem = null;

    // === 1. ドラッグ開始イベント設定 ===
    items.forEach(item => {
        item.addEventListener("dragstart", function (e) {
            currentDraggedItem = this;
            e.dataTransfer.setData("text/plain", "dragging");
            e.dataTransfer.effectAllowed = "move";
            this.style.opacity = "0.4";
        });

        item.addEventListener("dragend", function (e) {
            this.style.opacity = "1";
            currentDraggedItem = null;
        });
    });

    // === 2. ドロップゾーン（皿・置き場）イベント設定 ===
    dropZones.forEach(zone => {
        zone.addEventListener("dragover", function (e) {
            e.preventDefault(); // これがないとdropできない
            e.dataTransfer.dropEffect = "move";
        });

        zone.addEventListener("drop", function (e) {
            e.preventDefault();

            if (currentDraggedItem) {
                // DOM移動（HTML上で場所が変わる）
                this.appendChild(currentDraggedItem);

                // 重さを再計算して天秤を動かす
                updateScale();
            }
        });
    });

    /**
     * 天秤の状態を更新する関数
     */
    function updateScale() {
        const panLeft = document.getElementById("pan-left");
        const panRight = document.getElementById("pan-right");

        const weightLeft = calculateTotalWeight(panLeft);
        const weightRight = calculateTotalWeight(panRight);

        // 左が重いとマイナス、右が重いとプラス
        const diff = weightRight - weightLeft;

        // ★ マジックナンバー排除！ CONFIGを使う
        let angle = diff * CONFIG.SENSITIVITY;

        // 角度リミッター
        if (angle > CONFIG.MAX_ANGLE) angle = CONFIG.MAX_ANGLE;
        if (angle < -CONFIG.MAX_ANGLE) angle = -CONFIG.MAX_ANGLE;

        // アニメーション適用
        scaleArm.style.transform = `rotate(${angle}deg)`;
        scaleArm.style.transition = `transform ${CONFIG.ANIMATION_SPEED}s ease-out`;

        // クリア判定
        checkClear(weightLeft, weightRight);
    }

    // 重さ合計計算
    function calculateTotalWeight(container) {
        let total = 0;
        if (!container) return 0;

        const weights = container.querySelectorAll("[data-weight]");
        weights.forEach(w => {
            total += parseInt(w.getAttribute("data-weight") || "0");
        });
        return total;
    }

    /**
     * 判定ロジック修正版
     * 正誤に関わらず常に値をセットし、ボタンを押せるようにする
     */
    function checkClear(left, right) {
        // ボタンが存在しない場合は何もしない
        if (!submitBtn) return;

        // これでサーバーに "3020" とか "00" が送られて、向こうで判定してもらえる
        if (answerInput) {
            answerInput.value = `${left}${right}`;
        }
    }

    // 初期状態の更新
    updateScale();
});