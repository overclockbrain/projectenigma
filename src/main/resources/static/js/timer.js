/*
 * Simple Game Timer
 * サーバーから受け取った初期値を起点にカウントアップし、
 * フォーム送信時にその値を同期する。
 * @author R.Morioka
 * @version 1.1 (フォーム同期処理追加)
 * @since 1.0
 */
document.addEventListener('DOMContentLoaded', () => {
    // 1. タイマー表示要素
    const timerElement = document.getElementById('timer-val');

    if (timerElement) {
        let seconds = parseInt(timerElement.innerText, 10);

        // カウントアップ処理
        setInterval(() => {
            seconds++;
            timerElement.innerText = seconds;
        }, 1000);
    }

    // === ★ここから追加：フォーム送信時の同期処理 ===
    const answerForm = document.querySelector('.answer-form');

    if (answerForm) {
        answerForm.addEventListener('submit', function () {
            const currentTimerVal = document.getElementById('timer-val');
            const hiddenInput = document.getElementById('hidden-timer');

            // 画面のタイマー値を隠しフィールドにコピー
            if (currentTimerVal && hiddenInput) {
                hiddenInput.value = currentTimerVal.innerText;
            }
        });
    }
});