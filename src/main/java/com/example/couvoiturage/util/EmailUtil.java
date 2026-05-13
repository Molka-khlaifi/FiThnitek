package com.example.couvoiturage.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static final String FROM_EMAIL = "mhadhbichouaib4@gmail.com";
    // ATTENTION: Vous devez générer un "App Password" Google pour que cela fonctionne.
    // Allez sur votre compte Google -> Sécurité -> Validation en deux étapes -> Mots de passe d'application.
    private static final String APP_PASSWORD = "votre_mot_de_passe_application"; 

    public static void sendResetCode(String toEmail, String code) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Code de réinitialisation - Fi Thnitek");
            message.setText("Bonjour,\n\nVotre code de réinitialisation pour l'application Fi Thnitek est : " + code + 
                           "\n\nCe code est valable pendant 15 minutes.\n\nCordialement,\nL'équipe Fi Thnitek");

            Transport.send(message);
            System.out.println("Email envoyé avec succès à : " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
