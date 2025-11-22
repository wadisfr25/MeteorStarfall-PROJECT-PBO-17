import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private MainMenuPanel mainMenuPanel;
    private String currentUser;

    public Main() {
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setUndecorated(true); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Tambahkan panel login
        loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "login");

        // Nanti kita tambahkan main menu di langkah berikut
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    public void showMainMenu(String username) {
        mainMenuPanel = new MainMenuPanel(this, username);
        mainPanel.add(mainMenuPanel, "mainMenu");
        cardLayout.show(mainPanel, "mainMenu");
    }

    public void showGame(String username) {
        GamePanel gamePanel = new GamePanel(this, username);
        mainPanel.add(gamePanel, "game");
        cardLayout.show(mainPanel, "game");
    }

    public void showLeaderboard(String username) {
        LeaderboardPanel leaderboardPanel = new LeaderboardPanel(this, username);
        mainPanel.add(leaderboardPanel, "leaderboard");
        cardLayout.show(mainPanel, "leaderboard");
    }
    public void setCurrentUser(String username) {
        this.currentUser = username;
    }
    public String getCurrentUser() {
        return currentUser;
    }

    

    public void showLogin() {
        cardLayout.show(mainPanel, "login");
    }
public void showGameOver(String username, int score, int timePlayed, BufferedImage lastFrame) {
    GameOverPanel panel = new GameOverPanel(this, username, score, timePlayed, lastFrame);
    mainPanel.add(panel, "gameOver");
    cardLayout.show(mainPanel, "gameOver");
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }

}
