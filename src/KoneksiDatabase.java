import java.sql.*;

public class KoneksiDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/meteor_starfall";
    private static final String USER = "root"; 
    private static final String PASS = "";     

    // Mendapatkan koneksi ke MySQL
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Koneksi ke database gagal!");
            e.printStackTrace();
            return null;
        }
    }

    // =======================
    // REGISTER USER BARU
    // =======================
    public static boolean registerUser(String username, String password) {
        String checkQuery = "SELECT * FROM users WHERE username=?";
        String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement check = conn.prepareStatement(checkQuery);
             PreparedStatement insert = conn.prepareStatement(insertQuery)) {

            // Cek apakah username sudah ada
            check.setString(1, username);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("Username sudah digunakan!");
                return false;
            }

            // Insert user baru
            insert.setString(1, username);
            insert.setString(2, password);
            insert.executeUpdate();
            System.out.println("Registrasi berhasil!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =======================
    // LOGIN USER
    // =======================
    public static boolean loginUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username=? AND password=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Login berhasil! Selamat datang, " + username);
                return true;
            } else {
                System.out.println("Login gagal! Username atau password salah.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =======================
    // SIMPAN SKOR PLAYER
    // =======================
    public static void saveScore(String username, int score) {
        try (Connection conn = getConnection()) {

            // Cari user_id berdasarkan username
            String userQuery = "SELECT user_id FROM users WHERE username=?";
            PreparedStatement ps1 = conn.prepareStatement(userQuery);
            ps1.setString(1, username);
            ResultSet rs = ps1.executeQuery();

            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }

            // Jika user ditemukan, simpan skor
            if (userId != -1) {
                String insertScore = 
                    "INSERT INTO scores (user_id, score, date_played) VALUES (?, ?, NOW())";
                PreparedStatement ps2 = conn.prepareStatement(insertScore);
                ps2.setInt(1, userId);
                ps2.setInt(2, score);
                ps2.executeUpdate();

                System.out.println("Skor berhasil disimpan untuk user: " + username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =======================
    // AMBIL 10 SKOR TERATAS
    // =======================
    public static ResultSet getTop5Scores() {
        try {
            Connection conn = getConnection();
            String query =
                "SELECT u.username, s.score " +
                "FROM scores s JOIN users u ON s.user_id = u.user_id " +
                "ORDER BY s.score DESC LIMIT 5";

            PreparedStatement ps = conn.prepareStatement(query);
            return ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // =======================
    // Test koneksi
    // =======================
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Koneksi ke database berhasil!");
        } else {
            System.out.println("Gagal konek ke database.");
        }
    }
}
