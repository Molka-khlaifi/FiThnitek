package services;

import models.MaintenanceVehicule;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceVehiculeService implements IService<MaintenanceVehicule> {

    private final Connection connection;

    public MaintenanceVehiculeService() {
        connection = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(MaintenanceVehicule maintenance) {
        String sql = "INSERT INTO maintenance_vehicule " +
                "(idVehicule, kilometrageActuel, kilometrageDerniereVidange, kilometrageProchaineVidange, " +
                "dateDerniereVidange, dateExpirationAssurance, dateVisiteTechnique, dateExpirationVignette, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            remplirStatement(preparedStatement, maintenance);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur ajout maintenance vehicule : " + e.getMessage());
        }
    }

    @Override
    public void update(MaintenanceVehicule maintenance) {
        String sql = "UPDATE maintenance_vehicule SET " +
                "idVehicule = ?, " +
                "kilometrageActuel = ?, " +
                "kilometrageDerniereVidange = ?, " +
                "kilometrageProchaineVidange = ?, " +
                "dateDerniereVidange = ?, " +
                "dateExpirationAssurance = ?, " +
                "dateVisiteTechnique = ?, " +
                "dateExpirationVignette = ?, " +
                "notes = ? " +
                "WHERE idMaintenance = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            remplirStatement(preparedStatement, maintenance);
            preparedStatement.setInt(10, maintenance.getIdMaintenance());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur modification maintenance vehicule : " + e.getMessage());
        }
    }

    @Override
    public void delete(MaintenanceVehicule maintenance) {
        if (maintenance == null) {
            System.out.println("Aucune maintenance vehicule selectionnee.");
            return;
        }

        String sql = "DELETE FROM maintenance_vehicule WHERE idMaintenance = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, maintenance.getIdMaintenance());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur suppression maintenance vehicule : " + e.getMessage());
        }
    }

    @Override
    public List<MaintenanceVehicule> getAll() {
        List<MaintenanceVehicule> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_vehicule";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                maintenances.add(mapperMaintenance(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Erreur affichage maintenances vehicules : " + e.getMessage());
        }

        return maintenances;
    }

    public MaintenanceVehicule getByVehicule(int idVehicule) {
        String sql = "SELECT * FROM maintenance_vehicule WHERE idVehicule = ? ORDER BY idMaintenance DESC LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idVehicule);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapperMaintenance(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche maintenance vehicule : " + e.getMessage());
        }

        return null;
    }

    private void remplirStatement(PreparedStatement preparedStatement, MaintenanceVehicule maintenance) throws SQLException {
        preparedStatement.setInt(1, maintenance.getIdVehicule());
        preparedStatement.setInt(2, maintenance.getKilometrageActuel());
        preparedStatement.setInt(3, maintenance.getKilometrageDerniereVidange());
        preparedStatement.setInt(4, maintenance.getKilometrageProchaineVidange());
        setDate(preparedStatement, 5, maintenance.getDateDerniereVidange());
        setDate(preparedStatement, 6, maintenance.getDateExpirationAssurance());
        setDate(preparedStatement, 7, maintenance.getDateVisiteTechnique());
        setDate(preparedStatement, 8, maintenance.getDateExpirationVignette());
        preparedStatement.setString(9, maintenance.getNotes());
    }

    private MaintenanceVehicule mapperMaintenance(ResultSet resultSet) throws SQLException {
        MaintenanceVehicule maintenance = new MaintenanceVehicule();
        maintenance.setIdMaintenance(resultSet.getInt("idMaintenance"));
        maintenance.setIdVehicule(resultSet.getInt("idVehicule"));
        maintenance.setKilometrageActuel(resultSet.getInt("kilometrageActuel"));
        maintenance.setKilometrageDerniereVidange(resultSet.getInt("kilometrageDerniereVidange"));
        maintenance.setKilometrageProchaineVidange(resultSet.getInt("kilometrageProchaineVidange"));
        maintenance.setDateDerniereVidange(getDate(resultSet, "dateDerniereVidange"));
        maintenance.setDateExpirationAssurance(getDate(resultSet, "dateExpirationAssurance"));
        maintenance.setDateVisiteTechnique(getDate(resultSet, "dateVisiteTechnique"));
        maintenance.setDateExpirationVignette(getDate(resultSet, "dateExpirationVignette"));
        maintenance.setNotes(resultSet.getString("notes"));
        return maintenance;
    }

    private LocalDate getDate(ResultSet resultSet, String columnName) throws SQLException {
        Date date = resultSet.getDate(columnName);
        return date == null ? null : date.toLocalDate();
    }

    private void setDate(PreparedStatement preparedStatement, int index, LocalDate value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.DATE);
        } else {
            preparedStatement.setDate(index, Date.valueOf(value));
        }
    }
}
