package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private String url = "jdbc:mysql://localhost:3306/FiThnitek_DB";
    private String user = "root";
    private String password = "";
    private Connection conn;
    private static DBConnection instance;

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConn() {
        try {
            // Si la connexion est fermée ou nulle → on la recrée
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connexion rétablie");
            }
        } catch (SQLException e) {
            System.out.println("Erreur reconnexion : " + e.getMessage());
        }
        return conn;
    }

    private DBConnection() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion établie");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}