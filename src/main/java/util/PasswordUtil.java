package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for BCrypt password hashing and verification.
 */
public class PasswordUtil {

    /**
     * Hashes a plain-text password using BCrypt with a random salt.
     *
     * @param plainPassword the raw password entered by the user
     * @return a BCrypt hash string (60 characters)
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifies a plain-text password against a BCrypt hash.
     *
     * @param plainPassword  the raw password entered by the user
     * @param hashedPassword the BCrypt hash stored in the database
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2")) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
