package controllers;

import com.google.zxing.WriterException;
import com.stripe.exception.StripeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Transaction;
import services.EmailService;
import services.StripeService;
import services.TransactionService;
import services.UserService;
import services.exportService;
import util.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;

public class transactionController implements Initializable {

    @FXML private exportService exportService = new exportService();
    @FXML private TextField  userIdField;
    @FXML private TextField  tripIdField;
    @FXML private TextField  montantField;
    @FXML private TextField  commissionField;
    @FXML private TextField  dateField;
    @FXML private ComboBox<String> methodeField;
    @FXML private ComboBox<String> statutField;
    @FXML private TextField  paymentRefField;
    @FXML private TextField  nbPassagersField;
    @FXML private TextField  emailField;
    @FXML private TextField  searchField;
    @FXML private Label      erreurLabel;
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
    private UserService        userService        = new UserService();
    private StripeService      stripeService      = new StripeService();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private FilteredList<Transaction>   filteredTransactions;
    private int selectedTransactionId = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ── Auto-fill from session ──────────────────────────────────────────
        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            int uid = SessionManager.getCurrentUser().getId();
            userIdField.setText(String.valueOf(uid));
            // Resolve email from session object first, then DB fallback
            String sessionEmail = SessionManager.getCurrentUser().getEmail();
            emailField.setText(
                (sessionEmail != null && !sessionEmail.isBlank())
                    ? sessionEmail
                    : userService.getEmailByUserId(uid)
            );
        }

        // ── Auto-fill date = today ──────────────────────────────────────────
        dateField.setText(java.time.LocalDate.now().toString());

        // ── Auto-fill commission default ────────────────────────────────────
        commissionField.setText("0.10");

        // ── Payment method choices ──────────────────────────────────────────
        methodeField.setItems(FXCollections.observableArrayList("cash", "stripe"));
        methodeField.setValue("cash");

        statutField.setItems(FXCollections.observableArrayList("pending", "completed", "refunded", "cancelled"));
        statutField.setValue("pending");

        // ── Table columns ───────────────────────────────────────────────────
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

        // ── Table row selection → populate form ─────────────────────────────
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
                        String resolvedEmail = userService.getEmailByUserId(newVal.getUserId());
                        if (!resolvedEmail.isBlank()) emailField.setText(resolvedEmail);
                    }
                }
        );

        // Hint when user picks Stripe
        methodeField.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if ("stripe".equals(n)) {
                erreurLabel.setText("\u2139 Select a row or fill Amount + Trip, then click \"Stripe QR\".");
            } else {
                erreurLabel.setText("");
            }
        });
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
            int userId = Integer.parseInt(userIdField.getText().trim());
            int tripId = Integer.parseInt(tripIdField.getText().trim());
            double montant   = Double.parseDouble(montantField.getText().trim());
            int    nbPass    = Integer.parseInt(nbPassagersField.getText().trim());
            String methode   = methodeField.getValue();
            String email     = emailField.getText().trim();

            transactionService.completeTrip(userId, tripId, montant, nbPass, methode);

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
        // Restore session defaults instead of blanking everything
        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            userIdField.setText(String.valueOf(SessionManager.getCurrentUser().getId()));
            String email = SessionManager.getCurrentUser().getEmail();
            emailField.setText(email != null ? email : "");
        } else {
            userIdField.clear();
            emailField.clear();
        }
        tripIdField.clear();
        montantField.clear();
        commissionField.setText("0.10");
        dateField.setText(java.time.LocalDate.now().toString());
        paymentRefField.clear();
        nbPassagersField.clear();
        erreurLabel.setText("");
        methodeField.setValue("cash");
        statutField.setValue("pending");
        selectedTransactionId = -1;
        transactionTable.getSelectionModel().clearSelection();
    }

    /**
     * Creates a Stripe Checkout Session for the amount shown in montantField
     * (or the selected transaction's amount) and displays the payment URL as
     * a scannable QR code in a popup dialog.
     */
    @FXML
    private void showStripeQR() {
        double montant;
        int tripId;

        // Prefer the selected table row; fall back to the form fields
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            montant = selected.getMontant();
            tripId  = selected.getTripId();
        } else {
            try {
                montant = Double.parseDouble(montantField.getText().trim());
                tripId  = Integer.parseInt(tripIdField.getText().trim());
            } catch (NumberFormatException ex) {
                showAlert("Missing Info",
                        "Please select a transaction row OR enter Montant and Trip ID first.",
                        Alert.AlertType.WARNING);
                return;
            }
        }

        try {
            // 1. Create Stripe Checkout Session
            String checkoutUrl = stripeService.createCheckoutSession(montant, tripId);

            // 2. Generate QR code image (300×300 px)
            WritableImage qrImage = stripeService.generateQRImage(checkoutUrl, 300);

            // 3. Build the popup
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Stripe Payment QR Code");

            ImageView qrView = new ImageView(qrImage);
            qrView.setFitWidth(300);
            qrView.setFitHeight(300);
            qrView.setPreserveRatio(true);

            Text header = new Text("Scan to pay " + String.format("%.2f DT", montant) + " via Stripe");
            header.setFont(Font.font("System", FontWeight.BOLD, 14));

            Hyperlink link = new Hyperlink(checkoutUrl);
            link.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(checkoutUrl));
                } catch (Exception ignored) {}
            });

            Button closeBtn = new Button("Close");
            closeBtn.setStyle("-fx-background-color: #635BFF; -fx-text-fill: white; "
                    + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; "
                    + "-fx-padding: 6 20;");
            closeBtn.setOnAction(e -> popup.close());

            VBox box = new VBox(12, header, qrView, link, closeBtn);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(20));
            box.setStyle("-fx-background-color: white;");

            popup.setScene(new Scene(box));
            popup.setResizable(false);
            popup.showAndWait();

        } catch (StripeException ex) {
            showAlert("Stripe Error",
                    "Could not create Stripe session:\n" + ex.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (WriterException ex) {
            showAlert("QR Error",
                    "Failed to generate QR code:\n" + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
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