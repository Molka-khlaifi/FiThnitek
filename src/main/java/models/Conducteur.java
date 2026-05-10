package models;

import java.util.ArrayList;
import java.util.List;

public class Conducteur extends Utilisateur {
    private String marqueVoiture;
    private String modeleVoiture;
    private String immatriculation;
    private List<String> destinations;
    private double montantGagne;

    public Conducteur(String nom, String prenom, String cin, String numeroTelephone, String email, String motDePasse, 
                     String marqueVoiture, String modeleVoiture, String immatriculation) {
        super(nom, prenom, cin, numeroTelephone, email, motDePasse, Role.CONDUCTEUR);
        this.marqueVoiture = marqueVoiture;
        this.modeleVoiture = modeleVoiture;
        this.immatriculation = immatriculation;
        this.destinations = new ArrayList<>();
        this.montantGagne = 0.0;
    }

    // Getters and Setters
    public String getMarqueVoiture() { return marqueVoiture; }
    public void setMarqueVoiture(String marqueVoiture) { this.marqueVoiture = marqueVoiture; }

    public String getModeleVoiture() { return modeleVoiture; }
    public void setModeleVoiture(String modeleVoiture) { this.modeleVoiture = modeleVoiture; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public List<String> getDestinations() { return destinations; }
    public void addDestination(String destination) { this.destinations.add(destination); }

    public double getMontantGagne() { return montantGagne; }
    public void setMontantGagne(double montantGagne) { this.montantGagne = montantGagne; }
    public void addMontant(double montant) { this.montantGagne += montant; }
}
