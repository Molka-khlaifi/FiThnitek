package com.example.couvoiturage.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "couvoiturage_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // On essaie d'abord de se connecter au serveur sans spécifier la DB
            Connection serverConn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
            serverConn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            serverConn.close();

            // Maintenant on se connecte à la DB spécifique
            Connection conn = DriverManager.getConnection(SERVER_URL + DB_NAME, USER, PASSWORD);
            initializeTables(conn);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé", e);
        }
    }

    private static void initializeTables(Connection conn) throws SQLException {
        String createUtilisateur = "CREATE TABLE IF NOT EXISTS utilisateur (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "nom VARCHAR(50) NOT NULL," +
                "prenom VARCHAR(50) NOT NULL," +
                "cin VARCHAR(20) UNIQUE NOT NULL," +
                "telephone VARCHAR(20)," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "role ENUM('PASSAGER', 'CONDUCTEUR', 'ADMIN') NOT NULL" +
                ")";

        String createConducteur = "CREATE TABLE IF NOT EXISTS conducteur_info (" +
                "utilisateur_id INT PRIMARY KEY," +
                "carte_gris_path VARCHAR(255)," +
                "permis_path VARCHAR(255)," +
                "montant_gagne DOUBLE DEFAULT 0.0," +
                "FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE" +
                ")";

        String createPassager = "CREATE TABLE IF NOT EXISTS passager_info (" +
                "utilisateur_id INT PRIMARY KEY," +
                "FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE" +
                ")";

        String createLogs = "CREATE TABLE IF NOT EXISTS user_logs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "utilisateur_email VARCHAR(100)," +
                "action VARCHAR(255) NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(createUtilisateur);
            stmt.execute(createConducteur);
            stmt.execute(createPassager);
            stmt.execute(createLogs);
            
            // Cleanup and migration
            try {
                // Remove unused columns if they exist
                stmt.execute("ALTER TABLE conducteur_info DROP COLUMN IF EXISTS marque_voiture");
                stmt.execute("ALTER TABLE conducteur_info DROP COLUMN IF EXISTS modele_voiture");
                stmt.execute("ALTER TABLE conducteur_info DROP COLUMN IF EXISTS immatriculation");
                
                // Add new columns
                stmt.execute("ALTER TABLE conducteur_info ADD COLUMN IF NOT EXISTS carte_gris_path VARCHAR(255)");
                stmt.execute("ALTER TABLE conducteur_info ADD COLUMN IF NOT EXISTS permis_path VARCHAR(255)");
                
                // Add reset columns to utilisateur
                stmt.execute("ALTER TABLE utilisateur ADD COLUMN IF NOT EXISTS reset_code VARCHAR(10)");
                stmt.execute("ALTER TABLE utilisateur ADD COLUMN IF NOT EXISTS reset_expiry DATETIME");
            } catch (SQLException e) {
                // Silently ignore errors for DBs that don't support IF EXISTS in ALTER
            }
        }
    }
}
