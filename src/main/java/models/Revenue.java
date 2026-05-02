package models;

public class Revenue {
    private int id;
    private int userId;
    private String userType;
    private int tripId;
    private double montant;
    private String dateRevenue;
    private String typeRevenue;
    private String description;

    public Revenue() {}

    public Revenue(int userId, String userType, int tripId, double montant,
                   String dateRevenue, String typeRevenue, String description) {
        this.userId = userId;
        this.userType = userType;
        this.tripId = tripId;
        this.montant = montant;
        this.dateRevenue = dateRevenue;
        this.typeRevenue = typeRevenue;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Revenue{id=" + id + ", userId=" + userId + ", userType='" + userType +
                "', tripId=" + tripId + ", montant=" + montant + ", dateRevenue='" +
                dateRevenue + "', typeRevenue='" + typeRevenue + "', description='" +
                description + "'}";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getDateRevenue() { return dateRevenue; }
    public void setDateRevenue(String dateRevenue) { this.dateRevenue = dateRevenue; }

    public String getTypeRevenue() { return typeRevenue; }
    public void setTypeRevenue(String typeRevenue) { this.typeRevenue = typeRevenue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}