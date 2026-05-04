package models;

import java.time.LocalDateTime;

public class commentaire {
    int id;
    String contenu;
    LocalDateTime dateCommentaire;
    int likes;
    int forumId;
    int auteurId;
    public commentaire() {}
    public commentaire(String contenu, LocalDateTime dateCommentaire, int likes, int forumId, int auteurId) {
        this.contenu = contenu;
        this.dateCommentaire = dateCommentaire;
        this.likes = likes;
        this.forumId = forumId;
        this.auteurId = auteurId;
    }
    public commentaire(int id, String contenu, LocalDateTime dateCommentaire, int likes, int forumId, int auteurId) {
        this.id = id;
        this.contenu = contenu;
        this.dateCommentaire = dateCommentaire;
        this.likes = likes;
        this.forumId = forumId;
        this.auteurId = auteurId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateCommentaire() {
        return dateCommentaire;
    }

    public void setDateCommentaire(LocalDateTime dateCommentaire) {
        this.dateCommentaire = dateCommentaire;
    }

    public int getLike() {
        return likes;
    }

    public void setLike(int like) {
        this.likes = like;
    }

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public int getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(int auteurId) {
        this.auteurId = auteurId;
    }
    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", likes=" + likes +
                ", forumId=" + forumId +
                ", auteurId=" + auteurId +
                '}';
    }
}
