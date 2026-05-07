package models;

public class Transaction {
    private int id;
    private int userId;
    private int tripId;
    private double montant;
    private double commissionPlatform;
    private double montantNet;
    private String dateTransaction;
    private String dateUpdate;
    private String methodePaiement;
    private String paymentRef;
    private String statut;

    public Transaction() {}

    public Transaction(int userId, int tripId, double montant, double commissionPlatform,
                       String dateTransaction, String methode_paiement, String paymentRef) {
        this.userId = userId;
        this.tripId = tripId;
        this.montant = montant;
        this.commissionPlatform = commissionPlatform;
        this.montantNet = montant - (montant * commissionPlatform);
        this.dateTransaction = dateTransaction;
        this.dateUpdate = dateTransaction;
        this.methodePaiement = methode_paiement;
        this.paymentRef = paymentRef;
        this.statut = "pending";
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", userId=" + userId + ", tripId=" + tripId +
                ", montant=" + montant + ", commissionPlatform=" + commissionPlatform +
                ", montantNet=" + montantNet + ", dateTransaction='" + dateTransaction +
                "', dateUpdate='" + dateUpdate + "', methodePaiement='" + methodePaiement +
                "', paymentRef='" + paymentRef + "', statut='" + statut + "'}";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public double getCommissionPlatform() { return commissionPlatform; }
    public void setCommissionPlatform(double commissionPlatform) { this.commissionPlatform = commissionPlatform; }

    public double getMontantNet() { return montantNet; }
    public void setMontantNet(double montantNet) { this.montantNet = montantNet; }

    public String getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(String dateTransaction) { this.dateTransaction = dateTransaction; }

    public String getDateUpdate() { return dateUpdate; }
    public void setDateUpdate(String dateUpdate) { this.dateUpdate = dateUpdate; }

    public String getMethodePaiement() { return methodePaiement; }
    public void setMethodePaiement(String methodePaiement) { this.methodePaiement = methodePaiement; }

    public String getPaymentRef() { return paymentRef; }
    public void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}