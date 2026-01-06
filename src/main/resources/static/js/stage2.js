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
                console.log("Selected suspect:", suspectId);
            }

            // 5. 画面表示更新
            if (selectionDisplay) {
                selectionDisplay.textContent = `You suspect: ${suspectId}`;
                selectionDisplay.style.color = "#e53935";
                selectionDisplay.style.fontWeight = "bold";
            }
        });
    });
});