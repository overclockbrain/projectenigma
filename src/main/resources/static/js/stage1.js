document.addEventListener("DOMContentLoaded", function () {
    const scaleArm = document.getElementById("scale-arm");
    const dropZones = document.querySelectorAll(".drop-zone"); // 皿と置き場
    const items = document.querySelectorAll(".draggable-item[draggable='true']"); // 動かせるやつだけ

    // 要素が足りない場合は実行しない
    if (!scaleArm) return;

    // グローバル変数を使わずに、現在ドラッグ中のアイテムを保持
    let currentDraggedItem = null;

    // 1. ドラッグ開始イベント設定
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

    // 2. ドロップゾーン（皿・置き場）イベント設定
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

    // 初期状態でも一度計算する
    updateScale();

    // 天秤の角度計算関数
    function updateScale() {
        const panLeft = document.getElementById("pan-left");
        const panRight = document.getElementById("pan-right");

        const weightLeft = calculateTotalWeight(panLeft);
        const weightRight = calculateTotalWeight(panRight);

        // 左が重いとマイナス(左下がり)、右が重いとプラス(右下がり)
        const diff = weightRight - weightLeft;

        // 角度リミッター（±45度まで）
        // 差が100あると45度傾くくらいの感度
        let angle = diff * 0.5;
        if (angle > 45) angle = 45;
        if (angle < -45) angle = -45;

        // アニメーション適用
        scaleArm.style.transform = `rotate(${angle}deg)`;

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

    // 正解判定（デバッグ機能付き）
    function checkClear(left, right) {
        const answerInput = document.getElementById("answer-input");
        const submitBtn = document.getElementById("submit-btn");

        // ボタン要素が見つからない場合はエラーを出す
        if (!submitBtn) {
            return;
        }

        // 判定ロジック: 両方に物が乗ってて、重さが同じならOK
        if (left > 0 && right > 0 && left === right) {
            // 1. 答えをセット
            if (answerInput) answerInput.value = "5050";
        } else {
            // 条件を満たさない時は隠す
            if (answerInput) answerInput.value = "";
        }
    }
});