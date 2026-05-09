package models;

public class DocumentVehicule {

    private int idDocument;
    private int idVehicule;
    private String typeDocument;
    private String nomFichier;
    private String cheminFichier;
    private String dateUpload;
    private String statutDocument;

    public DocumentVehicule() {
    }
    public DocumentVehicule(int idVehicule, String typeDocument, String nomFichier,
                            String cheminFichier, String statutDocument) {
        this.idVehicule = idVehicule;
        this.typeDocument = typeDocument;
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
        this.statutDocument = statutDocument;
    }
    public DocumentVehicule(int idDocument, int idVehicule, String typeDocument, String nomFichier, String cheminFichier, String dateUpload, String statutDocument) {
        this.idDocument = idDocument;
        this.idVehicule = idVehicule;
        this.typeDocument = typeDocument;
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
        this.dateUpload = dateUpload;
        this.statutDocument = statutDocument;
    }

    public DocumentVehicule(int idVehicule, String typeDocument, String nomFichier, String cheminFichier, String dateUpload, String statutDocument) {
        this.idVehicule = idVehicule;
        this.typeDocument = typeDocument;
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
        this.dateUpload = dateUpload;
        this.statutDocument = statutDocument;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public int getIdVehicule() {
        return idVehicule;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public String getDateUpload() {
        return dateUpload;
    }

    public String getStatutDocument() {
        return statutDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public void setDateUpload(String dateUpload) {
        this.dateUpload = dateUpload;
    }

    public void setStatutDocument(String statutDocument) {
        this.statutDocument = statutDocument;
    }

    @Override
    public String toString() {
        return "DocumentVehicule{" +
                "idDocument=" + idDocument +
                ", idVehicule=" + idVehicule +
                ", typeDocument='" + typeDocument + '\'' +
                ", nomFichier='" + nomFichier + '\'' +
                ", cheminFichier='" + cheminFichier + '\'' +
                ", dateUpload='" + dateUpload + '\'' +
                ", statutDocument='" + statutDocument + '\'' +
                '}';
    }
}
