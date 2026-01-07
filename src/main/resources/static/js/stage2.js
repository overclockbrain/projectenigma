document.addEventListener("DOMContentLoaded", function () {
    const suspects = document.querySelectorAll(".suspect");
    const answerInput = document.getElementById("answer-input");
    const selectionDisplay = document.getElementById("selection-display");

    suspects.forEach(suspect => {
        suspect.addEventListener("click", function () {
            // 1. 他の選択を解除
            suspects.forEach(s => s.classList.remove("selected"));

            // 2. 自分を選択
            this.classList.add("selected");

            // 3. データを取得
            const suspectId = this.getAttribute("data-id");

            // 4. 隠しフォームに値をセット
            if (answerInput) {
                answerInput.value = suspectId;
            }

            // 5. 画面表示更新
            if (selectionDisplay) {
                // データ属性から翻訳パターンを取得 (th:data-pattern="#{stage2.you_suspect}")
                const pattern = selectionDisplay.getAttribute("data-pattern");

                if (pattern) {
                    selectionDisplay.textContent = pattern.replace("{0}", suspectId);
                } else {
                    selectionDisplay.textContent = suspectId;
                }

                // ★ここ修正！ 直接 style を触らず、CSSクラスを付与する
                selectionDisplay.classList.add("selection-active");
            }
        });
    });
});