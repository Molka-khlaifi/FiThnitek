package com.example.couvoiturage.model;

import java.util.ArrayList;
import java.util.List;

public class Conducteur extends Utilisateur {
    private String carteGrisPath;
    private String permisPath;
    private String statutValidation;
    private List<String> destinations;
    private double montantGagne;

    public Conducteur(String nom, String prenom, String cin, String numeroTelephone, String email, String motDePasse,
                      String carteGrisPath, String permisPath) {
        super(nom, prenom, cin, numeroTelephone, email, motDePasse, Role.CONDUCTEUR, false);
        this.carteGrisPath = carteGrisPath;
        this.permisPath = permisPath;
        this.statutValidation = "EN_ATTENTE";
        this.destinations = new ArrayList<>();
        this.montantGagne = 0.0;
    }

    public Conducteur(int id, String nom, String prenom, String cin, String numeroTelephone, String email, String motDePasse,
                      String carteGrisPath, String permisPath, String statutValidation) {
        super(id, nom, prenom, cin, numeroTelephone, email, motDePasse, Role.CONDUCTEUR, false);
        this.carteGrisPath = carteGrisPath;
        this.permisPath = permisPath;
        this.statutValidation = statutValidation;
        this.destinations = new ArrayList<>();
        this.montantGagne = 0.0;
    }

    // Getters and Setters
    public String getCarteGrisPath() { return carteGrisPath; }
    public void setCarteGrisPath(String carteGrisPath) { this.carteGrisPath = carteGrisPath; }

    public String getPermisPath() { return permisPath; }
    public void setPermisPath(String permisPath) { this.permisPath = permisPath; }

    public String getStatutValidation() { return statutValidation; }
    public void setStatutValidation(String statutValidation) { this.statutValidation = statutValidation; }

    public List<String> getDestinations() { return destinations; }
    public void addDestination(String destination) { this.destinations.add(destination); }

    public double getMontantGagne() { return montantGagne; }
    public void setMontantGagne(double montantGagne) { this.montantGagne = montantGagne; }
    public void addMontant(double montant) { this.montantGagne += montant; }
}
