public class GameThread extends Thread {

    private GamePanel panel;
    private boolean running = true;
    private boolean pausedFrameDrawn = false;

    public GameThread(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        while (running) {

            // ========================
            //       PAUSED MODE
            // ========================
            if (panel.isPaused()) {

                // gambar PAUSED hanya 1 kali
                if (!pausedFrameDrawn) {
                    panel.repaint();
                    pausedFrameDrawn = true;
                }

                try { Thread.sleep(16); } catch (Exception e) {}
                continue; // freeze total
            }

            // ========================
            //  NORMAL GAME RUNNING
            // ========================
            pausedFrameDrawn = false; // reset ketika unpause

            panel.updateGame();
            panel.repaint();

            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }

    public void stopGame() {
        running = false;
    }
}
