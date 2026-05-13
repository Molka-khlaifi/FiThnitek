package models;

import java.time.LocalDateTime;

public class UserLog {
    private int id;
    private String utilisateurEmail;
    private String action;
    private LocalDateTime timestamp;

    public UserLog(int id, String utilisateurEmail, String action, LocalDateTime timestamp) {
        this.id = id;
        this.utilisateurEmail = utilisateurEmail;
        this.action = action;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() { return id; }
    public String getUtilisateurEmail() { return utilisateurEmail; }
    public String getAction() { return action; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
