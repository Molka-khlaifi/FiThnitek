
package utils;

import  java.util.Arrays;
import java.util.List;

public class BadWordsFilter {

    // ✅ Liste de mots interdits (ajoute autant que tu veux)
    private static final List<String> BAD_WORDS = Arrays.asList(
            // Insultes françaises
            "idiot", "imbecile", "connard", "connasse", "salaud", "salope",
            "batard", "batarde", "cretin", "cretine", "abruti", "abrutie",
            "enfoiré", "enfoiree", "ordure", "pourriture", "merde", "putain",
            "con", "conne", "nul", "nulle", "stupide", "incompetent",
            "arnaque", "arnaquer", "voleur", "voleuse", "escroc",
            // Insultes arabes (translittération)
            "kahba", "wald el kahba", "zebi", "9a7ba", "7mar", "kelb",
            "wled el 9a7ba", "bel3a", "sharmouta",
            // Menaces
            "je vais te tuer", "je te retrouve", "je sais où tu habites"
    );

    /**
     * Retourne true si le texte contient un mot interdit.
     */
    public static boolean containsBadWord(String text) {
        if (text == null || text.isEmpty()) return false;

        String textLower = text.toLowerCase()
                .replace("é", "e").replace("è", "e").replace("ê", "e")
                .replace("à", "a").replace("â", "a")
                .replace("ô", "o").replace("û", "u").replace("î", "i")
                .replace("ç", "c");

        for (String word : BAD_WORDS) {
            if (textLower.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne le premier mot interdit trouvé (pour afficher dans le message d'erreur).
     */
    public static String getFoundBadWord(String text) {
        if (text == null || text.isEmpty()) return "";

        String textLower = text.toLowerCase()
                .replace("é", "e").replace("è", "e").replace("ê", "e")
                .replace("à", "a").replace("â", "a")
                .replace("ô", "o").replace("û", "u").replace("î", "i")
                .replace("ç", "c");

        for (String word : BAD_WORDS) {
            if (textLower.contains(word.toLowerCase())) {
                return word;
            }
        }
        return "";
    }

    /**
     * Vérifie tous les champs texte d'un formulaire en une seule fois.
     */
    public static boolean containsBadWordInAny(String... texts) {
        for (String text : texts) {
            if (containsBadWord(text)) return true;
        }
        return false;
    }
}
