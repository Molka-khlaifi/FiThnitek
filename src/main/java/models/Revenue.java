package models;

public class Revenue {
    private int id;
    private int transactionId;
    private int userId;
    private String userType;
    private double montant;
    private String dateRevenue;
    private String typeRevenue;
    private String mois;
    private int nbPassagers;
    private String statut;
    private double montantRembourse;
    private String dateRemboursement;
    private String raisonRemboursement;
    private String description;

    public Revenue() {}

    public Revenue(int transactionId, int userId, String userType, double montant,
                   String dateRevenue, String typeRevenue, String mois,
                   int nbPassagers, String description) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.userType = userType;
        this.montant = montant;
        this.dateRevenue = dateRevenue;
        this.typeRevenue = typeRevenue;
        this.mois = mois;
        this.nbPassagers = nbPassagers;
        this.statut = "pending";          // always starts as pending
        this.montantRembourse = 0;        // 0 until refunded
        this.dateRemboursement = null;    // null until refunded
        this.raisonRemboursement = null;  // null until refunded
        this.description = description;
    }

    @Override
    public String toString() {
        return "Revenue{id=" + id + ", transactionId=" + transactionId +
                ", userId=" + userId + ", userType='" + userType +
                "', montant=" + montant + ", dateRevenue='" + dateRevenue +
                "', typeRevenue='" + typeRevenue + "', mois='" + mois +
                "', nbPassagers=" + nbPassagers + ", statut='" + statut +
                "', montantRembourse=" + montantRembourse +
                "', description='" + description + "'}";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getDateRevenue() { return dateRevenue; }
    public void setDateRevenue(String dateRevenue) { this.dateRevenue = dateRevenue; }

    public String getTypeRevenue() { return typeRevenue; }
    public void setTypeRevenue(String typeRevenue) { this.typeRevenue = typeRevenue; }

    public String getMois() { return mois; }
    public void setMois(String mois) { this.mois = mois; }

    public int getNbPassagers() { return nbPassagers; }
    public void setNbPassagers(int nbPassagers) { this.nbPassagers = nbPassagers; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public double getMontantRembourse() { return montantRembourse; }
    public void setMontantRembourse(double montantRembourse) { this.montantRembourse = montantRembourse; }

    public String getDateRemboursement() { return dateRemboursement; }
    public void setDateRemboursement(String dateRemboursement) { this.dateRemboursement = dateRemboursement; }

    public String getRaisonRemboursement() { return raisonRemboursement; }
    public void setRaisonRemboursement(String raisonRemboursement) { this.raisonRemboursement = raisonRemboursement; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}