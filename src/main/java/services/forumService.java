package services;

import models.commentaire;
import models.publication;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class forumService implements IService<publication> {

    Connection con;

    public forumService() {
        this.con = DBConnection.getInstance().getConn();
    }

    // ───────────────── ADD ─────────────────
    @Override
    public void add(publication p) {

        String sql = "INSERT INTO publication (titre, contenu, categorie, statut, date_creation, nb_vues, auteurId, trajetId, epingle) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, p.getTitre());
            ps.setString(2, p.getContenu());
            ps.setString(3, p.getCategorie());
            ps.setString(4, p.getStatut());
            ps.setDate(5, new java.sql.Date(p.getDate_creation().getTime()));
            ps.setInt(6, p.getNb_vues());
            ps.setInt(7, p.getAuteurId());

            ps.setBoolean(9, p.isEpingle());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ADD ERROR: " + e.getMessage());
        }
    }

    // ───────────────── UPDATE ─────────────────
    @Override
    public void update(publication p) {

        String sql = "UPDATE publication SET titre=?, contenu=?, categorie=?, statut=?, date_creation=?, nb_vues=?, auteurId=?, trajetId=?, epingle=? WHERE id=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, p.getTitre());
            ps.setString(2, p.getContenu());
            ps.setString(3, p.getCategorie());
            ps.setString(4, p.getStatut());
            ps.setDate(5, new java.sql.Date(p.getDate_creation().getTime()));
            ps.setInt(6, p.getNb_vues());
            ps.setInt(7, p.getAuteurId());
            ps.setInt(8, p.getTrajetId());
            ps.setBoolean(9, p.isEpingle());
            ps.setInt(10, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("UPDATE ERROR: " + e.getMessage());
        }
    }

    // ───────────────── DELETE ─────────────────
    @Override
    public void delete(publication p) {

        String sql = "DELETE FROM publication WHERE id=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, p.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("DELETE ERROR: " + e.getMessage());
        }
    }

    // ───────────────── GET ALL ─────────────────
    @Override
    public List<publication> getAll() {

        List<publication> list = new ArrayList<>();

        String sql = "SELECT * FROM publication";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                publication p = new publication(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteurId"),
                        rs.getInt("trajetId"),
                        rs.getBoolean("epingle")
                );

                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("GET ALL ERROR: " + e.getMessage());
        }

        return list;
    }

    // ───────────────── GET BY ID ─────────────────
    public publication getById(int id) {

        String sql = "SELECT * FROM publication WHERE id=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new publication(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteurId"),
                        rs.getInt("trajetId"),
                        rs.getBoolean("epingle")
                );
            }

        } catch (SQLException e) {
            System.out.println("GET BY ID ERROR: " + e.getMessage());
        }

        return null;
    }

    // ───────────────── BY CATEGORIE ─────────────────
    public List<publication> getByCategorie(String cat) {

        List<publication> list = new ArrayList<>();

        String sql = "SELECT * FROM publication WHERE categorie=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cat);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new publication(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteurId"),
                        rs.getInt("trajetId"),
                        rs.getBoolean("epingle")
                ));
            }

        } catch (SQLException e) {
            System.out.println("CATEGORY ERROR: " + e.getMessage());
        }

        return list;
    }

    // ───────────────── RECHERCHE ─────────────────
    public List<publication> rechercher(String keyword) {

        List<publication> list = new ArrayList<>();

        String sql = "SELECT * FROM publication WHERE titre LIKE ? OR contenu LIKE ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new publication(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteurId"),
                        rs.getInt("trajetId"),
                        rs.getBoolean("epingle")
                ));
            }

        } catch (SQLException e) {
            System.out.println("SEARCH ERROR: " + e.getMessage());
        }

        return list;
    }

    // ───────────────── TRI ─────────────────
    public List<publication> trierParDate() {
        return getSorted("date_creation DESC");
    }

    public List<publication> trierParVues() {
        return getSorted("nb_vues DESC");
    }

    private List<publication> getSorted(String orderBy) {

        List<publication> list = new ArrayList<>();

        String sql = "SELECT * FROM publication ORDER BY " + orderBy;

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                list.add(new publication(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteurId"),
                        rs.getInt("trajetId"),
                        rs.getBoolean("epingle")
                ));
            }

        } catch (SQLException e) {
            System.out.println("SORT ERROR: " + e.getMessage());
        }

        return list;
    }

    // ───────────────── EPINGLE ─────────────────
    public void toggleEpingle(int id) {

        String sql = "UPDATE publication SET epingle = NOT epingle WHERE id=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("TOGGLE EPINGLE ERROR: " + e.getMessage());
        }
    }

    // ─── Récupérer uniquement les publications épinglés ─────────────────
    public List<publication> getEpingles() {
        List<publication> list = new ArrayList<>();
        String sql = "SELECT * FROM publication WHERE epingle = TRUE ORDER BY date_creation DESC";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new publication(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteurId"),   // ✅ corrigé ici
                        rs.getInt("trajetId"),   // ✅ corrigé ici
                        rs.getBoolean("epingle")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getEpingles : " + e.getMessage());
        }
        return list;
    }

    public void incrementerVues(int publicationId) {
        String sql = "UPDATE publication SET nb_vues = nb_vues + 1 WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, publicationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur incrementerVues : " + e.getMessage());
        }
    }

    // ───────────────── commentaireS ─────────────────
    public void addcommentaire(commentaire c) {
        String sql = "INSERT INTO commentaire (contenu, date_comment, likes, publication_id, auteur_id) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, c.getContenu());
            ps.setTimestamp(2, Timestamp.valueOf(c.getDateCommentaire())); // ✅ CORRIGÉ
            ps.setInt(3, c.getLike());
            ps.setInt(4, c.getpublicationId());
            ps.setInt(5, c.getAuteurId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur addcommentaire : " + e.getMessage());
        }
    }

    public void updatecommentaire(commentaire c) {
        String sql = "UPDATE commentaire SET contenu=? WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, c.getContenu());
            ps.setInt(2, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur updatecommentaire : " + e.getMessage());
        }
    }

    public void deletecommentaire(int id) {
        String sql = "DELETE FROM commentaire WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur deletecommentaire : " + e.getMessage());
        }
    }

    public List<commentaire> getcommentairesBypublication(int id) {

        List<commentaire> list = new ArrayList<>();

        String sql = "SELECT * FROM commentaire WHERE publication_id=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new commentaire(
                        rs.getInt("id"),
                        rs.getString("contenu"),
                        rs.getTimestamp("date_comment").toLocalDateTime(),
                        rs.getInt("likes"),
                        rs.getInt("publication_id"),
                        rs.getInt("auteur_id")
                ));
            }

        } catch (SQLException e) {
            System.out.println("COMMENT ERROR: " + e.getMessage());
        }

        return list;
    }

    public void likercommentaire(int id) {

        String sql = "UPDATE commentaire SET likes = likes + 1 WHERE id=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("LIKE ERROR: " + e.getMessage());
        }
    }
}