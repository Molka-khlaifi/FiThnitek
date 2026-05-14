package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import models.MaintenanceVehicule;
import models.Vehicule;
import services.MaintenanceVehiculeService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceController {

    @FXML
    private Label messageLabel;

    @FXML
    private Label rappelsActifsLabel;

    @FXML
    private Label alertesProchesLabel;

    @FXML
    private Label prochaineActionLabel;

    @FXML
    private Label vidangeTitleLabel;

    @FXML
    private Label vidangeDescriptionLabel;

    @FXML
    private Label vidangeBadgeLabel;

    @FXML
    private Label assuranceDescriptionLabel;

    @FXML
    private Label assuranceBadgeLabel;

    @FXML
    private Label visiteDescriptionLabel;

    @FXML
    private Label visiteBadgeLabel;

    @FXML
    private Label vignetteDescriptionLabel;

    @FXML
    private Label vignetteBadgeLabel;

    private final MaintenanceVehiculeService maintenanceVehiculeService = new MaintenanceVehiculeService();

    private Vehicule vehicule;
    private MaintenanceVehicule maintenance;
    private final List<String> notifications = new ArrayList<>();

    @FXML
    public void initialize() {
        afficherEtatSansVehicule();
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
        chargerMaintenance();
    }

    private void chargerMaintenance() {
        if (vehicule == null) {
            afficherEtatSansVehicule();
            return;
        }

        maintenance = maintenanceVehiculeService.getByVehicule(vehicule.getIdVehicule());

        if (maintenance == null) {
            afficherMaintenanceManquante();
            return;
        }

        notifications.clear();
        int rappelsActifs = 0;
        int alertesProches = 0;

        EtatMaintenance vidange = evaluerVidange();
        EtatMaintenance assurance = evaluerDate("Assurance", maintenance.getDateExpirationAssurance());
        EtatMaintenance visite = evaluerDate("Visite technique", maintenance.getDateVisiteTechnique());
        EtatMaintenance vignette = evaluerDate("Vignette", maintenance.getDateExpirationVignette());

        EtatMaintenance[] etats = {vidange, assurance, visite, vignette};
        for (EtatMaintenance etat : etats) {
            if (etat.actif) {
                rappelsActifs++;
            }
            if (etat.proche) {
                alertesProches++;
            }
            if (etat.notification != null && !etat.notification.isEmpty()) {
                notifications.add(etat.notification);
            }
        }

        rappelsActifsLabel.setText(String.valueOf(rappelsActifs));
        alertesProchesLabel.setText(String.valueOf(alertesProches));
        prochaineActionLabel.setText(getProchaineAction(vidange, assurance, visite, vignette));

        vidangeTitleLabel.setText("Vidange bient\u00f4t");
        vidangeDescriptionLabel.setText(vidange.description);
        vidangeBadgeLabel.setText(vidange.badge);
        appliquerCouleurBadge(vidangeBadgeLabel, vidange.couleur);

        assuranceDescriptionLabel.setText(assurance.description);
        assuranceBadgeLabel.setText(assurance.badge);
        appliquerCouleurBadge(assuranceBadgeLabel, assurance.couleur);

        visiteDescriptionLabel.setText(visite.description);
        visiteBadgeLabel.setText(visite.badge);
        appliquerCouleurBadge(visiteBadgeLabel, visite.couleur);

        vignetteDescriptionLabel.setText(vignette.description);
        vignetteBadgeLabel.setText(vignette.badge);
        appliquerCouleurBadge(vignetteBadgeLabel, vignette.couleur);

        messageLabel.setText("Maintenance charg\u00e9e pour " + texteVehicule() + ".");
    }

    private void afficherEtatSansVehicule() {
        rappelsActifsLabel.setText("0");
        alertesProchesLabel.setText("0");
        prochaineActionLabel.setText("Aucune");
        vidangeTitleLabel.setText("Vidange");
        vidangeDescriptionLabel.setText("Ouvrez cette page depuis un v\u00e9hicule s\u00e9lectionn\u00e9.");
        assuranceDescriptionLabel.setText("Aucun v\u00e9hicule s\u00e9lectionn\u00e9.");
        visiteDescriptionLabel.setText("Aucun v\u00e9hicule s\u00e9lectionn\u00e9.");
        vignetteDescriptionLabel.setText("Aucun v\u00e9hicule s\u00e9lectionn\u00e9.");
        definirBadgesEnAttente();
        notifications.clear();
        messageLabel.setText("S\u00e9lectionnez un v\u00e9hicule depuis Mon espace pour afficher sa maintenance.");
    }

    private void afficherMaintenanceManquante() {
        rappelsActifsLabel.setText("0");
        alertesProchesLabel.setText("0");
        prochaineActionLabel.setText("A cr\u00e9er");
        vidangeTitleLabel.setText("Vidange");
        vidangeDescriptionLabel.setText("Aucune donn\u00e9e de vidange enregistr\u00e9e pour ce v\u00e9hicule.");
        assuranceDescriptionLabel.setText("Aucune date d'assurance enregistr\u00e9e.");
        visiteDescriptionLabel.setText("Aucune date de visite technique enregistr\u00e9e.");
        vignetteDescriptionLabel.setText("Aucune date de vignette enregistr\u00e9e.");
        definirBadgesEnAttente();
        notifications.clear();
        notifications.add("Aucune fiche maintenance n'est encore enregistr\u00e9e pour " + texteVehicule() + ".");
        messageLabel.setText("Aucune maintenance trouv\u00e9e pour " + texteVehicule() + ".");
    }

    private void definirBadgesEnAttente() {
        vidangeBadgeLabel.setText("A renseigner");
        assuranceBadgeLabel.setText("A renseigner");
        visiteBadgeLabel.setText("A renseigner");
        vignetteBadgeLabel.setText("A renseigner");
        appliquerCouleurBadge(vidangeBadgeLabel, "#8a94a6");
        appliquerCouleurBadge(assuranceBadgeLabel, "#8a94a6");
        appliquerCouleurBadge(visiteBadgeLabel, "#8a94a6");
        appliquerCouleurBadge(vignetteBadgeLabel, "#8a94a6");
    }

    private EtatMaintenance evaluerVidange() {
        int kilometrageActuel = maintenance.getKilometrageActuel();
        int prochaineVidange = maintenance.getKilometrageProchaineVidange();

        if (prochaineVidange <= 0) {
            return new EtatMaintenance(
                    "Kilom\u00e9trage de prochaine vidange non renseign\u00e9.",
                    "A renseigner",
                    "#8a94a6",
                    false,
                    false,
                    "Kilom\u00e9trage de prochaine vidange \u00e0 renseigner."
            );
        }

        int kmRestants = prochaineVidange - kilometrageActuel;

        if (kmRestants <= 0) {
            return new EtatMaintenance(
                    "Vidange d\u00e9pass\u00e9e de " + Math.abs(kmRestants) + " km.",
                    "Urgent",
                    "#e74c3c",
                    true,
                    true,
                    "Vidange d\u00e9pass\u00e9e de " + Math.abs(kmRestants) + " km."
            );
        }

        if (kmRestants <= 500) {
            return new EtatMaintenance(
                    "Vidange recommand\u00e9e dans " + kmRestants + " km.",
                    "Bient\u00f4t",
                    "#f59e0b",
                    true,
                    true,
                    "Vidange recommand\u00e9e dans " + kmRestants + " km."
            );
        }

        return new EtatMaintenance(
                "Prochaine vidange dans " + kmRestants + " km.",
                "OK",
                "#0F6E56",
                true,
                false,
                ""
        );
    }

    private EtatMaintenance evaluerDate(String libelle, LocalDate date) {
        if (date == null) {
            return new EtatMaintenance(
                    libelle + " : date non renseign\u00e9e.",
                    "A renseigner",
                    "#8a94a6",
                    false,
                    false,
                    libelle + " : date \u00e0 renseigner."
            );
        }

        long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), date);

        if (joursRestants < 0) {
            return new EtatMaintenance(
                    libelle + " expir\u00e9e depuis " + Math.abs(joursRestants) + " jour(s).",
                    "Urgent",
                    "#e74c3c",
                    true,
                    true,
                    libelle + " expir\u00e9e depuis " + Math.abs(joursRestants) + " jour(s)."
            );
        }

        if (joursRestants <= 30) {
            return new EtatMaintenance(
                    libelle + " \u00e0 renouveler dans " + joursRestants + " jour(s).",
                    "Bient\u00f4t",
                    "#f59e0b",
                    true,
                    true,
                    libelle + " \u00e0 renouveler dans " + joursRestants + " jour(s)."
            );
        }

        return new EtatMaintenance(
                libelle + " valide jusqu'au " + date + ".",
                "OK",
                "#0F6E56",
                true,
                false,
                ""
        );
    }

    private String getProchaineAction(EtatMaintenance vidange, EtatMaintenance assurance, EtatMaintenance visite, EtatMaintenance vignette) {
        if (vidange.proche) {
            return "Vidange";
        }
        if (assurance.proche) {
            return "Assurance";
        }
        if (visite.proche) {
            return "Visite";
        }
        if (vignette.proche) {
            return "Vignette";
        }
        return "Aucune";
    }

    private void appliquerCouleurBadge(Label badgeLabel, String couleur) {
        badgeLabel.setStyle(
                "-fx-background-color: " + couleur + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 4 10 4 10;"
        );
    }

    private String texteVehicule() {
        if (vehicule == null) {
            return "ce v\u00e9hicule";
        }

        String marque = vehicule.getMarque() == null ? "" : vehicule.getMarque();
        String modele = vehicule.getModele() == null ? "" : vehicule.getModele();
        String nom = (marque + " " + modele).trim();
        return nom.isEmpty() ? "ce v\u00e9hicule" : nom;
    }

    @FXML
    private void monEspaceVehiculeAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionVehicule.fxml"));
            messageLabel.getScene().setRoot(root);
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du retour vers Mon Espace V\u00e9hicule.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void impactEnergetiqueAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ImpactEnergetique.fxml"));
            messageLabel.getScene().setRoot(root);
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture de la page Impact \u00e9nerg\u00e9tique.");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void maintenanceAction() {
        messageLabel.setText("Vous \u00eates d\u00e9j\u00e0 dans la page Maintenance.");
    }

    @FXML
    private void afficherNotificationsAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rappels maintenance");
        alert.setHeaderText("Notifications pr\u00e9ventives");
        alert.setContentText(getTexteNotifications());
        alert.showAndWait();
    }

    private String getTexteNotifications() {
        if (notifications.isEmpty()) {
            return "Aucune notification pr\u00e9ventive pour le moment.";
        }

        StringBuilder builder = new StringBuilder();
        for (String notification : notifications) {
            builder.append("- ").append(notification).append("\n");
        }
        return builder.toString();
    }

    private static class EtatMaintenance {
        private final String description;
        private final String badge;
        private final String couleur;
        private final boolean actif;
        private final boolean proche;
        private final String notification;

        private EtatMaintenance(String description, String badge, String couleur, boolean actif, boolean proche, String notification) {
            this.description = description;
            this.badge = badge;
            this.couleur = couleur;
            this.actif = actif;
            this.proche = proche;
            this.notification = notification;
        }
    }
}
