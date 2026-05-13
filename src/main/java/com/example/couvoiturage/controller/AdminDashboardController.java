package com.example.couvoiturage.controller;

import com.example.couvoiturage.HelloApplication;
import com.example.couvoiturage.model.Role;
import com.example.couvoiturage.model.UserEntry;
import com.example.couvoiturage.model.UserLog;
import com.example.couvoiturage.util.DatabaseConnection;
import com.example.couvoiturage.util.LogUtil;
import com.example.couvoiturage.util.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class AdminDashboardController {

    @FXML private TableView<UserEntry> userTable;
    @FXML private TableColumn<UserEntry, String> colNom;
    @FXML private TableColumn<UserEntry, String> colPrenom;
    @FXML private TableColumn<UserEntry, String> colEmail;
    @FXML private TableColumn<UserEntry, String> colRole;
    @FXML private TableColumn<UserEntry, Void> colActions;

    @FXML private TextField searchField;
    @FXML private Label totalUsersLabel;
    @FXML private Label conducteursLabel;
    @FXML private Label passagersLabel;

    // Logs UI
    @FXML private VBox usersSection;
    @FXML private VBox logsSection;
    @FXML private Button usersNavBtn;
    @FXML private Button logsNavBtn;
    @FXML private TableView<UserLog> logTable;
    @FXML private TableColumn<UserLog, String> colLogUser;
    @FXML private TableColumn<UserLog, String> colLogAction;
    @FXML private TableColumn<UserLog, java.time.LocalDateTime> colLogDate;

    // Champs du formulaire d'ajout
    @FXML private TextField addNomField;
    @FXML private TextField addPrenomField;
    @FXML private TextField addCinField;
    @FXML private TextField addPhoneField;
    @FXML private TextField addEmailField;
    @FXML private PasswordField addPasswordField;
    @FXML private ComboBox<Role> addRoleComboBox;
    @FXML private VBox addConducteurFields;
    @FXML private Label addCarteGrisFileLabel;
    @FXML private Label addPermisFileLabel;
    @FXML private Label addMessageLabel;

    private String addCarteGrisPath = null;
    private String addPermisPath    = null;
    private Stage  addDialogStage;
    private ObservableList<UserEntry> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        setupLogTable();
        loadData();
        loadLogs();
    }

    private void setupLogTable() {
        colLogUser.setCellValueFactory(new PropertyValueFactory<>("utilisateurEmail"));
        colLogAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colLogDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    }

    private void loadLogs() {
        ObservableList<UserLog> logList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM user_logs ORDER BY timestamp DESC")) {

            while (rs.next()) {
                logList.add(new UserLog(
                    rs.getInt("id"),
                    rs.getString("utilisateur_email"),
                    rs.getString("action"),
                    rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
            logTable.setItems(logList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showUsersSection(ActionEvent event) {
        usersSection.setVisible(true);
        usersSection.setManaged(true);
        logsSection.setVisible(false);
        logsSection.setManaged(false);
        usersNavBtn.getStyleClass().add("nav-button-active");
        logsNavBtn.getStyleClass().remove("nav-button-active");
    }

    @FXML
    void showLogsSection(ActionEvent event) {
        usersSection.setVisible(false);
        usersSection.setManaged(false);
        logsSection.setVisible(true);
        logsSection.setManaged(true);
        usersNavBtn.getStyleClass().remove("nav-button-active");
        logsNavBtn.getStyleClass().add("nav-button-active");
        loadLogs();
    }

    // ── Table ──────────────────────────────────────────────────────────────────

    private void setupTable() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Setup Search/Filtering
        FilteredList<UserEntry> filteredData = new FilteredList<>(userList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<UserEntry> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedData);

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final Button editBtn   = new Button("Modifier");
            private final Button banBtn    = new Button("Bannir");
            private final HBox container   = new HBox(5, editBtn, banBtn, deleteBtn);
            {
                deleteBtn.setStyle("-fx-background-color: #ff7675; -fx-text-fill: white; -fx-font-size: 11px;");
                editBtn  .setStyle("-fx-background-color: #74b9ff; -fx-text-fill: white; -fx-font-size: 11px;");
                banBtn   .setStyle("-fx-background-color: #fdcb6e; -fx-text-fill: white; -fx-font-size: 11px;");

                deleteBtn.setOnAction(e -> handleDeleteUser(getTableView().getItems().get(getIndex())));
                editBtn  .setOnAction(e -> handleEditUser  (getTableView().getItems().get(getIndex())));
                banBtn   .setOnAction(e -> handleBanUser   (getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    UserEntry user = getTableView().getItems().get(getIndex());
                    if (user.isBanned()) {
                        banBtn.setText("Débannir");
                        banBtn.setStyle("-fx-background-color: #00b894; -fx-text-fill: white; -fx-font-size: 11px;");
                    } else {
                        banBtn.setText("Bannir");
                        banBtn.setStyle("-fx-background-color: #fdcb6e; -fx-text-fill: white; -fx-font-size: 11px;");
                    }
                    setGraphic(container);
                }
            }
        });
    }

    private void loadData() {
        userList.clear();
        int total = 0, conducteurs = 0, passagers = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery("SELECT * FROM utilisateur")) {

            while (rs.next()) {
                Role role = Role.valueOf(rs.getString("role"));
                boolean banned = rs.getBoolean("banned");
                userList.add(new UserEntry(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("cin"),
                    rs.getString("telephone"),
                    rs.getString("email"),
                    "",
                    role,
                    banned
                ));
                total++;
                if (role == Role.CONDUCTEUR) conducteurs++;
                else if (role == Role.PASSAGER) passagers++;
            }

            totalUsersLabel.setText(String.valueOf(total));
            conducteursLabel.setText(String.valueOf(conducteurs));
            passagersLabel.setText(String.valueOf(passagers));

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les données : " + e.getMessage());
        }
    }

    // ── Ajout utilisateur ─────────────────────────────────────────────────────

    @FXML
    void handleAddUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("add_user_dialog.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load());

            addRoleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
            addCarteGrisPath = null;
            addPermisPath    = null;

            addDialogStage = new Stage();
            addDialogStage.setTitle("Ajouter un utilisateur");
            addDialogStage.initModality(Modality.APPLICATION_MODAL);
            addDialogStage.setScene(scene);
            addDialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout.");
        }
    }

    @FXML
    void handleAddRoleSelection(ActionEvent event) {
        Role selected = addRoleComboBox.getValue();
        boolean isConducteur = (selected == Role.CONDUCTEUR);
        addConducteurFields.setVisible(isConducteur);
        addConducteurFields.setManaged(isConducteur);
    }

    @FXML
    void handleAddChooseCarteGris(ActionEvent event) {
        File f = openFileChooser("Sélectionner la Carte Grise");
        if (f != null) { addCarteGrisPath = f.getAbsolutePath(); addCarteGrisFileLabel.setText(f.getName()); }
    }

    @FXML
    void handleAddChoosePermis(ActionEvent event) {
        File f = openFileChooser("Sélectionner le Permis de Conduire");
        if (f != null) { addPermisPath = f.getAbsolutePath(); addPermisFileLabel.setText(f.getName()); }
    }

    private File openFileChooser(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documents (PDF, Images)", "*.pdf", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        return fc.showOpenDialog(addDialogStage != null ? addDialogStage : new Stage());
    }

    @FXML
    void handleCancelAdd(ActionEvent event) {
        if (addDialogStage != null) addDialogStage.close();
    }

    @FXML
    void handleConfirmAdd(ActionEvent event) {
        if (!validateAddFields()) return;

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO utilisateur (nom, prenom, cin, telephone, email, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement up = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            up.setString(1, addNomField.getText().trim());
            up.setString(2, addPrenomField.getText().trim());
            up.setString(3, addCinField.getText().trim());
            up.setString(4, addPhoneField.getText().trim());
            up.setString(5, addEmailField.getText().trim());
            up.setString(6, PasswordUtil.hash(addPasswordField.getText())); // BCrypt hash
            up.setString(7, addRoleComboBox.getValue().toString());
            up.executeUpdate();

            ResultSet rs = up.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                Role role  = addRoleComboBox.getValue();

                if (role == Role.CONDUCTEUR) {
                    PreparedStatement cp = conn.prepareStatement(
                        "INSERT INTO conducteur_info (utilisateur_id, carte_gris_path, permis_path) VALUES (?, ?, ?)");
                    cp.setInt(1, userId);
                    cp.setString(2, addCarteGrisPath);
                    cp.setString(3, addPermisPath);
                    cp.executeUpdate();
                } else if (role == Role.PASSAGER) {
                    PreparedStatement pp = conn.prepareStatement(
                        "INSERT INTO passager_info (utilisateur_id) VALUES (?)");
                    pp.setInt(1, userId);
                    pp.executeUpdate();
                }
            }

            conn.commit();
            LogUtil.log(addEmailField.getText(), "Création de compte par Admin");
            addDialogStage.close();
            loadData();

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            addMessageLabel.setText(e.getMessage().contains("Duplicate entry")
                ? "Email ou CIN déjà utilisé."
                : "Erreur lors de l'ajout : " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean validateAddFields() {
        if (addNomField.getText().trim().isEmpty()   ||
            addEmailField.getText().trim().isEmpty()  ||
            addPasswordField.getText().isEmpty()      ||
            addRoleComboBox.getValue() == null        ||
            addCinField.getText().trim().isEmpty()) {
            addMessageLabel.setText("Veuillez remplir les champs obligatoires.");
            return false;
        }
        if (addRoleComboBox.getValue() == Role.CONDUCTEUR &&
            (addCarteGrisPath == null || addPermisPath == null)) {
            addMessageLabel.setText("Veuillez joindre la Carte Grise et le Permis.");
            return false;
        }
        return true;
    }

    // ── Modifier / Supprimer ──────────────────────────────────────────────────

    private void handleEditUser(UserEntry user) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("edit_user.fxml"));
            Scene scene = new Scene(loader.load());
            EditUserController ctrl = loader.getController();
            ctrl.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Modifier l'utilisateur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            if (ctrl.isSaveClicked()) loadData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de modification.");
        }
    }

    private void handleDeleteUser(UserEntry user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Supprimer " + user.getNom() + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement p = conn.prepareStatement("DELETE FROM utilisateur WHERE id = ?")) {
                    p.setInt(1, user.getId());
                    p.executeUpdate();
                    LogUtil.log(user.getEmail(), "Utilisateur supprimé par Admin");
                    loadData();
                } catch (SQLException e) {
                    showAlert("Erreur", "Suppression impossible : " + e.getMessage());
                }
            }
        });
    }

    private void handleBanUser(UserEntry user) {
        String action = user.isBanned() ? "débannir" : "bannir";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            action + " " + user.getNom() + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement p = conn.prepareStatement("UPDATE utilisateur SET banned = ? WHERE id = ?")) {
                    p.setBoolean(1, !user.isBanned());
                    p.setInt(2, user.getId());
                    p.executeUpdate();
                    
                    String logAction = user.isBanned() ? "Utilisateur débanni par Admin" : "Utilisateur banni par Admin";
                    LogUtil.log(user.getEmail(), logAction);
                    loadData();
                } catch (SQLException e) {
                    showAlert("Erreur", "Opération impossible : " + e.getMessage());
                }
            }
        });
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        HelloApplication.changeScene("login.fxml", "Fi Thnitek - Connexion");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
