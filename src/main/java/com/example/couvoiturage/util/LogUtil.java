package com.example.couvoiturage.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogUtil {
    public static void log(String email, String action) {
        String query = "INSERT INTO user_logs (utilisateur_email, action) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, action);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
