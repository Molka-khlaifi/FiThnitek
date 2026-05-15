package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/fithnitek";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static DBConnection instance;
    private static Connection connection;

    private DBConnection() {
        connection = creerConnexion();
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public synchronized Connection getConn() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = creerConnexion();
            }
        } catch (SQLException e) {
            System.out.println("Erreur vérification connexion DB : " + e.getMessage());
        }

        return connection;
    }

    public static synchronized Connection getConnection() {
        return getInstance().getConn();
    }

    private static Connection creerConnexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion réussie à la base de données fithnitek");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erreur connexion DB : " + e.getMessage());
        }

        return null;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Connexion fermée");
            } catch (SQLException e) {
                System.out.println("Erreur fermeture connexion DB : " + e.getMessage());
            }
        }
    }
}