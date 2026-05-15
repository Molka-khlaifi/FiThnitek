package services;

import models.DocumentVehicule;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentVehiculeService implements IService<DocumentVehicule> {

    private final Connection connection;

    public DocumentVehiculeService() {
        connection = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(DocumentVehicule documentVehicule) {
        String sql = "INSERT INTO documentvehicule " +
                "(id_vehicule, type_document, nom_fichier, chemin_fichier, statut_document) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, documentVehicule.getIdVehicule());
            preparedStatement.setString(2, documentVehicule.getTypeDocument());
            preparedStatement.setString(3, documentVehicule.getNomFichier());
            preparedStatement.setString(4, documentVehicule.getCheminFichier());
            preparedStatement.setString(5, documentVehicule.getStatutDocument());

            preparedStatement.executeUpdate();

            System.out.println("Document véhicule ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur ajout document véhicule : " + e.getMessage());
        }
    }

    @Override
    public void update(DocumentVehicule documentVehicule) {
        String sql = "UPDATE documentvehicule SET " +
                "id_vehicule = ?, " +
                "type_document = ?, " +
                "nom_fichier = ?, " +
                "chemin_fichier = ?, " +
                "statut_document = ? " +
                "WHERE id_document = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, documentVehicule.getIdVehicule());
            preparedStatement.setString(2, documentVehicule.getTypeDocument());
            preparedStatement.setString(3, documentVehicule.getNomFichier());
            preparedStatement.setString(4, documentVehicule.getCheminFichier());
            preparedStatement.setString(5, documentVehicule.getStatutDocument());
            preparedStatement.setInt(6, documentVehicule.getIdDocument());

            preparedStatement.executeUpdate();

            System.out.println("Document véhicule modifié avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur modification document véhicule : " + e.getMessage());
        }
    }

    @Override
    public void delete(DocumentVehicule documentVehicule) {
        if (documentVehicule == null) {
            System.out.println("Aucun document véhicule sélectionné.");
            return;
        }

        String sql = "DELETE FROM documentvehicule WHERE id_document = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, documentVehicule.getIdDocument());

            preparedStatement.executeUpdate();

            System.out.println("Document véhicule supprimé avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur suppression document véhicule : " + e.getMessage());
        }
    }

    @Override
    public List<DocumentVehicule> getAll() {
        List<DocumentVehicule> documents = new ArrayList<>();

        String sql = "SELECT * FROM documentvehicule";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                DocumentVehicule documentVehicule = new DocumentVehicule();

                documentVehicule.setIdDocument(resultSet.getInt("idDocument"));
                documentVehicule.setIdVehicule(resultSet.getInt("idVehicule"));
                documentVehicule.setTypeDocument(resultSet.getString("typeDocument"));
                documentVehicule.setNomFichier(resultSet.getString("nomFichier"));
                documentVehicule.setCheminFichier(resultSet.getString("cheminFichier"));
                documentVehicule.setDateUpload(resultSet.getString("dateUpload"));
                documentVehicule.setStatutDocument(resultSet.getString("statutDocument"));

                documents.add(documentVehicule);
            }
        } catch (SQLException e) {
            System.out.println("Erreur affichage documents véhicules : " + e.getMessage());
        }

        return documents;
    }
}