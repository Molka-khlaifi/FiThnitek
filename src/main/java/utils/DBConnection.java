package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;

    private static final String URL      = "jdbc:mysql://localhost:3306/fi_thnitek";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    // Constructeur privé (Singleton)
    private DBConnection() {}

    // ✅ synchronized pour éviter les accès concurrents
    public static synchronized Connection getConnection() {

        try {
            // Reconnecter si la connexion est nulle ou fermée
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✔ Connexion réussie à la base de données");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver MySQL introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Erreur connexion DB : " + e.getMessage());
        }

        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // ✅ Remettre à null pour permettre une reconnexion
                System.out.println("✔ Connexion fermée");
            } catch (SQLException e) {
                System.out.println("❌ Erreur fermeture : " + e.getMessage());
            }
        }
    }
}