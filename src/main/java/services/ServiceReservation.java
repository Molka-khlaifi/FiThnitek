package services;

import models.Reservation;
import models.Trajet;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation {

    private final Connection conn = DBConnection.getInstance().getConn();
    private final ServiceTrajet serviceTrajet = new ServiceTrajet();

    // ── CREATE ───────────────────────────────────────────────────────────────
    public void ajouter(Reservation r) throws SQLException {
        Trajet trajet = serviceTrajet.getById(r.getTrajetId());
        if (trajet == null)
            throw new SQLException("Trajet introuvable.");
        if (trajet.getPlacesDisponibles() < r.getNombrePlaces())
            throw new SQLException("Seulement " + trajet.getPlacesDisponibles() + " place(s) disponible(s).");

        String sql = "INSERT INTO reservation " +
                "(trajet_id, passager_nom, passager_email, passager_tel, " +
                " nombre_places, date_reservation, statut, commentaire, montant_total) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getTrajetId());
            ps.setString(2, r.getPassagerNom());
            ps.setString(3, r.getPassagerEmail());
            ps.setString(4, r.getPassagerTel());
            ps.setInt(5, r.getNombrePlaces());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, r.getStatut() != null ? r.getStatut() : "EN_ATTENTE");
            ps.setString(8, r.getCommentaire());
            ps.setDouble(9, r.getMontantTotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        }
        serviceTrajet.mettreAJourPlaces(r.getTrajetId(), -r.getNombrePlaces());
    }

    // ── READ ALL ─────────────────────────────────────────────────────────────
    public List<Reservation> getAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(t.depart, ' -> ', t.destination) AS trajet_info " +
                "FROM reservation r " +
                "JOIN trajet t ON r.trajet_id = t.id " +
                "ORDER BY r.date_reservation DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reservation res = map(rs);
                res.setTrajetInfo(rs.getString("trajet_info"));
                list.add(res);
            }
        }
        return list;
    }

    // ── READ BY TRAJET ────────────────────────────────────────────────────────
    public List<Reservation> getByTrajet(int trajetId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(t.depart, ' -> ', t.destination) AS trajet_info " +
                "FROM reservation r " +
                "JOIN trajet t ON r.trajet_id = t.id " +
                "WHERE r.trajet_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trajetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation res = map(rs);
                    res.setTrajetInfo(rs.getString("trajet_info"));
                    list.add(res);
                }
            }
        }
        return list;
    }

    // ── READ BY ID ────────────────────────────────────────────────────────────
    public Reservation getById(int id) throws SQLException {
        String sql = "SELECT r.*, CONCAT(t.depart, ' -> ', t.destination) AS trajet_info " +
                "FROM reservation r " +
                "JOIN trajet t ON r.trajet_id = t.id " +
                "WHERE r.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Reservation res = map(rs);
                    res.setTrajetInfo(rs.getString("trajet_info"));
                    return res;
                }
            }
        }
        return null;
    }

    // ── CHANGER STATUT ────────────────────────────────────────────────────────
    public void changerStatut(int id, String nouveauStatut) throws SQLException {
        if ("ANNULEE".equals(nouveauStatut)) {
            Reservation r = getById(id);
            if (r != null && !"ANNULEE".equals(r.getStatut())) {
                serviceTrajet.mettreAJourPlaces(r.getTrajetId(), r.getNombrePlaces());
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservation SET statut = ? WHERE id = ?")) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public void supprimer(int id) throws SQLException {
        Reservation r = getById(id);
        if (r != null && !"ANNULEE".equals(r.getStatut())) {
            serviceTrajet.mettreAJourPlaces(r.getTrajetId(), r.getNombrePlaces());
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM reservation WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────
    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setTrajetId(rs.getInt("trajet_id"));
        r.setPassagerNom(rs.getString("passager_nom"));
        r.setPassagerEmail(rs.getString("passager_email"));
        r.setPassagerTel(rs.getString("passager_tel"));
        r.setNombrePlaces(rs.getInt("nombre_places"));
        r.setDateReservation(rs.getTimestamp("date_reservation").toLocalDateTime());
        r.setStatut(rs.getString("statut"));
        r.setCommentaire(rs.getString("commentaire"));
        r.setMontantTotal(rs.getDouble("montant_total"));
        return r;
    }
}