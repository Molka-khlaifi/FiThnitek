package services;

import models.Transaction;
import util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionService implements IService<Transaction> {
    Connection conn;

    public TransactionService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Transaction t) {
        String SQL = "INSERT INTO transaction (revenue_id, montant, date_transaction, methode_paiement, statut) VALUES (" +
                t.getRevenueId() + "," + t.getMontant() + ",'" + t.getDateTransaction() +
                "','" + t.getMethodePaiement() + "','" + t.getStatut() + "')";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
            System.out.println("Transaction added successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Transaction t) {
        String SQL = "UPDATE transaction SET revenue_id=" + t.getRevenueId() + ", montant=" +
                t.getMontant() + ", date_transaction='" + t.getDateTransaction() +
                "', methode_paiement='" + t.getMethodePaiement() + "', statut='" +
                t.getStatut() + "' WHERE id=" + t.getId();
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
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getInt("id"));
                t.setRevenueId(rs.getInt("revenue_id"));
                t.setMontant(rs.getDouble("montant"));
                t.setDateTransaction(rs.getString("date_transaction"));
                t.setMethodePaiement(rs.getString("methode_paiement"));
                t.setStatut(rs.getString("statut"));
                transactions.add(t);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transactions;
    }
}