package models;

public class UserEntry extends Utilisateur {
    public UserEntry(int id, String nom, String prenom, String cin, String numeroTelephone, String email, String motDePasse, Role role) {
        super(id, nom, prenom, cin, numeroTelephone, email, motDePasse, role);
    }
}
