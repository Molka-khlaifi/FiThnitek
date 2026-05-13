package services;

import models.Trajet;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTrajet {

    private final Connection conn;

    public ServiceTrajet() {
        this.conn = DBConnection.getInstance().getConn();
    }

    // ── Utilitaire : filtre statut ───────────────────────────────────────────
    private boolean hasStatutFilter(String statut) {
        return statut != null && !statut.isEmpty() && !"TOUS".equals(statut);
    }

    // ── CREATE ───────────────────────────────────────────────────────────────
    public void ajouter(Trajet t) throws SQLException {
        String sql = "INSERT INTO trajet "
                + "(depart, destination, date_depart, date_arrivee, prix, "
                + " places_total, places_disponibles, conducteur_nom, conducteur_tel, "
                + " matricule_vehicule, description, statut) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  t.getDepart());
            ps.setString(2,  t.getDestination());
            ps.setTimestamp(3, Timestamp.valueOf(t.getDateDepart()));
            ps.setTimestamp(4, Timestamp.valueOf(t.getDateArrivee()));
            ps.setDouble(5,  t.getPrix());
            ps.setInt(6,     t.getPlacesTotal());
            ps.setInt(7,     t.getPlacesDisponibles());
            ps.setString(8,  t.getConducteurNom());
            ps.setString(9,  t.getConducteurTel());
            ps.setString(10, t.getMatriculeVehicule());
            ps.setString(11, t.getDescription());
            ps.setString(12, t.getStatut() != null ? t.getStatut() : "ACTIF");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setId(rs.getInt(1));
            }
        }
    }

    // ── READ ALL ─────────────────────────────────────────────────────────────
    public List<Trajet> getAll() throws SQLException {
        List<Trajet> list = new ArrayList<>();
        String sql = "SELECT * FROM trajet ORDER BY date_depart DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── READ BY ID ───────────────────────────────────────────────────────────
    public Trajet getById(int id) throws SQLException {
        String sql = "SELECT * FROM trajet WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    // ── READ BY STATUT ───────────────────────────────────────────────────────
    @SuppressWarnings("unused")
    public List<Trajet> getByStatut(String statut) throws SQLException {
        List<Trajet> list = new ArrayList<>();
        String sql = "SELECT * FROM trajet WHERE statut = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── RECHERCHER ───────────────────────────────────────────────────────────
    @SuppressWarnings("unused")
    public List<Trajet> rechercher(String keyword, String statut) throws SQLException {
        List<Trajet> list = new ArrayList<>();
        String sql = "SELECT * FROM trajet "
                + "WHERE (depart LIKE ? OR destination LIKE ? OR conducteur_nom LIKE ?)";
        if (hasStatutFilter(statut)) sql += " AND statut = ?";
        sql += " ORDER BY date_depart DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            if (hasStatutFilter(statut)) ps.setString(4, statut);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    public void modifier(Trajet t) throws SQLException {
        String sql = "UPDATE trajet SET "
                + "depart=?, destination=?, date_depart=?, date_arrivee=?, prix=?, "
                + "places_total=?, places_disponibles=?, conducteur_nom=?, conducteur_tel=?, "
                + "matricule_vehicule=?, description=?, statut=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  t.getDepart());
            ps.setString(2,  t.getDestination());
            ps.setTimestamp(3, Timestamp.valueOf(t.getDateDepart()));
            ps.setTimestamp(4, Timestamp.valueOf(t.getDateArrivee()));
            ps.setDouble(5,  t.getPrix());
            ps.setInt(6,     t.getPlacesTotal());
            ps.setInt(7,     t.getPlacesDisponibles());
            ps.setString(8,  t.getConducteurNom());
            ps.setString(9,  t.getConducteurTel());
            ps.setString(10, t.getMatriculeVehicule());
            ps.setString(11, t.getDescription());
            ps.setString(12, t.getStatut());
            ps.setInt(13,    t.getId());
            ps.executeUpdate();
        }
    }

    // ── METTRE A JOUR PLACES ─────────────────────────────────────────────────
    public void mettreAJourPlaces(int trajetId, int delta) throws SQLException {
        String sql = "UPDATE trajet SET places_disponibles = places_disponibles + ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, trajetId);
            ps.executeUpdate();
        }
    }

    // ── CHANGER STATUT ───────────────────────────────────────────────────────
    @SuppressWarnings("unused")
    public void changerStatut(int id, String nouveauStatut) throws SQLException {
        String sql = "UPDATE trajet SET statut = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM trajet WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── VERIFIER DISPONIBILITE ───────────────────────────────────────────────
    @SuppressWarnings("unused")
    public boolean verifierDisponibilite(int trajetId, int placesDemandees) throws SQLException {
        Trajet t = getById(trajetId);
        if (t == null) return false;
        return t.getPlacesDisponibles() >= placesDemandees;
    }

    // ── MAPPER ───────────────────────────────────────────────────────────────
    private Trajet map(ResultSet rs) throws SQLException {
        Trajet t = new Trajet();
        t.setId(rs.getInt("id"));
        t.setDepart(rs.getString("depart"));
        t.setDestination(rs.getString("destination"));

        Timestamp tsDepart = rs.getTimestamp("date_depart");
        if (tsDepart != null) t.setDateDepart(tsDepart.toLocalDateTime());

        Timestamp tsArrivee = rs.getTimestamp("date_arrivee");
        if (tsArrivee != null) t.setDateArrivee(tsArrivee.toLocalDateTime());

        t.setPrix(rs.getDouble("prix"));
        t.setPlacesTotal(rs.getInt("places_total"));
        t.setPlacesDisponibles(rs.getInt("places_disponibles"));
        t.setConducteurNom(rs.getString("conducteur_nom"));
        t.setConducteurTel(rs.getString("conducteur_tel"));
        t.setMatriculeVehicule(rs.getString("matricule_vehicule"));   // ← corrigé
        t.setDescription(rs.getString("description"));
        t.setStatut(rs.getString("statut"));
        return t;
    }
}