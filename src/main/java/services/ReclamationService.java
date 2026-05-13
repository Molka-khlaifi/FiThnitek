
package services;

import models.Reclamation;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements IService<Reclamation> {

    private Connection getConn() throws SQLException {
        return DBConnection.getConnection();
    }

    // ================= ADD =================
    @Override
    public void add(Reclamation r) {
        String sql = "INSERT INTO reclamation (id_user, objet, description, type_reclamation, niveau_urgence, etat, date_reclamation) VALUES (?,?,?,?,?,?,NOW())";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, r.getIdUser());
            ps.setString(2, r.getObjet());
            ps.setString(3, r.getDescription());
            ps.setString(4, r.getType());
            ps.setString(5, r.getUrgence());
            ps.setString(6, r.getEtat());
            ps.executeUpdate();
            System.out.println("Réclamation ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= UPDATE =================
    @Override
    public void update(Reclamation r) {
        String sql = "UPDATE reclamation SET objet=?, description=?, type_reclamation=?, niveau_urgence=?, etat=? WHERE id_reclamation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, r.getObjet());
            ps.setString(2, r.getDescription());
            ps.setString(3, r.getType());
            ps.setString(4, r.getUrgence());
            ps.setString(5, r.getEtat());
            ps.setInt(6, r.getId());
            ps.executeUpdate();
            System.out.println("Réclamation modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= DELETE =================
    @Override
    public void delete(Reclamation r) {
        // ✅ Supprimer d'abord les réponses liées (contrainte clé étrangère)
        String sqlReponses = "DELETE FROM reponsereclamation WHERE id_reclamation=?";
        String sqlReclamation = "DELETE FROM reclamation WHERE id_reclamation=?";

        try {
            // Étape 1 : supprimer les réponses liées
            try (PreparedStatement ps = getConn().prepareStatement(sqlReponses)) {
                ps.setInt(1, r.getId());
                ps.executeUpdate();
                System.out.println("Réponses liées supprimées !");
            }

            // Étape 2 : supprimer la réclamation
            try (PreparedStatement ps = getConn().prepareStatement(sqlReclamation)) {
                ps.setInt(1, r.getId());
                ps.executeUpdate();
                System.out.println("Réclamation supprimée !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur suppression : " + e.getMessage());
        }
    }

    // ================= GET ALL =================
    @Override
    public List<Reclamation> getAll() {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reclamation";

        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id_reclamation"));
                r.setIdUser(rs.getInt("id_user"));
                r.setObjet(rs.getString("objet"));
                r.setDescription(rs.getString("description"));
                r.setType(rs.getString("type_reclamation"));
                r.setUrgence(rs.getString("niveau_urgence"));
                r.setEtat(rs.getString("etat"));
                if (rs.getTimestamp("date_reclamation") != null)
                    r.setDate(rs.getTimestamp("date_reclamation").toLocalDateTime());
                list.add(r);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return list;
    }

    // ================= UPDATE ETAT =================
    public void updateEtat(int id, String etat) {
        String sql = "UPDATE reclamation SET etat=? WHERE id_reclamation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, etat);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
