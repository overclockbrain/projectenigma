/**
 * Stage 3: Fix the Leak (Order Agnostic)
 * 順番関係なく、リングと蓋の両方が揃えばクリア。
 */
document.addEventListener("DOMContentLoaded", function () {
    const leakHole = document.querySelector(".leak-hole");
    const answerInput = document.getElementById("answer-input");
    const submitBtn = document.getElementById("submit-btn");

    setupFooterGimmick();

    // アイテムのドラッグ設定
    setupDragItem(document.querySelector(".solid-part"));

    if (leakHole) {
        leakHole.addEventListener("dragover", (e) => {
            e.preventDefault();
            e.dataTransfer.dropEffect = "move";
        });

        leakHole.addEventListener("drop", function (e) {
            e.preventDefault();
            if (!currentDraggedItem) return;

            const itemType = currentDraggedItem.getAttribute("data-type");

            // 既にそのパーツが入ってたら無視
            if (itemType === "oring" && this.classList.contains("has-ring")) return;
            if (itemType === "solid" && this.classList.contains("has-lid")) return;
            if (this.classList.contains("fixed")) return;

            // --- 状態更新 ---
            if (itemType === "oring") {
                this.classList.add("has-ring");
            } else if (itemType === "solid") {
                this.classList.add("has-lid");
            }

            // ドロップされたアイテムを消す（穴に入った演出）
            currentDraggedItem.style.display = "none";
            currentDraggedItem = null; // ドラッグ状態リセット

            // --- クリア判定 ---
            // 両方のクラスが揃っていたらクリア！
            if (this.classList.contains("has-ring") && this.classList.contains("has-lid")) {
                this.classList.add("fixed");
                checkClear();
            }
        });
    }

    // --- フッターハックなど（変更なし） ---
    let currentDraggedItem = null;

    function setupFooterGimmick() {
        const homeLink = document.querySelector(".footer-home");
        if (!homeLink || homeLink.querySelector(".stolen-letter")) return;

        const originalText = homeLink.innerHTML;
        if (originalText.toLowerCase().includes("home")) {
            const newHtml = originalText.replace(/home/i, (match) => {
                const h = match.charAt(0);
                const o = match.charAt(1);
                const me = match.substring(2);
                // フッターは「Oリング(oring)」扱い
                return `${h}<span class="stolen-letter" draggable="true" data-type="oring">${o}</span>${me}`;
            });
            homeLink.innerHTML = newHtml;
            setupDragItem(homeLink.querySelector(".stolen-letter"));
        }
    }

    function setupDragItem(item) {
        if (!item) return;
        item.addEventListener("dragstart", function (e) {
            currentDraggedItem = this;
            e.dataTransfer.effectAllowed = "move";
            setTimeout(() => { this.style.opacity = "0.1"; }, 0);
        });
        // ドロップ失敗した時に戻す処理が必要ならここに追加
        item.addEventListener("dragend", function (e) {
            this.style.opacity = "1";
            // currentDraggedItem = null; // drop内で消してるのでここはコメントアウトか調整
        });
    }

    function checkClear() {
        if (submitBtn) submitBtn.disabled = false;
        if (answerInput) answerInput.value = "fixed";
    }
});