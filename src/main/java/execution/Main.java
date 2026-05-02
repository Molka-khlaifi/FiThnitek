package execution;

import models.Revenue;
import models.Transaction;
import services.IService;
import services.RevenueService;
import services.TransactionService;

public class Main {
    public static void main(String[] args) {
        IService<Revenue> revenueService = new RevenueService();
        IService<Transaction> transactionService = new TransactionService();

        // --- ADD REVENUES ---
        // Driver revenues
        Revenue r1 = new Revenue(1, "driver", 101, 15.0, "2024-01-10", "per_trip", "Trajet Tunis-Sousse");
        Revenue r2 = new Revenue(1, "driver", 102, 20.0, "2024-01-15", "per_trip", "Trajet Tunis-Sfax");
        Revenue r3 = new Revenue(1, "driver", 0, 50.0, "2024-01-31", "monthly", "Bonus janvier");
        // Passenger revenues
        Revenue r4 = new Revenue(2, "passenger", 101, 5.0, "2024-01-10", "per_trip", "Trajet Tunis-Sousse");
        Revenue r5 = new Revenue(3, "passenger", 102, 7.0, "2024-01-15", "per_trip", "Trajet Tunis-Sfax");

        revenueService.add(r1);
        revenueService.add(r2);
        revenueService.add(r3);
        revenueService.add(r4);
        revenueService.add(r5);

        // --- ADD TRANSACTIONS ---
        Transaction t1 = new Transaction(1, 15.0, "2024-01-10", "cash", "completed");
        Transaction t2 = new Transaction(2, 20.0, "2024-01-15", "cash", "completed");
        Transaction t3 = new Transaction(3, 50.0, "2024-01-31", "cash", "pending");
        Transaction t4 = new Transaction(4, 5.0, "2024-01-10", "cash", "completed");
        Transaction t5 = new Transaction(5, 7.0, "2024-01-15", "cash", "completed");

        transactionService.add(t1);
        transactionService.add(t2);
        transactionService.add(t3);
        transactionService.add(t4);
        transactionService.add(t5);

        // --- DISPLAY ---
        System.out.println("\n--- Revenues ---");
        System.out.println(revenueService.getAll());

        System.out.println("\n--- Transactions ---");
        System.out.println(transactionService.getAll());
    }
}