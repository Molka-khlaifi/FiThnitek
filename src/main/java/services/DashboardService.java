package services;

import util.DBConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides aggregated data for the Dashboard charts.
 */
public class DashboardService {

    private final Connection conn;

    public DashboardService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    // ── KPI helpers ──────────────────────────────────────────────────────────

    public int getTotalTransactions() {
        return queryInt("SELECT COUNT(*) FROM transaction");
    }

    /** Sum of per-trip revenue (excluding monthly summary rows). */
    public double getTotalRevenue() {
        return queryDouble("SELECT COALESCE(SUM(montant), 0) FROM revenue WHERE type_revenue='per_trip'");
    }

    public double getTotalRefunded() {
        return queryDouble("SELECT COALESCE(SUM(montant_rembourse), 0) FROM revenue");
    }

    public int getDistinctDriverCount() {
        return queryInt("SELECT COUNT(DISTINCT user_id) FROM revenue WHERE user_type='driver'");
    }

    // ── Pie charts ───────────────────────────────────────────────────────────

    /** Returns { "completed" -> 12, "pending" -> 3, ... } */
    public Map<String, Integer> getTransactionStatusCounts() {
        return queryGroupCount(
                "SELECT statut, COUNT(*) FROM transaction GROUP BY statut",
                "statut");
    }

    /** Returns { "cash" -> 10, "flouci" -> 5 } */
    public Map<String, Integer> getPaymentMethodCounts() {
        return queryGroupCount(
                "SELECT methode_paiement, COUNT(*) FROM transaction GROUP BY methode_paiement",
                "methode_paiement");
    }

    // ── Bar / Line charts ────────────────────────────────────────────────────

    /** Returns { "2024-01" -> 135.0, "2024-02" -> 200.5, ... } ordered by month */
    public Map<String, Double> getRevenueByMonth() {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT mois, COALESCE(SUM(montant), 0) as total " +
                     "FROM revenue WHERE type_revenue='per_trip' " +
                     "GROUP BY mois ORDER BY mois";
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("mois"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.out.println("[DashboardService] getRevenueByMonth: " + e.getMessage());
        }
        return result;
    }

    /** Returns { "2024-01" -> 8, "2024-02" -> 15, ... } ordered by month */
    public Map<String, Integer> getPassengersByMonth() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT mois, COALESCE(SUM(nb_passagers), 0) as total " +
                     "FROM revenue WHERE type_revenue='per_trip' " +
                     "GROUP BY mois ORDER BY mois";
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("mois"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.out.println("[DashboardService] getPassengersByMonth: " + e.getMessage());
        }
        return result;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private int queryInt(String sql) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("[DashboardService] queryInt: " + e.getMessage());
        }
        return 0;
    }

    private double queryDouble(String sql) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.out.println("[DashboardService] queryDouble: " + e.getMessage());
        }
        return 0.0;
    }

    private Map<String, Integer> queryGroupCount(String sql, String keyCol) {
        Map<String, Integer> result = new LinkedHashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString(keyCol), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.out.println("[DashboardService] queryGroupCount: " + e.getMessage());
        }
        return result;
    }
}
