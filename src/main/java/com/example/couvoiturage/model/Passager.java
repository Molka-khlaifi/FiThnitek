package com.example.couvoiturage.model;

import java.util.ArrayList;
import java.util.List;

public class Passager extends Utilisateur {
    private List<String> historiqueTrajets;

    public Passager(String nom, String prenom, String cin, String numeroTelephone, String email, String motDePasse) {
        super(nom, prenom, cin, numeroTelephone, email, motDePasse, Role.PASSAGER, false);
        this.historiqueTrajets = new ArrayList<>();
    }

    public List<String> getHistoriqueTrajets() { return historiqueTrajets; }
    public void addTrajet(String trajet) { this.historiqueTrajets.add(trajet); }
}
