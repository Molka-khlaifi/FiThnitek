package controllers;

import org.example.Main;
import util.DBConnection;
import util.EmailUtil;
import util.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label messageLabel;

    private static String targetEmail; // Pour conserver l'email entre les deux écrans

    @FXML
    void handleSendCode(ActionEvent event) {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showMessage("Veuillez saisir votre email.", "#e74c3c");
            return;
        }

        try (Connection conn = DBConnection.getInstance().getConn()) {
            // Vérifier si l'utilisateur existe
            String checkSql = "SELECT id, telephone FROM utilisateur WHERE email = ?";
            PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
            checkPstmt.setString(1, email);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.next()) {
                String phone = rs.getString("telephone");
                String code = generateCode();
                
                // Sauvegarder le code en base (valable 15 minutes)
                String updateSql = "UPDATE utilisateur SET reset_code = ?, reset_expiry = ? WHERE email = ?";
                PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
                updatePstmt.setString(1, code);
                updatePstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)));
                updatePstmt.setString(3, email);
                updatePstmt.executeUpdate();

                targetEmail = email;
                
                // ENVOI RÉEL PAR EMAIL
                final String finalCode = code;
                new Thread(() -> EmailUtil.sendResetCode(email, finalCode)).start();
                
                // SIMULATION SMS (Toujours en console pour le moment car payant)
                simulateNotification(email, phone, code);
                
                Main.changeScene("reset_password.fxml", "Fi Thnitek - Réinitialisation");
            } else {
                showMessage("Aucun compte associé à cet email.", "#e74c3c");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showMessage("Erreur technique.", "#e74c3c");
        }
    }

    @FXML
    void handleResetPassword(ActionEvent event) {
        String code = codeField.getText().trim();
        String newPwd = newPasswordField.getText();

        if (code.isEmpty() || newPwd.isEmpty()) {
            showMessage("Veuillez remplir tous les champs.", "#e74c3c");
            return;
        }

        try (Connection conn = DBConnection.getInstance().getConn()) {
            String sql = "SELECT * FROM utilisateur WHERE email = ? AND reset_code = ? AND reset_expiry > ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, targetEmail);
            pstmt.setString(2, code);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Code valide -> Update password et clear reset code
                String updateSql = "UPDATE utilisateur SET password = ?, reset_code = NULL, reset_expiry = NULL WHERE email = ?";
                PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
                updatePstmt.setString(1, PasswordUtil.hash(newPwd));
                updatePstmt.setString(2, targetEmail);
                updatePstmt.executeUpdate();

                showMessage("Mot de passe réinitialisé ! Redirection...", "#27ae60");
                // Attendre un peu puis rediriger
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            try { goToLogin(null); } catch (IOException e) { e.printStackTrace(); }
                        });
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }).start();
            } else {
                showMessage("Code invalide ou expiré.", "#e74c3c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Erreur lors de la réinitialisation.", "#e74c3c");
        }
    }

    private void simulateNotification(String email, String phone, String code) {
        System.out.println("==========================================");
        System.out.println("SIMULATION D'ENVOI (GRATUIT)");
        System.out.println("À : " + email);
        System.out.println("SMS vers : " + (phone != null ? phone : "Non fourni"));
        System.out.println("MESSAGE : Votre code Fi Thnitek est : " + code);
        System.out.println("==========================================");
        
        // Optionnel : Vous pourriez ouvrir une petite alerte ici pour montrer le code
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    void goToLogin(ActionEvent event) throws IOException {
        Main.changeScene("login.fxml", "Fi Thnitek - Connexion");
    }
}
