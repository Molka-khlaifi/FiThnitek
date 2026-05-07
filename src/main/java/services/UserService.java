package services;

import util.DBConnection;

import java.sql.*;

/**
 * Looks up a user's email address from the database.
 * Tries common table names so it works when the colleague's users table is merged.
 * Falls back to an empty string gracefully if the table doesn't exist yet.
 */
public class UserService {

    private final Connection conn;

    public UserService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    /**
     * Attempts to fetch the email for the given userId.
     * Tries table names: user, users, utilisateur, utilisateurs.
     * Returns "" if the table doesn't exist yet or the user has no email.
     */
    public String getEmailByUserId(int userId) {
        String[] tableNames = {"user", "users", "utilisateur", "utilisateurs"};
        for (String table : tableNames) {
            try {
                String sql = "SELECT email FROM " + table + " WHERE id = " + userId;
                Statement stmt = conn.createStatement();
                ResultSet rs   = stmt.executeQuery(sql);
                if (rs.next()) {
                    String email = rs.getString("email");
                    return (email != null) ? email.trim() : "";
                }
            } catch (SQLException e) {
                // Table or column doesn't exist yet — try the next candidate
            }
        }
        System.out.println("[UserService] Could not resolve email for userId=" + userId
                + " — users table not yet available.");
        return "";
    }
}
