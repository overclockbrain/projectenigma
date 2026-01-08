document.addEventListener("DOMContentLoaded", function () {
    const suspects = document.querySelectorAll(".suspect");
    const answerInput = document.getElementById("answer-input");
    const selectionDisplay = document.getElementById("selection-display");

    suspects.forEach(suspect => {
        suspect.addEventListener("click", function () {
            // 1. 全員の顔を「元の顔」に戻す
            suspects.forEach(s => {
                s.classList.remove("selected");
                const icon = s.querySelector(".suspect-icon");
                if (icon && icon.dataset.originalFace) {
                    icon.textContent = icon.dataset.originalFace;
                }
            });

            // 2. 自分を選択状態にする
            this.classList.add("selected");

            // 3. 自分の顔を「狼」に変える！
            const myIcon = this.querySelector(".suspect-icon");
            if (myIcon) {
                myIcon.textContent = "🐺";
            }

            // 4. IDを取得してフォームにセット
            const suspectId = this.getAttribute("data-id");
            if (answerInput) {
                answerInput.value = suspectId;
            }

            // 5. 画面下の表示更新
            if (selectionDisplay) {
                const pattern = selectionDisplay.getAttribute("data-pattern");
                if (pattern) {
                    selectionDisplay.textContent = pattern.replace("{0}", suspectId);
                } else {
                    selectionDisplay.textContent = suspectId;
                }
                selectionDisplay.classList.add("selection-active");
            }
        });
    });
});