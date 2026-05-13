package models;

public class Admin extends Utilisateur {
    public Admin(String nom, String prenom, String cin, String numeroTelephone, String email, String motDePasse) {
        super(nom, prenom, cin, numeroTelephone, email, motDePasse, Role.ADMIN);
    }
}
