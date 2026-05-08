package models;

import java.util.Date;

public class publication {
    private int id;
    String titre;
    String contenu;
    String categorie;
    String statut;
    Date date_creation;
    int nb_vues;
    int auteurId;
    Integer trajetId;
    boolean epingle;
    String image;

    public publication() {}
    public publication(String titre, String contenu, String categorie, String statut, Date date_creation, int nb_vues, int auteurId, Integer trajetId, boolean epingle,String image) {
        this.titre = titre;
        this.contenu = contenu;
        this.categorie = categorie;
        this.statut = statut;
        this.date_creation = date_creation;
        this.nb_vues = nb_vues;
        this.auteurId = auteurId;
        this.trajetId = trajetId;
        this.epingle = epingle;
        this.image = image;
    }

    public publication(int id,String titre, String contenu, String categorie, String statut, Date date_creation ,int nb_vues, int auteurId,Integer trajet_id,boolean epingle,String image) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.categorie = categorie;
        this.statut = statut;
        this.date_creation = date_creation;
        this.nb_vues = nb_vues;
        this.auteurId = auteurId;
        this.trajetId = trajet_id;
        this.epingle = epingle;
        this.image = image;
    }

    public  int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Date getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(Date date_creation) {
        this.date_creation = date_creation;
    }

    public int getNb_vues() {
        return nb_vues;
    }

    public void setNb_vues(int nb_vues) {
        this.nb_vues = nb_vues;
    }

    public int getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(int auteurId) {
        this.auteurId = auteurId;
    }

    public Integer getTrajetId() {
        return trajetId;
    }

    public void setTrajetId(Integer trajetId) {
        this.trajetId = trajetId;
    }
    public boolean isEpingle() {
        return epingle;
    }

    public void setEpingle(boolean epingle) {
        this.epingle = epingle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "publication{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", categorie='" + categorie + '\'' +
                ", statut='" + statut + '\'' +
                ", nbVues=" + nb_vues +
                ", auteurId=" + auteurId + ", trajetId=" + trajetId +
                '}';}

}
