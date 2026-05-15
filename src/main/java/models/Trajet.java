package models;

import java.time.LocalDateTime;

public class Trajet {

    private int           id;
    private String        depart;
    private String        destination;
    private LocalDateTime dateDepart;
    private LocalDateTime dateArrivee;
    private double        prix;
    private int           placesTotal;
    private int           placesDisponibles;
    private String        conducteurNom;
    private String        conducteurTel;
    private String        matriculeVehicule;   // ← nom aligné avec la DB et le dialog
    private String        description;
    private String        statut;

    // ── Constructeurs ────────────────────────────────────────────────────────

    public Trajet() {}

    public Trajet(String depart, String destination,
                  LocalDateTime dateDepart, LocalDateTime dateArrivee,
                  double prix, int placesTotal, int placesDisponibles,
                  String conducteurNom, String conducteurTel,
                  String matriculeVehicule, String description, String statut) {
        this.depart            = depart;
        this.destination       = destination;
        this.dateDepart        = dateDepart;
        this.dateArrivee       = dateArrivee;
        this.prix              = prix;
        this.placesTotal       = placesTotal;
        this.placesDisponibles = placesDisponibles;
        this.conducteurNom     = conducteurNom;
        this.conducteurTel     = conducteurTel;
        this.matriculeVehicule = matriculeVehicule;
        this.description       = description;
        this.statut            = statut != null ? statut : "ACTIF";
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDepart() { return depart; }
    public void setDepart(String depart) { this.depart = depart; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDateTime dateDepart) { this.dateDepart = dateDepart; }

    public LocalDateTime getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(LocalDateTime dateArrivee) { this.dateArrivee = dateArrivee; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getPlacesTotal() { return placesTotal; }
    public void setPlacesTotal(int placesTotal) { this.placesTotal = placesTotal; }

    public int getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }

    public String getConducteurNom() { return conducteurNom; }
    public void setConducteurNom(String conducteurNom) { this.conducteurNom = conducteurNom; }

    public String getConducteurTel() { return conducteurTel; }
    public void setConducteurTel(String conducteurTel) { this.conducteurTel = conducteurTel; }

    // ── matriculeVehicule ────────────────────────────────────────────────────
    public String getMatriculeVehicule() { return matriculeVehicule; }
    public void setMatriculeVehicule(String matriculeVehicule) { this.matriculeVehicule = matriculeVehicule; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    // ── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return depart + " -> " + destination + " (" + statut + ")";
    }
}