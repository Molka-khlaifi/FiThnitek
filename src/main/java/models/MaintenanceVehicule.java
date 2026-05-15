package models;

import java.time.LocalDate;

public class MaintenanceVehicule {

    private int idMaintenance;
    private int idVehicule;
    private int kilometrageActuel;
    private int kilometrageDerniereVidange;
    private int kilometrageProchaineVidange;
    private LocalDate dateDerniereVidange;
    private LocalDate dateExpirationAssurance;
    private LocalDate dateVisiteTechnique;
    private LocalDate dateExpirationVignette;
    private String notes;

    public MaintenanceVehicule() {
    }

    public int getIdMaintenance() {
        return idMaintenance;
    }

    public void setIdMaintenance(int idMaintenance) {
        this.idMaintenance = idMaintenance;
    }

    public int getIdVehicule() {
        return idVehicule;
    }

    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }

    public int getKilometrageActuel() {
        return kilometrageActuel;
    }

    public void setKilometrageActuel(int kilometrageActuel) {
        this.kilometrageActuel = kilometrageActuel;
    }

    public int getKilometrageDerniereVidange() {
        return kilometrageDerniereVidange;
    }

    public void setKilometrageDerniereVidange(int kilometrageDerniereVidange) {
        this.kilometrageDerniereVidange = kilometrageDerniereVidange;
    }

    public int getKilometrageProchaineVidange() {
        return kilometrageProchaineVidange;
    }

    public void setKilometrageProchaineVidange(int kilometrageProchaineVidange) {
        this.kilometrageProchaineVidange = kilometrageProchaineVidange;
    }

    public LocalDate getDateDerniereVidange() {
        return dateDerniereVidange;
    }

    public void setDateDerniereVidange(LocalDate dateDerniereVidange) {
        this.dateDerniereVidange = dateDerniereVidange;
    }

    public LocalDate getDateExpirationAssurance() {
        return dateExpirationAssurance;
    }

    public void setDateExpirationAssurance(LocalDate dateExpirationAssurance) {
        this.dateExpirationAssurance = dateExpirationAssurance;
    }

    public LocalDate getDateVisiteTechnique() {
        return dateVisiteTechnique;
    }

    public void setDateVisiteTechnique(LocalDate dateVisiteTechnique) {
        this.dateVisiteTechnique = dateVisiteTechnique;
    }

    public LocalDate getDateExpirationVignette() {
        return dateExpirationVignette;
    }

    public void setDateExpirationVignette(LocalDate dateExpirationVignette) {
        this.dateExpirationVignette = dateExpirationVignette;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
