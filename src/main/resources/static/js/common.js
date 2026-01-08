/**
 * Project Enigma: Common Logic
 * 全ページ共通で使う処理（アラート消去など）をここに書く。
 */
document.addEventListener("DOMContentLoaded", function () {

    // === アラートメッセージの自動消去 ===
    const alertBox = document.querySelector(".alert");
    if (alertBox) {
        // 3秒後にフェードアウト開始
        setTimeout(() => {
            alertBox.style.transition = "opacity 0.5s ease-out";
            alertBox.style.opacity = "0";

            // フェードアウト完了後にDOMから完全削除
            setTimeout(() => alertBox.remove(), 500);
        }, 3000);
    }

});