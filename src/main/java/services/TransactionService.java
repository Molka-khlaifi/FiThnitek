package services;

import models.Revenue;
import models.Transaction;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionService implements IService<Transaction> {
    Connection conn;
    RevenueService revenueService;

    public TransactionService() {
        this.conn = DBConnection.getInstance().getConn();
        this.revenueService = new RevenueService();
    }

    @Override
    public void add(Transaction t) {
        String SQL = "INSERT INTO transaction (user_id, trip_id, montant, commission_platform, " +
                "montant_net, date_transaction, date_update, methode_paiement, payment_ref, statut) " +
                "VALUES (" + t.getUserId() + "," + t.getTripId() + "," + t.getMontant() + "," +
                t.getCommissionPlatform() + "," + t.getMontantNet() + ",'" +
                t.getDateTransaction() + "','" + t.getDateUpdate() + "','" +
                t.getMethodePaiement() + "','" + t.getPaymentRef() + "','" + t.getStatut() + "')";
        try {
            System.out.println("Executing SQL: " + SQL);  // ← add this
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                t.setId(rs.getInt(1));
            }
            System.out.println("Transaction inserted with id: " + t.getId());  // ← add this
        } catch (SQLException e) {
            e.printStackTrace();  // ← change from getMessage() to printStackTrace()
        }
    }

    @Override
    public void update(Transaction t) {
        String SQL = "UPDATE transaction SET " +
                "montant=" + t.getMontant() + ", " +
                "commission_platform=" + t.getCommissionPlatform() + ", " +
                "montant_net=" + t.getMontantNet() + ", " +
                "methode_paiement='" + t.getMethodePaiement() + "', " +
                "payment_ref='" + t.getPaymentRef() + "', " +
                "statut='" + t.getStatut() + "', " +
                "date_update='" + LocalDate.now() + "' " +
                "WHERE id=" + t.getId();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
            System.out.println("Transaction updated successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Transaction t) {
        String SQL = "DELETE FROM transaction WHERE id=" + t.getId();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
            System.out.println("Transaction deleted successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Transaction> getAll() {
        String SQL = "SELECT * FROM transaction";
        ArrayList<Transaction> list = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getInt("id"));
                t.setUserId(rs.getInt("user_id"));
                t.setTripId(rs.getInt("trip_id"));
                t.setMontant(rs.getDouble("montant"));
                t.setCommissionPlatform(rs.getDouble("commission_platform"));
                t.setMontantNet(rs.getDouble("montant_net"));
                t.setDateTransaction(rs.getString("date_transaction"));
                t.setDateUpdate(rs.getString("date_update"));
                t.setMethodePaiement(rs.getString("methode_paiement"));
                t.setPaymentRef(rs.getString("payment_ref"));
                t.setStatut(rs.getString("statut"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    // ── Called when driver clicks "Complete Trip" ──────────
    public void completeTrip(int userId, int tripId, double montant,
                             int nbPassagers, String methode) {
        // Step 1: create transaction
        double commission = montant * 0.10;
        Transaction t = new Transaction(userId, tripId, montant, 0.10,
                LocalDate.now().toString(), methode, "N/A");
        t.setStatut("completed");
        add(t);

        // Step 2: auto-generate driver revenue
        String mois = LocalDate.now().toString().substring(0, 7);
        Revenue driverRevenue = new Revenue(t.getId(), userId, "driver",
                t.getMontantNet(), LocalDate.now().toString(),
                "per_trip", mois, nbPassagers,
                "Trajet ID: " + tripId);
        driverRevenue.setStatut("confirmed");
        revenueService.add(driverRevenue);

        // Step 3: check if monthly summary needed
        revenueService.generateMonthlySummary(userId, mois);

        System.out.println("Trip completed! Transaction and revenue generated automatically.");
    }

    // ── Called when a refund is needed ─────────────────────
    public void refundTransaction(int transactionId, double montantRembourse, String raison) {
        // Step 1: update transaction status
        String SQL = "UPDATE transaction SET statut='refunded', date_update='" +
                LocalDate.now() + "' WHERE id=" + transactionId;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);

            // Step 2: update revenue entry
            revenueService.refundRevenue(transactionId, montantRembourse, raison);
            System.out.println("Refund processed successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
