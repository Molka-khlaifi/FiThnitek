package services;

import models.Revenue;
import util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RevenueService implements IService<Revenue> {
    Connection conn;

    public RevenueService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Revenue r) {
        String SQL = "INSERT INTO revenue (user_id, user_type, trip_id, montant, date_revenue, type_revenue, description) VALUES (" +
                r.getUserId() + ",'" + r.getUserType() + "'," + r.getTripId() + "," +
                r.getMontant() + ",'" + r.getDateRevenue() + "','" + r.getTypeRevenue() +
                "','" + r.getDescription() + "')";
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
        String SQL = "UPDATE revenue SET user_id=" + r.getUserId() + ", user_type='" +
                r.getUserType() + "', trip_id=" + r.getTripId() + ", montant=" +
                r.getMontant() + ", date_revenue='" + r.getDateRevenue() + "', type_revenue='" +
                r.getTypeRevenue() + "', description='" + r.getDescription() +
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
        ArrayList<Revenue> revenues = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Revenue r = new Revenue();
                r.setId(rs.getInt("id"));
                r.setUserId(rs.getInt("user_id"));
                r.setUserType(rs.getString("user_type"));
                r.setTripId(rs.getInt("trip_id"));
                r.setMontant(rs.getDouble("montant"));
                r.setDateRevenue(rs.getString("date_revenue"));
                r.setTypeRevenue(rs.getString("type_revenue"));
                r.setDescription(rs.getString("description"));
                revenues.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return revenues;
    }
}