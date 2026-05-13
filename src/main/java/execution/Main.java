package execution;

import models.Revenue;
import models.Transaction;
import services.IService;
import services.RevenueService;
import services.TransactionService;

public class Main {
    public static void main(String[] args) {

        TransactionService transactionService = new TransactionService();
        RevenueService revenueService = new RevenueService();

        // ── Simulate driver completing a cash trip ──────────
        System.out.println("\n=== Driver completes Trip 1 (cash) ===");
        transactionService.completeTrip(1, 101, 15.0, 2, "cash");

        // ── Simulate driver completing a stripe trip ────────
        System.out.println("\n=== Driver completes Trip 2 (stripe) ===");
        transactionService.completeTrip(1, 102, 20.0, 3, "stripe");

        // ── Simulate another driver completing a trip ───────
        System.out.println("\n=== Driver 2 completes Trip 3 (cash) ===");
        transactionService.completeTrip(2, 103, 25.0, 1, "cash");

        // ── Display all transactions ─────────────────────────
        System.out.println("\n=== All Transactions ===");
        for (Transaction t : transactionService.getAll()) {
            System.out.println(t);
        }

        // ── Display all revenues ─────────────────────────────
        System.out.println("\n=== All Revenues ===");
        for (Revenue r : revenueService.getAll()) {
            System.out.println(r);
        }

        // ── Display driver 1 revenues only ──────────────────
        System.out.println("\n=== Driver 1 Revenues ===");
        for (Revenue r : revenueService.getByDriver(1)) {
            System.out.println(r);
        }

        // ── Simulate a refund ────────────────────────────────
        System.out.println("\n=== Refund Transaction 1 ===");
        transactionService.refundTransaction(1, 15.0, "Trajet annule par le conducteur");

        // ── Display after refund ─────────────────────────────
        System.out.println("\n=== All Revenues After Refund ===");
        for (Revenue r : revenueService.getAll()) {
            System.out.println(r);
        }
    }
}