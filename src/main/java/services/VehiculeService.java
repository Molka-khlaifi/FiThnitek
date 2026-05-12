package services;

import models.Vehicule;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculeService implements IService<Vehicule> {

    private final Connection connection;

    public VehiculeService() {
        connection = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(Vehicule vehicule) {
        String sql = "INSERT INTO vehicule " +
                "(id_utilisateur, marque, modele, immatriculation, couleur, annee, nombre_places, type_vehicule, energie, photo_path, statut, statut_validation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vehicule.getIdUtilisateur());
            preparedStatement.setString(2, vehicule.getMarque());
            preparedStatement.setString(3, vehicule.getModele());
            preparedStatement.setString(4, vehicule.getImmatriculation());
            preparedStatement.setString(5, vehicule.getCouleur());
            preparedStatement.setInt(6, vehicule.getAnnee());
            preparedStatement.setInt(7, vehicule.getNombrePlaces());
            preparedStatement.setString(8, vehicule.getTypeVehicule());
            preparedStatement.setString(9, vehicule.getEnergie());
            preparedStatement.setString(10, vehicule.getPhotoPath());
            preparedStatement.setString(11, vehicule.getStatut());
            preparedStatement.setString(12, vehicule.getStatutValidation());

            preparedStatement.executeUpdate();

            System.out.println("Véhicule ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur ajout véhicule : " + e.getMessage());
        }
    }

    @Override
    public void update(Vehicule vehicule) {
        String sql = "UPDATE vehicule SET " +
                "id_utilisateur = ?, " +
                "marque = ?, " +
                "modele = ?, " +
                "immatriculation = ?, " +
                "couleur = ?, " +
                "annee = ?, " +
                "nombre_places = ?, " +
                "type_vehicule = ?, " +
                "energie = ?, " +
                "photo_path = ?, " +
                "statut = ?, " +
                "statut_validation = ? " +
                "WHERE id_vehicule = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, vehicule.getIdUtilisateur());
            preparedStatement.setString(2, vehicule.getMarque());
            preparedStatement.setString(3, vehicule.getModele());
            preparedStatement.setString(4, vehicule.getImmatriculation());
            preparedStatement.setString(5, vehicule.getCouleur());
            preparedStatement.setInt(6, vehicule.getAnnee());
            preparedStatement.setInt(7, vehicule.getNombrePlaces());
            preparedStatement.setString(8, vehicule.getTypeVehicule());
            preparedStatement.setString(9, vehicule.getEnergie());
            preparedStatement.setString(10, vehicule.getPhotoPath());
            preparedStatement.setString(11, vehicule.getStatut());
            preparedStatement.setString(12, vehicule.getStatutValidation());
            preparedStatement.setInt(13, vehicule.getIdVehicule());

            preparedStatement.executeUpdate();

            System.out.println("Véhicule modifié avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur modification véhicule : " + e.getMessage());
        }
    }

    @Override
    public void delete(Vehicule vehicule) {
        if (vehicule == null) {
            System.out.println("Aucun véhicule sélectionné.");
            return;
        }

        String sql = "UPDATE vehicule SET statut = ?, date_demande_suppression = NOW() WHERE id_vehicule = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "SUPPRESSION_DEMANDEE");
            preparedStatement.setInt(2, vehicule.getIdVehicule());

            int lignesModifiees = preparedStatement.executeUpdate();

            if (lignesModifiees > 0) {
                System.out.println("Demande de suppression du véhicule enregistrée !");
            } else {
                System.out.println("Aucun véhicule modifié.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur demande suppression véhicule : " + e.getMessage());
        }
    }

    public void supprimerVehiculesApres48h() {
        String sql = "DELETE FROM vehicule " +
                "WHERE statut = ? " +
                "AND date_demande_suppression IS NOT NULL " +
                "AND date_demande_suppression <= DATE_SUB(NOW(), INTERVAL 48 HOUR)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, "SUPPRESSION_DEMANDEE");

            int lignesSupprimees = preparedStatement.executeUpdate();

            System.out.println(lignesSupprimees + " véhicule(s) supprimé(s) définitivement.");
        } catch (SQLException e) {
            System.out.println("Erreur suppression définitive véhicules après 48h : " + e.getMessage());
        }
    }

    public List<Vehicule> afficher() {
        return getAll();
    }

    @Override
    public List<Vehicule> getAll() {
        List<Vehicule> vehicules = new ArrayList<>();

        String sql = "SELECT * FROM vehicule";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Vehicule vehicule = new Vehicule();

                vehicule.setIdVehicule(resultSet.getInt("idVehicule"));
                vehicule.setIdUtilisateur(resultSet.getInt("idUtilisateur"));
                vehicule.setMarque(resultSet.getString("marque"));
                vehicule.setModele(resultSet.getString("modele"));
                vehicule.setImmatriculation(resultSet.getString("immatriculation"));
                vehicule.setCouleur(resultSet.getString("couleur"));
                vehicule.setAnnee(resultSet.getInt("annee"));
                vehicule.setNombrePlaces(resultSet.getInt("nombrePlaces"));
                vehicule.setTypeVehicule(resultSet.getString("typeVehicule"));
                vehicule.setEnergie(resultSet.getString("energie"));
                vehicule.setPhotoPath(resultSet.getString("photoPath"));
                vehicule.setStatut(resultSet.getString("statut"));
                vehicule.setStatutValidation(resultSet.getString("statutValidation"));

                vehicules.add(vehicule);
            }
        } catch (SQLException e) {
            System.out.println("Erreur affichage véhicules : " + e.getMessage());
        }

        return vehicules;
    }

    public List<Vehicule> rechercher(String keyword) {
        List<Vehicule> vehicules = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }

        String sql = "SELECT * FROM vehicule WHERE " +
                "marque LIKE ? OR " +
                "modele LIKE ? OR " +
                "immatriculation LIKE ? OR " +
                "couleur LIKE ? OR " +
                "type_vehicule LIKE ? OR " +
                "energie LIKE ? OR " +
                "statut LIKE ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            String searchValue = keyword.trim() + "%";

            preparedStatement.setString(1, searchValue);
            preparedStatement.setString(2, searchValue);
            preparedStatement.setString(3, searchValue);
            preparedStatement.setString(4, searchValue);
            preparedStatement.setString(5, searchValue);
            preparedStatement.setString(6, searchValue);
            preparedStatement.setString(7, searchValue);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Vehicule vehicule = new Vehicule();

                vehicule.setIdVehicule(resultSet.getInt("id_vehicule"));
                vehicule.setIdUtilisateur(resultSet.getInt("id_utilisateur"));
                vehicule.setMarque(resultSet.getString("marque"));
                vehicule.setModele(resultSet.getString("modele"));
                vehicule.setImmatriculation(resultSet.getString("immatriculation"));
                vehicule.setCouleur(resultSet.getString("couleur"));
                vehicule.setAnnee(resultSet.getInt("annee"));
                vehicule.setNombrePlaces(resultSet.getInt("nombre_places"));
                vehicule.setTypeVehicule(resultSet.getString("type_vehicule"));
                vehicule.setEnergie(resultSet.getString("energie"));
                vehicule.setPhotoPath(resultSet.getString("photo_path"));
                vehicule.setStatut(resultSet.getString("statut"));
                vehicule.setStatutValidation(resultSet.getString("statut_validation"));

                vehicules.add(vehicule);
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche véhicules : " + e.getMessage());
        }

        return vehicules;
    }
}