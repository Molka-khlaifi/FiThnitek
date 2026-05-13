package entities;

import java.time.LocalDateTime;

public class Reservation {

    private int id;
    private int trajetId;

    private String passagerNom;
    private String passagerEmail;
    private String passagerTel;

    private int nombrePlaces;

    private LocalDateTime dateReservation;

    private String statut;
    private String commentaire;

    private double montantTotal;

    // Affichage trajet
    private String trajetInfo;

    public Reservation() {
    }

    public Reservation(
            int trajetId,
            String passagerNom,
            String passagerEmail,
            String passagerTel,
            int nombrePlaces,
            String statut,
            String commentaire,
            double montantTotal
    ) {

        this.trajetId = trajetId;
        this.passagerNom = passagerNom;
        this.passagerEmail = passagerEmail;
        this.passagerTel = passagerTel;

        this.nombrePlaces = nombrePlaces;

        this.dateReservation = LocalDateTime.now();

        this.statut = statut;
        this.commentaire = commentaire;

        this.montantTotal = montantTotal;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrajetId() {
        return trajetId;
    }

    public void setTrajetId(int trajetId) {
        this.trajetId = trajetId;
    }

    public String getPassagerNom() {
        return passagerNom;
    }

    public void setPassagerNom(String passagerNom) {
        this.passagerNom = passagerNom;
    }

    public String getPassagerEmail() {
        return passagerEmail;
    }

    public void setPassagerEmail(String passagerEmail) {
        this.passagerEmail = passagerEmail;
    }

    public String getPassagerTel() {
        return passagerTel;
    }

    public void setPassagerTel(String passagerTel) {
        this.passagerTel = passagerTel;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(
            LocalDateTime dateReservation
    ) {
        this.dateReservation = dateReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    @SuppressWarnings("unused")
    public String getTrajetInfo() {
        return trajetInfo;
    }

    public void setTrajetInfo(String trajetInfo) {
        this.trajetInfo = trajetInfo;
    }

    @Override
    public String toString() {

        return passagerNom
                + " - "
                + nombrePlaces
                + " place(s) - "
                + statut;
    }
}