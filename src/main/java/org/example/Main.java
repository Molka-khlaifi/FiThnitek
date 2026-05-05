package org.example;

import models.publication;
import services.forumService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        forumService service = new forumService();

        System.out.println("==============================");
        System.out.println("      TEST GESTION publication      ");
        System.out.println("==============================");

        // ─── 1. Ajouter un publication ──────────────────────────────────────────
        System.out.println("\n--- ADD publication ---");
        publication f1 = new publication(
                "Trajet Tunis → Sfax disponible",
                "Bonjour, je propose un trajet chaque vendredi soir.",
                "annonce",
                "ouvert",
                new java.util.Date(),
                0,
                1,
                0,
                false
        );
        service.add(f1);

        publication f2 = new publication(
                "Conseil pour covoiturage longue distance ?",
                "Quelqu'un a des conseils pour les trajets de plus de 3h ?",
                "question",
                "ouvert",
                new java.util.Date(),
                0,
                1,
                0,
                false

        );
        service.add(f2);

        // ─── 2. Afficher tous les publications ──────────────────────────────────
        System.out.println("\n--- GET ALL publications ---");
        List<publication> publications = service.getAll();
        for (publication f : publications) {
            System.out.println(f);
        }

        // ─── 3. Modifier un publication ─────────────────────────────────────────
        System.out.println("\n--- UPDATE publication (id=1) ---");
        publication fUpdate = service.getById(1); // ✅
        if (fUpdate != null) {
            fUpdate.setTitre("Trajet Tunis → Sfax VENDREDI (MAJ)");
            fUpdate.setStatut("ouvert");
            service.update(fUpdate); // ✅
            System.out.println("Après modification : " + service.getById(1));
        }

        // ─── 4. Filtrer par catégorie ─────────────────────────────────────
        System.out.println("\n--- GET BY CATEGORIE 'question' ---");
        List<publication> questions = service.getByCategorie("question");
        for (publication f : questions) {
            System.out.println(f);
        }

        // ─── 5. Supprimer un publication ────────────────────────────────────────
        System.out.println("\n--- DELETE publication ---");
        service.delete(f2);
        System.out.println("publications restants : " + service.getAll().size());

        System.out.println("\n====== TOUS LES TESTS PASSES ======");
    }
}