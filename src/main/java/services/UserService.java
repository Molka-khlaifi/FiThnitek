package services;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    private static final String[] USER_TABLE_NAMES = {"user", "users", "utilisateur", "utilisateurs"};

    public String getEmailByUserId(int userId) {
        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            return "";
        }

        for (String tableName : USER_TABLE_NAMES) {
            String sql = "SELECT email FROM " + tableName + " WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String email = resultSet.getString("email");
                        return email == null ? "" : email.trim();
                    }
                }
            } catch (SQLException e) {
                // Try the next known user table name from merged modules.
            }
        }

        System.out.println("[UserService] Could not resolve email for userId=" + userId);

        return "";
    }
}
