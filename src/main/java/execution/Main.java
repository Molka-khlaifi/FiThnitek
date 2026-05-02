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

        System.out.println("\n--- Revenues ---");
        System.out.println(revenueService.getAll());

        System.out.println("\n--- Transactions ---");
        System.out.println(transactionService.getAll());
    }
}