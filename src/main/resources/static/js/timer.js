/*
 * Simple Game Timer
 * サーバーから受け取った初期値を起点にカウントアップする
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
document.addEventListener('DOMContentLoaded', () => {
    // 時間を表示している要素を取得
    const timerElement = document.getElementById('timer-val');

    if (timerElement) {
        // 現在の表示されている秒数を取得
        let seconds = parseInt(timerElement.innerText, 10);

        // 1秒(1000ms)ごとに実行
        setInterval(() => {
            seconds++;
            timerElement.innerText = seconds;
        }, 1000);
    }
});