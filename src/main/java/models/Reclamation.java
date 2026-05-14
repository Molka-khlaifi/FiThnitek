
package models;

import java.time.LocalDateTime;

public class Reclamation {

    private int id;
    private int idUser;
    private String objet;
    private String description;
    private String type;
    private String urgence;
    private String etat;
    private LocalDateTime date;

    // ---- Constructeur vide ----
    public Reclamation() {}

    // ---- Constructeur complet ----
    public Reclamation(int id, int idUser, String objet, String description,
                       String type, String urgence, String etat, LocalDateTime date) {
        this.id = id;
        this.idUser = idUser;
        this.objet = objet;
        this.description = description;
        this.type = type;
        this.urgence = urgence;
        this.etat = etat;
        this.date = date;
    }

    // ---- Constructeur sans ID (pour l'ajout) ----
    public Reclamation(int idUser, String objet, String description,
                       String type, String urgence) {
        this.idUser = idUser;
        this.objet = objet;
        this.description = description;
        this.type = type;
        this.urgence = urgence;
        this.etat = "En attente";
        this.date = LocalDateTime.now();
    }

    // ---- Getters & Setters ----
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getIdUser() { return idUser; }

    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getObjet() { return objet; }

    public void setObjet(String objet) { this.objet = objet; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getUrgence() { return urgence; }

    public void setUrgence(String urgence) { this.urgence = urgence; }

    public String getEtat() { return etat; }

    public void setEtat(String etat) { this.etat = etat; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", objet='" + objet + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", urgence='" + urgence + '\'' +
                ", etat='" + etat + '\'' +
                ", date=" + date +
                '}';
    }
}