package services;

import models.commentaire;
import models.forum;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class forumService implements IService<forum> {
    Connection con;

    public forumService() {
        this.con = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(forum forum) {
        String SQL = "Insert Into forum (titre, contenu, categorie, date_creation, statut, nb_vues, auteurId, trajetId) values (" + forum.getTitre() + "','" + forum.getCategorie() + "','" + forum.getContenu() + "','" + forum.getStatut() + "','" + forum.getDate_creation() + "','" + forum.getNb_vues()+","+forum.getAuteurId() + "," + forum.getTrajetId() + ")";
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(forum forum) {
        String SQL = "Update forum set( titre, contenu, categorie, statut, nb_vues)" + forum.getTitre() + "," + forum.getContenu() + "," + forum.getCategorie() + "," + forum.getStatut() + "," + forum.getNb_vues() +","+ "WHERE id='" +forum.getId()+",";
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(forum forum) {
        String SQL = "Delete from forum where id=" + forum.getId();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<forum> getAll() {
        String req = "select * from forum";
        ArrayList<forum> forum = new ArrayList<>();
        Statement stm;
        try {
            stm = this.con.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                forum f = new forum(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteur_id"),
                        rs.getInt("trajet_id")
                );
                forum.add(f);
            }
        } catch (SQLException e) {
            System.out.println("erreur get all forum : "+ e.getMessage());
        }
        return forum;
    }

    // get by id
    public forum getById(int id) {
        String sql = "select * from forum where id= ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new forum(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteur_id"),
                        rs.getInt("trajet_id")
                );
            }
        } catch (SQLException e) {
            System.out.println("erreur get by id forum  : "+ e.getMessage());
        }
        return null;

    }

    //get by categorie
    public List<forum> getByCategorie(String categorie) {
        List<forum> forums = new ArrayList<>();
        String sql = "select * from forum where categorie= ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,categorie);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                forum f = new forum(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getString("categorie"),
                        rs.getString("statut"),
                        rs.getDate("date_creation"),
                        rs.getInt("nb_vues"),
                        rs.getInt("auteur_id"),
                        rs.getInt("trajet_id")
                );
                forums.add(f);
            }
        } catch (SQLException e) {
            System.out.println("erreur get by id forum  : "+ e.getMessage());
        }
        return forums;
    }

    //incrementer nb vues
    public void incrementerVues( int forumId) {
        String sql = "UPDATE forum SET nb_vues=nb_vues+1 WHERE id= ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,forumId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("erreur incrementer vues : "+ e.getMessage());
        }
    }

    //ajout commentaire
    public void addCommentaire(commentaire c ) {
        String sql = "INSERT INTO commentaire (contenu, date_comment, likes, forum_id, auteur_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,c.getContenu());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, c.getLike());
            ps.setInt(4, c.getForumId());
            ps.setInt(5, c.getAuteurId());
            ps.executeUpdate();
            System.out.println("commentaire ajouté avec succès ! ");
        } catch (SQLException e) {
            System.out.println("erreur d'ajout du commentaire : "+ e.getMessage());
        }
    }

}



