package com.example.couvoiturage.model;

public abstract class Utilisateur {
    protected int id;
    protected String nom;
    protected String prenom;
    protected String cin;
    protected String numeroTelephone;
    protected String email;
    protected String motDePasse;
    protected Role role;
    protected boolean banned;

    public Utilisateur(int id, String nom, String prenom, String cin, String numeroTelephone, String email, String motDePasse, Role role, boolean banned) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.numeroTelephone = numeroTelephone;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.banned = banned;
    }

    public Utilisateur(String nom, String prenom, String cin, String numeroTelephone, String email, String motDePasse, Role role, boolean banned) {
        this(0, nom, prenom, cin, numeroTelephone, email, motDePasse, role, banned);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }
}
