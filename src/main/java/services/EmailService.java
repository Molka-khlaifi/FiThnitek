package services;

import com.example.couvoiturage.util.EmailConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            EmailConfig.SMTP_HOST);
        props.put("mail.smtp.port",            String.valueOf(EmailConfig.SMTP_PORT));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        EmailConfig.SENDER_EMAIL,
                        EmailConfig.SENDER_PASSWORD);
            }
        });
    }

    /** Sends an email on a background thread so the UI never freezes. */
    private static void sendAsync(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            System.out.println("[EmailService] No recipient address — skipping email.");
            return;
        }
        Thread t = new Thread(() -> {
            try {
                Session session = createSession();
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EmailConfig.SENDER_EMAIL, "Fi Thniytek"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
                System.out.println("[EmailService] Email sent successfully to: " + to);
            } catch (Exception e) {
                System.out.println("[EmailService] Failed to send email: " + e.getMessage());
            }
        }, "email-sender");
        t.setDaemon(true);
        t.start();
    }

    // ── Trip Completion ────────────────────────────────────────────────────────
    public static void sendTripCompletionEmail(String toEmail,
                                               int tripId,
                                               double montant,
                                               double montantNet,
                                               String methode) {
        String subject = "✅ Trip Completed — Fi Thniytek";
        String body = "Hello,\n\n"
                + "Your trip has been successfully completed.\n\n"
                + "─── Trip Details ───────────────────\n"
                + "Trip ID     : " + tripId + "\n"
                + "Amount      : " + String.format("%.2f", montant)    + " DT\n"
                + "Net Amount  : " + String.format("%.2f", montantNet) + " DT\n"
                + "Payment     : " + methode + "\n"
                + "────────────────────────────────────\n\n"
                + "Thank you for using Fi Thniytek!\n"
                + "— Fi Thniytek Team";
        sendAsync(toEmail, subject, body);
    }

    // ── Refund ─────────────────────────────────────────────────────────────────
    public static void sendRefundEmail(String toEmail,
                                       int transactionId,
                                       double montantRembourse) {
        String subject = "🔄 Refund Processed — Fi Thniytek";
        String body = "Hello,\n\n"
                + "Your refund has been successfully processed.\n\n"
                + "─── Refund Details ─────────────────\n"
                + "Transaction ID  : " + transactionId + "\n"
                + "Amount Refunded : " + String.format("%.2f", montantRembourse) + " DT\n"
                + "────────────────────────────────────\n\n"
                + "If you have any questions, please contact our support.\n"
                + "— Fi Thniytek Team";
        sendAsync(toEmail, subject, body);
    }
}
