package models;

public class Transaction {
    private int id;
    private int revenueId;
    private double montant;
    private String dateTransaction;
    private String methodePaiement;
    private String statut;

    public Transaction() {}

    public Transaction(int revenueId, double montant, String dateTransaction,
                       String methodePaiement, String statut) {
        this.revenueId = revenueId;
        this.montant = montant;
        this.dateTransaction = dateTransaction;
        this.methodePaiement = methodePaiement;
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", revenueId=" + revenueId + ", montant=" + montant +
                ", dateTransaction='" + dateTransaction + "', methodePaiement='" +
                methodePaiement + "', statut='" + statut + "'}";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRevenueId() { return revenueId; }
    public void setRevenueId(int revenueId) { this.revenueId = revenueId; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(String dateTransaction) { this.dateTransaction = dateTransaction; }

    public String getMethodePaiement() { return methodePaiement; }
    public void setMethodePaiement(String methodePaiement) { this.methodePaiement = methodePaiement; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}