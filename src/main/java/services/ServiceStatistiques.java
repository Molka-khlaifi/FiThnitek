package services;

import util.DBConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceStatistiques {

    private final Connection conn = DBConnection.getInstance().getConn();

    public int totalTrajets() throws SQLException {
        return count("SELECT COUNT(*) FROM trajet");
    }

    public int totalReservations() throws SQLException {
        return count("SELECT COUNT(*) FROM reservation");
    }

    public double revenueTotal() throws SQLException {
        String sql = "SELECT COALESCE(SUM(montant_total),0) FROM reservation WHERE statut='CONFIRMEE'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    public double tauxOccupationMoyen() throws SQLException {
        String sql = "SELECT COALESCE(AVG((places_total - places_disponibles) * 100.0 / places_total),0) " +
                "FROM trajet WHERE places_total > 0";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    public Map<String, Integer> trajetsByStatut() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT statut, COUNT(*) AS cnt FROM trajet GROUP BY statut ORDER BY cnt DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("statut"), rs.getInt("cnt"));
        }
        return map;
    }

    public Map<String, Integer> topTrajets() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT CONCAT(t.depart, ' -> ', t.destination) AS route, COUNT(r.id) AS cnt " +
                "FROM trajet t LEFT JOIN reservation r ON t.id = r.trajet_id " +
                "GROUP BY t.id ORDER BY cnt DESC LIMIT 6";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("route"), rs.getInt("cnt"));
        }
        return map;
    }

    public Map<String, Double> revenueParMois() throws SQLException {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(date_reservation,'%Y-%m') AS mois, SUM(montant_total) AS total " +
                "FROM reservation WHERE statut='CONFIRMEE' " +
                "AND date_reservation >= DATE_SUB(NOW(), INTERVAL 12 MONTH) " +
                "GROUP BY mois ORDER BY mois";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("mois"), rs.getDouble("total"));
        }
        return map;
    }

    public Map<String, Integer> reservationsByStatut() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT statut, COUNT(*) AS cnt FROM reservation GROUP BY statut";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("statut"), rs.getInt("cnt"));
        }
        return map;
    }

    public int placesVendues() throws SQLException {
        String sql = "SELECT COALESCE(SUM(nombre_places),0) FROM reservation WHERE statut != 'ANNULEE'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int count(String sql) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}