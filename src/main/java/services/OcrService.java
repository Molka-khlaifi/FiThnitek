package services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrService {

    private static final String GEMINI_API_KEY_ENV = "GEMINI_API_KEY";
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public OcrResult scannerImage(File file, String typeDocument) throws OcrException {
        String apiKey = System.getenv(GEMINI_API_KEY_ENV);

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new MissingApiKeyException("Cl\u00e9 API Gemini manquante. Veuillez configurer GEMINI_API_KEY.");
        }

        try {
            String mimeType = getMimeType(file);
            String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            String prompt = construirePrompt(typeDocument);
            String requestBody = construireRequestBody(prompt, mimeType, base64Image);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL))
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new OcrException("Erreur Gemini OCR (" + response.statusCode() + ") : " + extraireErreur(response.body()));
            }

            String texteExtrait = extraireTexteGemini(response.body());

            if (texteExtrait == null || texteExtrait.trim().isEmpty()) {
                throw new OcrException("Gemini n'a retourn\u00e9 aucun texte exploitable.");
            }

            return new OcrResult(
                    texteExtrait,
                    detecterImmatriculation(texteExtrait),
                    detecterDateExpiration(texteExtrait),
                    formaterTypeDocument(typeDocument)
            );
        } catch (IOException e) {
            throw new OcrException("Impossible de lire ou envoyer le fichier OCR : " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OcrException("Analyse OCR interrompue.", e);
        }
    }

    private String construirePrompt(String typeDocument) {
        return "Extract useful vehicle document information from this image. " +
                "Document type selected in the app: " + formaterTypeDocument(typeDocument) + ". " +
                "Return concise structured text in French with:\\n" +
                "- Type document\\n" +
                "- Immatriculation\\n" +
                "- Date expiration\\n" +
                "- Nom propri\u00e9taire if visible\\n" +
                "- R\u00e9sum\u00e9 texte extrait\\n" +
                "If a field is not visible, return \"Non d\u00e9tect\u00e9\".";
    }

    private String construireRequestBody(String prompt, String mimeType, String base64Image) {
        return "{" +
                "\"contents\":[{" +
                "\"parts\":[" +
                "{\"text\":\"" + jsonEscape(prompt) + "\"}," +
                "{\"inline_data\":{\"mime_type\":\"" + jsonEscape(mimeType) + "\",\"data\":\"" + base64Image + "\"}}" +
                "]" +
                "}]" +
                "}";
    }

    private String getMimeType(File file) {
        String nom = file.getName().toLowerCase();

        if (nom.endsWith(".png")) {
            return "image/png";
        }

        return "image/jpeg";
    }

    private String extraireTexteGemini(String responseBody) {
        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(responseBody);

        if (matcher.find()) {
            return jsonUnescape(matcher.group(1));
        }

        return "";
    }

    private String extraireErreur(String responseBody) {
        Pattern pattern = Pattern.compile("\"message\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
        Matcher matcher = pattern.matcher(responseBody);

        if (matcher.find()) {
            return jsonUnescape(matcher.group(1));
        }

        return "appel OCR refus\u00e9 ou indisponible.";
    }

    private String detecterImmatriculation(String texte) {
        Pattern pattern = Pattern.compile("\\b[0-9]{1,4}\\s*(?:TU|تونس|TN)\\s*[0-9]{1,4}\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texte);

        if (matcher.find()) {
            return matcher.group().trim();
        }

        pattern = Pattern.compile("(?i)immatriculation\\s*[:\\-]?\\s*([^\\n\\r]+)");
        matcher = pattern.matcher(texte);

        if (matcher.find()) {
            return nettoyerValeur(matcher.group(1));
        }

        return "Non d\u00e9tect\u00e9";
    }

    private String detecterDateExpiration(String texte) {
        Pattern labeledPattern = Pattern.compile("(?i)(date\\s+d[' ]?expiration|expiration|valable\\s+jusqu[' ]?au)\\s*[:\\-]?\\s*([^\\n\\r]+)");
        Matcher labeledMatcher = labeledPattern.matcher(texte);

        if (labeledMatcher.find()) {
            return nettoyerValeur(labeledMatcher.group(2));
        }

        Pattern datePattern = Pattern.compile("\\b\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}\\b|\\b\\d{4}-\\d{1,2}-\\d{1,2}\\b");
        Matcher dateMatcher = datePattern.matcher(texte);

        if (dateMatcher.find()) {
            return dateMatcher.group().trim();
        }

        return "Non d\u00e9tect\u00e9";
    }

    private String nettoyerValeur(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return "Non d\u00e9tect\u00e9";
        }

        String nettoyee = valeur.trim();
        int tiretIndex = nettoyee.indexOf("-");

        if (tiretIndex > 0) {
            nettoyee = nettoyee.substring(0, tiretIndex).trim();
        }

        return nettoyee.isEmpty() ? "Non d\u00e9tect\u00e9" : nettoyee;
    }

    private String formaterTypeDocument(String typeDocument) {
        if (typeDocument == null || typeDocument.trim().isEmpty()) {
            return "Non d\u00e9tect\u00e9";
        }

        switch (typeDocument) {
            case "CARTE_GRISE":
                return "Carte grise";
            case "ASSURANCE":
                return "Assurance";
            case "VISITE_TECHNIQUE":
                return "Visite technique";
            case "VIGNETTE":
                return "Vignette";
            case "AUTRE":
                return "Autre document";
            default:
                return typeDocument;
        }
    }

    private String jsonEscape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String jsonUnescape(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);

            if (escaping) {
                switch (current) {
                    case 'u':
                        if (i + 4 < value.length()) {
                            String hex = value.substring(i + 1, i + 5);
                            try {
                                builder.append((char) Integer.parseInt(hex, 16));
                                i += 4;
                            } catch (NumberFormatException e) {
                                builder.append("\\u").append(hex);
                                i += 4;
                            }
                        } else {
                            builder.append('u');
                        }
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case '"':
                        builder.append('"');
                        break;
                    case '\\':
                        builder.append('\\');
                        break;
                    default:
                        builder.append(current);
                        break;
                }
                escaping = false;
            } else if (current == '\\') {
                escaping = true;
            } else {
                builder.append(current);
            }
        }

        return builder.toString();
    }

    public static class OcrResult {
        private final String texteBrut;
        private final String immatriculation;
        private final String dateExpiration;
        private final String typeDocument;

        public OcrResult(String texteBrut, String immatriculation, String dateExpiration, String typeDocument) {
            this.texteBrut = texteBrut;
            this.immatriculation = immatriculation;
            this.dateExpiration = dateExpiration;
            this.typeDocument = typeDocument;
        }

        public String toDisplayText() {
            return "Type document : " + typeDocument + "\n" +
                    "Immatriculation : " + immatriculation + "\n" +
                    "Date expiration : " + dateExpiration + "\n\n" +
                    "Texte extrait :\n" + texteBrut;
        }
    }

    public static class OcrException extends Exception {
        public OcrException(String message) {
            super(message);
        }

        public OcrException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class MissingApiKeyException extends OcrException {
        public MissingApiKeyException(String message) {
            super(message);
        }
    }
}
