package services;

import models.ReponseReclamation;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseReclamationService implements IService<ReponseReclamation> {

    // ✅ Plus de connexion stockée en champ — récupérée dans chaque méthode
    private Connection getConn() throws SQLException {
        return DBConnection.getConnection();
    }

    @Override
    public void add(ReponseReclamation r) {
        String sql = "INSERT INTO reponsereclamation (id_reclamation, id_admin, message_reponse, date_reponse) VALUES (?,?,?,NOW())";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, r.getIdReclamation());
            ps.setInt(2, r.getIdAdmin());
            ps.setString(3, r.getMessage());
            ps.executeUpdate();
            System.out.println("Réponse ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(ReponseReclamation r) {
        String sql = "UPDATE reponsereclamation SET message_reponse=? WHERE id_reponse=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, r.getMessage());
            ps.setInt(2, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(ReponseReclamation r) {
        String sql = "DELETE FROM reponsereclamation WHERE id_reponse=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<ReponseReclamation> getAll() {
        List<ReponseReclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reponsereclamation";

        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ReponseReclamation r = new ReponseReclamation();
                r.setId(rs.getInt("id_reponse"));
                r.setIdReclamation(rs.getInt("id_reclamation"));
                r.setIdAdmin(rs.getInt("id_admin"));
                r.setMessage(rs.getString("message_reponse"));
                // ✅ Lecture de la date corrigée
                if (rs.getTimestamp("date_reponse") != null)
                    r.setDate(rs.getTimestamp("date_reponse").toLocalDateTime());
                list.add(r);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return list;
    }

    // ================= GET BY RECLAMATION =================
    public List<ReponseReclamation> getByReclamation(int idRec) {
        List<ReponseReclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reponsereclamation WHERE id_reclamation=?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idRec);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReponseReclamation r = new ReponseReclamation();
                    r.setId(rs.getInt("id_reponse"));
                    r.setIdReclamation(rs.getInt("id_reclamation"));
                    r.setIdAdmin(rs.getInt("id_admin"));
                    r.setMessage(rs.getString("message_reponse"));
                    // ✅ Lecture de la date
                    if (rs.getTimestamp("date_reponse") != null)
                        r.setDate(rs.getTimestamp("date_reponse").toLocalDateTime());
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return list;
    }
}
