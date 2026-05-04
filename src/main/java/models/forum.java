package models;

import java.util.Date;

public class forum {
    int id;
    String titre;
    String contenu;
    String categorie;
    String statut;
    Date date_creation;
    int nb_vues;
    int auteurId;
    int trajetId;

    public forum() {}
    public forum(String titre, String contenu, String categorie, String statut, Date date_creation, int nb_vues, int auteurId, int trajetId) {
        this.titre = titre;
        this.contenu = contenu;
        this.categorie = categorie;
        this.statut = statut;
        this.date_creation = date_creation;
        this.nb_vues = nb_vues;
        this.auteurId = auteurId;
        this.trajetId = trajetId;
    }

    public forum(int id,String titre, String contenu, String categorie, String statut, Date date_creation ,int nb_vues, int auteurId,int trajet_id) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.categorie = categorie;
        this.statut = statut;
        this.date_creation = date_creation;
        this.nb_vues = nb_vues;
        this.auteurId = auteurId;
        this.trajetId = trajet_id;
    }

    public int getId() {
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

    public int getTrajetId() {
        return trajetId;
    }

    public void setTrajet_id(int trajetId) {
        this.trajetId = trajetId;
    }
    @Override
    public String toString() {
        return "Forum{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", categorie='" + categorie + '\'' +
                ", statut='" + statut + '\'' +
                ", nbVues=" + nb_vues +
                ", auteurId=" + auteurId +
                '}';}

}
