package services;

import models.Revenue;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RevenueService implements IService<Revenue> {
    Connection conn;

    public RevenueService() {
        this.conn = DBConnection.getInstance().getConn();
        FinanceSchemaService.ensureSchema();
    }

    @Override
    public void add(Revenue r) {
        String SQL = "INSERT INTO revenue (transaction_id, user_id, user_type, montant, " +
                "date_revenue, type_revenue, mois, nb_passagers, statut, " +
                "montant_rembourse, date_remboursement, raison_remboursement, description) " +
                "VALUES (" + r.getTransactionId() + "," + r.getUserId() + ",'" +
                r.getUserType() + "'," + r.getMontant() + ",'" + r.getDateRevenue() + "','" +
                r.getTypeRevenue() + "','" + r.getMois() + "'," + r.getNbPassagers() + ",'" +
                r.getStatut() + "'," + r.getMontantRembourse() + "," +
                (r.getDateRemboursement() == null ? "NULL" : "'" + r.getDateRemboursement() + "'") + "," +
                (r.getRaisonRemboursement() == null ? "NULL" : "'" + r.getRaisonRemboursement() + "'") + ",'" +
                r.getDescription() + "')";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
            System.out.println("Revenue added successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Revenue r) {
        String SQL = "UPDATE revenue SET statut='" + r.getStatut() +
                "', montant=" + r.getMontant() +
                ", description='" + r.getDescription() +
                "' WHERE id=" + r.getId();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
            System.out.println("Revenue updated successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Revenue r) {
        String SQL = "DELETE FROM revenue WHERE id=" + r.getId();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
            System.out.println("Revenue deleted successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Revenue> getAll() {
        String SQL = "SELECT * FROM revenue";
        ArrayList<Revenue> list = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Revenue r = new Revenue();
                r.setId(rs.getInt("id"));
                r.setTransactionId(rs.getInt("transaction_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setUserType(rs.getString("user_type"));
                r.setMontant(rs.getDouble("montant"));
                r.setDateRevenue(rs.getString("date_revenue"));
                r.setTypeRevenue(rs.getString("type_revenue"));
                r.setMois(rs.getString("mois"));
                r.setNbPassagers(rs.getInt("nb_passagers"));
                r.setStatut(rs.getString("statut"));
                r.setMontantRembourse(rs.getDouble("montant_rembourse"));
                r.setDateRemboursement(rs.getString("date_remboursement"));
                r.setRaisonRemboursement(rs.getString("raison_remboursement"));
                r.setDescription(rs.getString("description"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    // ── Auto-generate monthly summary ──────────────────────
    public void generateMonthlySummary(int userId, String mois) {
        // Calculate total for the month
        String SQL = "SELECT SUM(montant) as total, SUM(nb_passagers) as total_passagers " +
                "FROM revenue WHERE user_id=" + userId +
                " AND mois='" + mois + "' AND type_revenue='per_trip'";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                double total = rs.getDouble("total");
                int totalPassagers = rs.getInt("total_passagers");

                // Check if monthly summary already exists
                String checkSQL = "SELECT id FROM revenue WHERE user_id=" + userId +
                        " AND mois='" + mois + "' AND type_revenue='monthly'";
                ResultSet check = stmt.executeQuery(checkSQL);

                if (check.next()) {
                    // Update existing summary
                    String updateSQL = "UPDATE revenue SET montant=" + total +
                            ", nb_passagers=" + totalPassagers +
                            ", date_revenue='" + LocalDate.now() +
                            "' WHERE user_id=" + userId +
                            " AND mois='" + mois + "' AND type_revenue='monthly'";
                    stmt.executeUpdate(updateSQL);
                    System.out.println("Monthly summary updated for " + mois);
                } else {
                    // Create new monthly summary
                    Revenue monthly = new Revenue(0, userId, "driver", total,
                            LocalDate.now().toString(), "monthly", mois,
                            totalPassagers, "Recapitulatif " + mois);
                    monthly.setStatut("confirmed");
                    add(monthly);
                    System.out.println("Monthly summary created for " + mois);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ── Process refund ─────────────────────────────────────
    public void refundRevenue(int transactionId, double montantRembourse, String raison) {
        String SQL = "UPDATE revenue SET statut='refunded'" +
                ", montant_rembourse=" + montantRembourse +
                ", date_remboursement='" + LocalDate.now() +
                "', raison_remboursement='" + raison +
                "' WHERE transaction_id=" + transactionId;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
            System.out.println("Revenue refunded successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ── Get all revenues for a specific driver ─────────────
    public List<Revenue> getByDriver(int userId) {
        String SQL = "SELECT * FROM revenue WHERE user_id=" + userId +
                " AND user_type='driver'";
        ArrayList<Revenue> list = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Revenue r = new Revenue();
                r.setId(rs.getInt("id"));
                r.setTransactionId(rs.getInt("transaction_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setUserType(rs.getString("user_type"));
                r.setMontant(rs.getDouble("montant"));
                r.setDateRevenue(rs.getString("date_revenue"));
                r.setTypeRevenue(rs.getString("type_revenue"));
                r.setMois(rs.getString("mois"));
                r.setNbPassagers(rs.getInt("nb_passagers"));
                r.setStatut(rs.getString("statut"));
                r.setMontantRembourse(rs.getDouble("montant_rembourse"));
                r.setDateRemboursement(rs.getString("date_remboursement"));
                r.setRaisonRemboursement(rs.getString("raison_remboursement"));
                r.setDescription(rs.getString("description"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
