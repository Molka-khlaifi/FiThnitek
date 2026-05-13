package services;

import models.commentaire;
import models.publication;
import util.DBConnection;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class forumService implements IService<publication> {

    Connection con;
    private static final String TRANSLATE_URL = "https://api.mymemory.translated.net/get";

    public forumService() {
        this.con = DBConnection.getInstance().getConn();
    }

    // ───────────────── TRADUCTION ─────────────────
    public String traduireEnAnglais(String texte) {
        try {
            // Essai 1 : détection automatique
            String resultat = essayerTraduction(texte, "autodetect|en");
            if (resultat != null && !resultat.equalsIgnoreCase(texte)) {
                return resultat;
            }
            // Essai 2 : français → anglais
            resultat = essayerTraduction(texte, "fr|en");
            if (resultat != null && !resultat.equalsIgnoreCase(texte)) {
                return resultat;
            }
            // Essai 3 : arabe → anglais
            resultat = essayerTraduction(texte, "ar|en");
            if (resultat != null && !resultat.equalsIgnoreCase(texte)) {
                return resultat;
            }
            return texte;

        } catch (Exception e) {
            System.out.println("Exception traduction : " + e.getMessage());
            return texte;
        }
    }

    private String essayerTraduction(String texte, String langpair) {
        try {
            String textEncode = java.net.URLEncoder.encode(texte, StandardCharsets.UTF_8);
            String urlStr = TRANSLATE_URL + "?q=" + textEncode + "&langpair=" + langpair;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            System.out.println("MyMemory [" + langpair + "] status : " + status);

            if (status == 200) {
                Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                String json = response.toString();
                System.out.println("Réponse [" + langpair + "] : " + json);

                // Vérifier responseStatus dans le JSON
                if (json.contains("\"responseStatus\":200") ||
                        json.contains("\"responseStatus\": 200")) {

                    String key = "\"translatedText\":\"";
                    int start = json.indexOf(key) + key.length();
                    int end = json.indexOf("\"", start);

                    if (start > key.length() && end > start) {
                        return json.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur essayerTraduction [" + langpair + "] : " + e.getMessage());
        }
        return null;
    }

    // ───────────────── ADD ─────────────────
    @Override
    public void add(publication p) {
        if (p.getAuteurId() == 0) {
            System.out.println("ERROR: auteurId is 0 (invalid FK)");
            return;
        }
        String sql = "INSERT INTO publication (titre, contenu, categorie, statut, date_creation, nb_vues, auteurId, trajetId, epingle, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, p.getTitre());
            ps.setString(2, p.getContenu());
            ps.setString(3, p.getCategorie());
            ps.setString(4, p.getStatut());
            ps.setDate(5, new Date(p.getDate_creation().getTime()));
            ps.setInt(6, p.getNb_vues());
            ps.setInt(7, p.getAuteurId());
            if (p.getTrajetId() == null || p.getTrajetId() == 0) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, p.getTrajetId());
            }
            ps.setBoolean(9, p.isEpingle());
            ps.setString(10, p.getImage());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ADD ERROR: " + e.getMessage());
        }
    }

    // ───────────────── UPDATE ─────────────────
    @Override
    public void update(publication p) {
        String sql = "UPDATE publication SET titre=?, contenu=?, categorie=?, statut=?, date_creation=?, nb_vues=?, auteurId=?, trajetId=?, epingle=?, image=? WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, p.getTitre());
            ps.setString(2, p.getContenu());
            ps.setString(3, p.getCategorie());
            ps.setString(4, p.getStatut());
            ps.setDate(5, new Date(p.getDate_creation().getTime()));
            ps.setInt(6, p.getNb_vues());
            ps.setInt(7, p.getAuteurId());
            if (p.getTrajetId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, p.getTrajetId());
            }
            ps.setBoolean(9, p.isEpingle());
            ps.setString(10, p.getImage());
            ps.setInt(11, p.getId());
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
                list.add(mapPublication(rs));
            }
        } catch (SQLException e) {
            System.out.println("GET ALL ERROR: " + e.getMessage());
        }
        return list;
    }

    // ───────────────── GET BY AUTEUR ─────────────────
    public List<publication> getByAuteur(int auteurId) {
        List<publication> list = new ArrayList<>();
        String sql = "SELECT * FROM publication WHERE auteurId = ? ORDER BY date_creation DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, auteurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapPublication(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getByAuteur : " + e.getMessage());
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
            if (rs.next()) return mapPublication(rs);
        } catch (SQLException e) {
            System.out.println("GET BY ID ERROR: " + e.getMessage());
        }
        return null;
    }

    // ───────────────── GET NOM AUTEUR ─────────────────
    public String getNomAuteur(int auteurId) {
        String sql = "SELECT nom, prenom FROM utilisateur WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, auteurId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nom") + " " + rs.getString("prenom");
            }
        } catch (SQLException e) {
            System.out.println("GET NOM AUTEUR ERROR: " + e.getMessage());
        }
        return "Auteur inconnu";
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
                list.add(mapPublication(rs));
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
                list.add(mapPublication(rs));
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
                list.add(mapPublication(rs));
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

    public List<publication> getEpingles() {
        List<publication> list = new ArrayList<>();
        String sql = "SELECT * FROM publication WHERE epingle = TRUE ORDER BY date_creation DESC";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(mapPublication(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getEpingles : " + e.getMessage());
        }
        return list;
    }

    // ───────────────── VUES ─────────────────
    public void incrementerVues(int id) {
        String req = "UPDATE publication SET nb_vues = nb_vues + 1 WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ───────────────── COMMENTAIRES ─────────────────
    public void addcommentaire(commentaire c) {
        String sql = "INSERT INTO commentaire (contenu, dateCommentaire, likes, publicationId, auteurId) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, c.getContenu());
            ps.setTimestamp(2, Timestamp.valueOf(c.getDateCommentaire()));
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

    public List<commentaire> getcommentairesBypublication(int publicationId) {
        List<commentaire> liste = new ArrayList<>();
        String sql = "SELECT c.*, u.nom AS AuteurNom " +
                "FROM commentaire c " +
                "JOIN utilisateur u ON c.auteurId = u.id " +
                "WHERE c.publicationId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, publicationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                commentaire c = new commentaire(
                        rs.getString("contenu"),
                        rs.getTimestamp("dateCommentaire").toLocalDateTime(),
                        rs.getInt("likes"),
                        rs.getInt("publicationId"),
                        rs.getInt("auteurId")
                );
                c.setId(rs.getInt("id"));
                liste.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return liste;
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

    // ───────────────── STATISTIQUES ─────────────────
    public int countPostsByutilisateur(int utilisateurId) {
        String sql = "SELECT COUNT(*) FROM publication WHERE auteurId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("COUNT POSTS ERROR: " + e.getMessage());
        }
        return 0;
    }

    public int countCommentairesByutilisateur(int utilisateurId) {
        String sql = "SELECT COUNT(*) FROM commentaire WHERE auteurId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("COUNT COMMENTS ERROR: " + e.getMessage());
        }
        return 0;
    }

    public int countLikesByutilisateur(int utilisateurId) {
        String sql = "SELECT SUM(likes) FROM commentaire WHERE auteurId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("COUNT LIKES ERROR: " + e.getMessage());
        }
        return 0;
    }

    public int calculScore(int utilisateurId) {
        int posts = countPostsByutilisateur(utilisateurId);
        int commentaires = countCommentairesByutilisateur(utilisateurId);
        int likes = countLikesByutilisateur(utilisateurId);
        return (posts * 5) + (commentaires * 2) + likes;
    }

    public String getBadge(int utilisateurId) {
        int score = calculScore(utilisateurId);
        if (score >= 100) return "👑 Legend";
        if (score >= 50)  return "🔥 Super Fan";
        if (score >= 20)  return "⭐ Utilisateur Actif";
        return "🌱 Newbie";
    }

    // ───────────────── MAPPER ─────────────────
    private publication mapPublication(ResultSet rs) throws SQLException {
        Integer trajetId = (Integer) rs.getObject("trajetId");
        publication p = new publication(
                rs.getInt("id"),
                rs.getString("titre"),
                rs.getString("contenu"),
                rs.getString("categorie"),
                rs.getString("statut"),
                rs.getDate("date_creation"),
                rs.getInt("nb_vues"),
                rs.getInt("auteurId"),
                trajetId,
                rs.getBoolean("epingle"),
                rs.getString("image")
        );
        p.setImage(rs.getString("image"));
        return p;
    }
}