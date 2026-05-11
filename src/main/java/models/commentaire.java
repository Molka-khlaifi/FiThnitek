package models;

import java.time.LocalDateTime;

public class commentaire {
    int id;
    String contenu;
    LocalDateTime dateCommentaire;
    int likes;
    int publicationId;
    int auteurId;
    String auteurNom;

    public commentaire() {}
    public commentaire(String contenu, LocalDateTime dateCommentaire, int likes, int publicationId, int auteurId) {
        this.contenu = contenu;
        this.dateCommentaire = dateCommentaire;
        this.likes = likes;
        this.publicationId = publicationId;
        this.auteurId = auteurId;
    }
    public commentaire(int id, String contenu, LocalDateTime dateCommentaire, int likes, int publicationId, int auteurId) {
        this.id = id;
        this.contenu = contenu;
        this.dateCommentaire = dateCommentaire;
        this.likes = likes;
        this.publicationId = publicationId;
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

    public int getpublicationId() {
        return publicationId;
    }

    public void setpublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    public int getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(int auteurId) {
        this.auteurId = auteurId;
    }

    public String getauteurNom() {return auteurNom;}
    public void setAuteurNom(String auteurNom) { this.auteurNom = auteurNom; }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", likes=" + likes +
                ", publicationId=" + publicationId +
                ", auteurId=" + auteurId +
                '}';
    }
}
