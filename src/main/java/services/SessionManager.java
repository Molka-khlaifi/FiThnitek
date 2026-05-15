package services;

public final class SessionManager {
    private static User currentUser = new User(1, "Utilisateur", "Demo", "user@fithnitek.local", "PASSAGER");

    private SessionManager() {
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static final class User {
        private final int id;
        private final String nom;
        private final String prenom;
        private final String email;
        private final String role;

        public User(int id, String nom, String prenom, String email, String role) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }
    }
}
