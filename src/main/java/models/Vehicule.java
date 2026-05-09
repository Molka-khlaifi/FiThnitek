package models;

public class Vehicule {

    private int idVehicule;
    private int idUtilisateur;
    private String marque;
    private String modele;
    private String immatriculation;
    private String couleur;
    private int annee;
    private int nombrePlaces;
    private String typeVehicule;
    private String energie;
    private String photoPath;
    private String statut;
    private String statutValidation;

    public Vehicule() {
    }

    public Vehicule(int idVehicule, int idUtilisateur, String marque, String modele, String immatriculation,
                    String couleur, int annee, int nombrePlaces, String typeVehicule,
                    String energie, String photoPath, String statut, String statutValidation) {
        this.idVehicule = idVehicule;
        this.idUtilisateur = idUtilisateur;
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.couleur = couleur;
        this.annee = annee;
        this.nombrePlaces = nombrePlaces;
        this.typeVehicule = typeVehicule;
        this.energie = energie;
        this.photoPath = photoPath;
        this.statut = statut;
        this.statutValidation = statutValidation;
    }

    public Vehicule(int idUtilisateur, String marque, String modele, String immatriculation,
                    String couleur, int annee, int nombrePlaces, String typeVehicule,
                    String energie, String photoPath, String statut, String statutValidation) {
        this.idUtilisateur = idUtilisateur;
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.couleur = couleur;
        this.annee = annee;
        this.nombrePlaces = nombrePlaces;
        this.typeVehicule = typeVehicule;
        this.energie = energie;
        this.photoPath = photoPath;
        this.statut = statut;
        this.statutValidation = statutValidation;
    }

    public int getIdVehicule() {
        return idVehicule;
    }

    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public String getTypeVehicule() {
        return typeVehicule;
    }

    public void setTypeVehicule(String typeVehicule) {
        this.typeVehicule = typeVehicule;
    }

    public String getEnergie() {
        return energie;
    }

    public void setEnergie(String energie) {
        this.energie = energie;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getStatutValidation() {
        return statutValidation;
    }

    public void setStatutValidation(String statutValidation) {
        this.statutValidation = statutValidation;
    }

    @Override
    public String toString() {
        return "Vehicule{" +
                "idVehicule=" + idVehicule +
                ", idUtilisateur=" + idUtilisateur +
                ", marque='" + marque + '\'' +
                ", modele='" + modele + '\'' +
                ", immatriculation='" + immatriculation + '\'' +
                ", couleur='" + couleur + '\'' +
                ", annee=" + annee +
                ", nombrePlaces=" + nombrePlaces +
                ", typeVehicule='" + typeVehicule + '\'' +
                ", energie='" + energie + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", statut='" + statut + '\'' +
                ", statutValidation='" + statutValidation + '\'' +
                '}';
    }
}