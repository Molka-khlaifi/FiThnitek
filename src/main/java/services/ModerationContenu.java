package services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ModerationContenu {

    // Liste des mots irrespectueux (Solution 1)
    private static final Set<String> MOTS_IRRESPECTUEUX = new HashSet<>(Arrays.asList(
            "stupide", "idiot", "merde", "bite", "putain",
            "connard", "salope", "enculé", "fuck", "shit", "damn",
            "nul", "imbécile", "abruti", "crétin", "moron"
    ));

    // Pattern pour détecter les insultes déguisées (Solution 5)
    private static final Pattern MOTIF_INSULTES = Pattern.compile(
            "(?i)(c+n+|m+rde|j+f|b+tard|p+tain|connard|salope|encul[ée])"
    );

    /**
     * Vérifie et modère le contenu
     * @param texte Le texte à vérifier
     * @return Le texte modéré, ou null si le texte est rejeté
     */
    public static String moderer(String texte) {
        if (texte == null || texte.isEmpty()) {
            return texte;
        }

        // 1. Vérifier la liste noire (Solution 1)
        String texteMinuscule = texte.toLowerCase();
        for (String mot : MOTS_IRRESPECTUEUX) {
            if (texteMinuscule.contains(mot)) {
                System.out.println("Mot interdit détecté: " + mot);
                return null; // Contenu rejeté
            }
        }

        // 2. Vérifier les patterns d'insultes déguisées (Solution 5)
        if (MOTIF_INSULTES.matcher(texte).find()) {
            System.out.println("Pattern d'insulte détecté");
            return null; // Contenu rejeté
        }

        // 3. Nettoyer le texte (supprimer caractères spéciaux indésirables)
        texte = nettoyerTexte(texte);

        return texte;
    }

    /**
     * Nettoie le texte des caractères spéciaux
     */
    private static String nettoyerTexte(String texte) {
        // Garde les lettres, chiffres, espaces, ponctuation simple
        texte = texte.replaceAll("[^a-zA-Z0-9\\sàâäéèêëîïôöùûüçÀÂÄÉÈÊËÎÏÔÖÙÛÜÇ.,!?']", " ");
        return texte.trim();
    }

    /**
     * Message d'erreur pour l'utilisateur
     */
    public static String getMessageErreur() {
        return "❌ Votre commentaire contient des termes inappropriés. Veuillez reformuler votre message de façon respectueuse.";
    }
}