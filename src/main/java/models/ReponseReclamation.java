package models;

import java.time.LocalDateTime;

public class ReponseReclamation {

    private int id;
    private int idReclamation;
    private int idAdmin;
    private String message;
    private LocalDateTime date;

    // ---- Constructeur vide ----
    public ReponseReclamation() {}

    // ---- Constructeur complet ----
    public ReponseReclamation(int id, int idReclamation, int idAdmin,
                              String message, LocalDateTime date) {
        this.id = id;
        this.idReclamation = idReclamation;
        this.idAdmin = idAdmin;
        this.message = message;
        this.date = date;
    }

    // ---- Constructeur sans ID (pour l'ajout) ----
    public ReponseReclamation(int idReclamation, int idAdmin, String message) {
        this.idReclamation = idReclamation;
        this.idAdmin = idAdmin;
        this.message = message;
        this.date = LocalDateTime.now();
    }

    // ---- Getters & Setters ----
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getIdReclamation() { return idReclamation; }

    public void setIdReclamation(int idReclamation) { this.idReclamation = idReclamation; }

    public int getIdAdmin() { return idAdmin; }

    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }

    @Override
    public String toString() {
        return "ReponseReclamation{" +
                "id=" + id +
                ", idReclamation=" + idReclamation +
                ", idAdmin=" + idAdmin +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
