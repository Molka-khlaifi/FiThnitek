package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Transaction;
import services.EmailService;
import services.TransactionService;
import services.UserService;
import services.exportService;

import java.net.URL;
import java.util.ResourceBundle;

public class transactionController implements Initializable {

    @FXML private exportService exportService = new exportService();
    @FXML private TextField userIdField;
    @FXML private TextField tripIdField;
    @FXML private TextField montantField;
    @FXML private TextField commissionField;
    @FXML private TextField dateField;
    @FXML private ComboBox<String> methodeField;
    @FXML private ComboBox<String> statutField;
    @FXML private TextField paymentRefField;
    @FXML private TextField nbPassagersField;
    @FXML private TextField emailField;
    @FXML private TextField searchField;
    @FXML private Label erreurLabel;
    @FXML private Button completeTripBtn;
    @FXML private Button updateBtn;
    @FXML private Button refundBtn;
    @FXML private Button deleteBtn;
    @FXML private Button clearBtn;

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> idCol;
    @FXML private TableColumn<Transaction, Integer> userIdCol;
    @FXML private TableColumn<Transaction, Integer> tripIdCol;
    @FXML private TableColumn<Transaction, Double> montantCol;
    @FXML private TableColumn<Transaction, Double> commissionCol;
    @FXML private TableColumn<Transaction, Double> montantNetCol;
    @FXML private TableColumn<Transaction, String> dateCol;
    @FXML private TableColumn<Transaction, String> methodeCol;
    @FXML private TableColumn<Transaction, String> paymentRefCol;
    @FXML private TableColumn<Transaction, String> statutCol;

    private TransactionService transactionService = new TransactionService();
    private UserService userService = new UserService();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactions;
    private int selectedTransactionId = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        methodeField.setItems(FXCollections.observableArrayList("cash", "flouci"));
        methodeField.setValue("cash");

        statutField.setItems(FXCollections.observableArrayList("pending", "completed", "refunded", "cancelled"));
        statutField.setValue("pending");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        tripIdCol.setCellValueFactory(new PropertyValueFactory<>("tripId"));
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        commissionCol.setCellValueFactory(new PropertyValueFactory<>("commissionPlatform"));
        montantNetCol.setCellValueFactory(new PropertyValueFactory<>("montantNet"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTransaction"));
        methodeCol.setCellValueFactory(new PropertyValueFactory<>("methodePaiement"));
        paymentRefCol.setCellValueFactory(new PropertyValueFactory<>("paymentRef"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        loadTable();

        transactionTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedTransactionId = newVal.getId();
                        userIdField.setText(String.valueOf(newVal.getUserId()));
                        tripIdField.setText(String.valueOf(newVal.getTripId()));
                        montantField.setText(String.valueOf(newVal.getMontant()));
                        commissionField.setText(String.valueOf(newVal.getCommissionPlatform()));
                        dateField.setText(newVal.getDateTransaction());
                        methodeField.setValue(newVal.getMethodePaiement());
                        paymentRefField.setText(newVal.getPaymentRef());
                        statutField.setValue(newVal.getStatut());
                        // Try to auto-fill email from users table
                        String resolvedEmail = userService.getEmailByUserId(newVal.getUserId());
                        emailField.setText(resolvedEmail);
                    }
                }
        );
    }

    private void loadTable() {
        transactionList.clear();
        transactionList.addAll(transactionService.getAll());

        filteredTransactions = new FilteredList<>(transactionList, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredTransactions.setPredicate(t -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return String.valueOf(t.getUserId()).contains(lower)
                        || String.valueOf(t.getTripId()).contains(lower);
            });
        });

        SortedList<Transaction> sortedTransactions = new SortedList<>(filteredTransactions);
        sortedTransactions.comparatorProperty().bind(transactionTable.comparatorProperty());
        transactionTable.setItems(sortedTransactions);
    }

    @FXML
    private void completeTrip() {
        try {
            int userId    = Integer.parseInt(userIdField.getText().trim());
            int tripId    = Integer.parseInt(tripIdField.getText().trim());
            double montant = Double.parseDouble(montantField.getText().trim());
            int nbPassagers = Integer.parseInt(nbPassagersField.getText().trim());
            String methode  = methodeField.getValue();
            String email    = emailField.getText().trim();

            transactionService.completeTrip(userId, tripId, montant, nbPassagers, methode);

            // Send notification in background (email may be empty — service handles it)
            double commission = 0.10;
            double montantNet = montant - (montant * commission);
            EmailService.sendTripCompletionEmail(email, tripId, montant, montantNet, methode);

            loadTable();
            clearFields();
            showAlert("Success", "Trip completed! Transaction and revenue generated.",
                    Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            erreurLabel.setText("User ID, Trip ID, Montant and Nb Passagers must be numbers!");
        } catch (Exception e) {
            erreurLabel.setText("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void updateTransaction() {
        if (selectedTransactionId == -1) {
            showAlert("Error", "Please select a transaction from the table to update.", Alert.AlertType.ERROR);
            return;
        }
        try {
            double montant = Double.parseDouble(montantField.getText().trim());
            double commission = Double.parseDouble(commissionField.getText().trim());
            String methode = methodeField.getValue();
            String paymentRef = paymentRefField.getText().trim();
            String statut = statutField.getValue();

            if (statut == null || statut.isEmpty()) {
                erreurLabel.setText("Please select a status.");
                return;
            }

            Transaction t = new Transaction();
            t.setId(selectedTransactionId);
            t.setMontant(montant);
            t.setCommissionPlatform(commission);
            t.setMontantNet(montant - (montant * commission));
            t.setMethodePaiement(methode);
            t.setPaymentRef(paymentRef);
            t.setStatut(statut);

            transactionService.update(t);
            loadTable();
            clearFields();
            showAlert("Success", "Transaction updated successfully!", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            erreurLabel.setText("Montant and Commission must be valid numbers!");
        } catch (Exception e) {
            erreurLabel.setText("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void refund() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a transaction to refund.", Alert.AlertType.ERROR);
            return;
        }

        // Resolve email — use field value if available, otherwise prompt
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Driver Email");
            dialog.setHeaderText("Enter the driver's email to send a refund notification:");
            dialog.setContentText("Email:");
            dialog.showAndWait().ifPresent(input -> emailField.setText(input.trim()));
            email = emailField.getText().trim();
        }

        transactionService.refundTransaction(selected.getId(), selected.getMontant(), "Refund requested");
        EmailService.sendRefundEmail(email, selected.getId(), selected.getMontant());
        loadTable();
        showAlert("Success", "Refund processed successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void deleteTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a transaction to delete.", Alert.AlertType.ERROR);
            return;
        }
        transactionService.delete(selected);
        loadTable();
        showAlert("Success", "Transaction deleted successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clearFields() {
        userIdField.clear();
        tripIdField.clear();
        montantField.clear();
        commissionField.clear();
        dateField.clear();
        paymentRefField.clear();
        nbPassagersField.clear();
        emailField.clear();
        erreurLabel.setText("");
        methodeField.setValue("cash");
        statutField.setValue("pending");
        selectedTransactionId = -1;
        transactionTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void exportPDF() {
        String path = System.getProperty("user.home") + "/Desktop/transactions_report.pdf";
        exportService.exportTransactionsToPDF(transactionService.getAll(), path);
        showAlert("Success", "PDF exported to your Desktop!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void exportExcel() {
        String path = System.getProperty("user.home") + "/Desktop/transactions_report.xlsx";
        exportService.exportTransactionsToExcel(transactionService.getAll(), path);
        showAlert("Success", "Excel exported to your Desktop!", Alert.AlertType.INFORMATION);
    }
}