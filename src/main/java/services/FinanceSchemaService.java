package services;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class FinanceSchemaService {

    private static boolean initialized = false;

    private FinanceSchemaService() {
    }

    public static synchronized void ensureSchema() {
        if (initialized) {
            return;
        }
        initialized = true;

        Connection conn = DBConnection.getInstance().getConn();
        if (conn == null) {
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            ensureTransactionColumns(stmt);
            ensureRevenueColumns(stmt);
        } catch (SQLException e) {
            System.out.println("[FinanceSchemaService] " + e.getMessage());
        }
    }

    private static void ensureTransactionColumns(Statement stmt) throws SQLException {
        addColumnIfMissing(stmt, "transaction", "user_id", "INT DEFAULT 0");
        addColumnIfMissing(stmt, "transaction", "trip_id", "INT DEFAULT 0");
        addColumnIfMissing(stmt, "transaction", "montant", "DOUBLE DEFAULT 0");
        addColumnIfMissing(stmt, "transaction", "commission_platform", "DOUBLE DEFAULT 0.10");
        addColumnIfMissing(stmt, "transaction", "montant_net", "DOUBLE DEFAULT 0");
        addColumnIfMissing(stmt, "transaction", "date_transaction", "VARCHAR(30)");
        addColumnIfMissing(stmt, "transaction", "date_update", "VARCHAR(30)");
        addColumnIfMissing(stmt, "transaction", "methode_paiement", "VARCHAR(50) DEFAULT 'cash'");
        addColumnIfMissing(stmt, "transaction", "payment_ref", "VARCHAR(255) DEFAULT 'N/A'");
        addColumnIfMissing(stmt, "transaction", "statut", "VARCHAR(50) DEFAULT 'pending'");
    }

    private static void ensureRevenueColumns(Statement stmt) throws SQLException {
        addColumnIfMissing(stmt, "revenue", "transaction_id", "INT DEFAULT 0");
        addColumnIfMissing(stmt, "revenue", "user_id", "INT DEFAULT 0");
        addColumnIfMissing(stmt, "revenue", "user_type", "VARCHAR(50) DEFAULT 'driver'");
        addColumnIfMissing(stmt, "revenue", "montant", "DOUBLE DEFAULT 0");
        addColumnIfMissing(stmt, "revenue", "date_revenue", "VARCHAR(30)");
        addColumnIfMissing(stmt, "revenue", "type_revenue", "VARCHAR(50) DEFAULT 'per_trip'");
        addColumnIfMissing(stmt, "revenue", "mois", "VARCHAR(7)");
        addColumnIfMissing(stmt, "revenue", "nb_passagers", "INT DEFAULT 0");
        addColumnIfMissing(stmt, "revenue", "statut", "VARCHAR(50) DEFAULT 'pending'");
        addColumnIfMissing(stmt, "revenue", "montant_rembourse", "DOUBLE DEFAULT 0");
        addColumnIfMissing(stmt, "revenue", "date_remboursement", "VARCHAR(30)");
        addColumnIfMissing(stmt, "revenue", "raison_remboursement", "VARCHAR(255)");
        addColumnIfMissing(stmt, "revenue", "description", "VARCHAR(255)");
    }

    private static void addColumnIfMissing(Statement stmt, String table, String column, String definition) {
        try {
            if (!columnExists(stmt, table, column)) {
                stmt.executeUpdate("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
            }
        } catch (SQLException e) {
            System.out.println("[FinanceSchemaService] Could not add " + table + "." + column + ": " + e.getMessage());
        }
    }

    private static boolean columnExists(Statement stmt, String table, String column) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM `" + table + "` LIKE '" + column + "'")) {
            return rs.next();
        }
    }
}
