package br.gov.inep.censo.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utilitario de senha com suporte a legado SHA-256 e hash moderno PBKDF2.
 */
public final class PasswordUtil {

    private static final String PBKDF2_PREFIX = "PBKDF2";
    private static final int PBKDF2_ITERATIONS = 12000;
    private static final int SALT_SIZE_BYTES = 16;
    private static final int HASH_SIZE_BYTES = 32;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String plainText) {
        String normalized = normalize(plainText);
        byte[] salt = new byte[SALT_SIZE_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(normalized, salt, PBKDF2_ITERATIONS, HASH_SIZE_BYTES);
        return PBKDF2_PREFIX + "$" + PBKDF2_ITERATIONS + "$" + toHex(salt) + "$" + toHex(hash);
    }

    public static boolean verifyPassword(String plainText, String storedHash) {
        if (storedHash == null || storedHash.trim().length() == 0) {
            return false;
        }
        String normalized = normalize(plainText);
        String hash = storedHash.trim();

        if (isPbkdf2Hash(hash)) {
            String[] parts = hash.split("\\$");
            if (parts.length != 4) {
                return false;
            }

            int iterations;
            try {
                iterations = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return false;
            }

            byte[] salt = fromHex(parts[2]);
            byte[] expected = fromHex(parts[3]);
            if (salt.length == 0 || expected.length == 0) {
                return false;
            }

            byte[] actual = pbkdf2(normalized, salt, iterations, expected.length);
            return constantTimeEquals(expected, actual);
        }

        // Compatibilidade com hashes antigos (SHA-256 sem salt).
        return sha256(normalized).equals(hash);
    }

    public static boolean needsRehash(String storedHash) {
        return !isPbkdf2Hash(storedHash);
    }

    public static String sha256(String plainText) {
        String normalized = normalize(plainText);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponivel.", e);
        }

        byte[] hash;
        try {
            hash = digest.digest(normalized.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 indisponivel.", e);
        }
        return toHex(hash);
    }

    private static boolean isPbkdf2Hash(String hash) {
        return hash != null && hash.startsWith(PBKDF2_PREFIX + "$");
    }

    private static String normalize(String plainText) {
        return plainText == null ? "" : plainText;
    }

    private static byte[] pbkdf2(String plainText, byte[] salt, int iterations, int outputBytes) {
        PBEKeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt, iterations, outputBytes * 8);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar hash PBKDF2.", e);
        } finally {
            spec.clearPassword();
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= (a[i] ^ b[i]);
        }
        return diff == 0;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xff;
            if (value < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(value));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String value) {
        if (value == null || value.length() == 0 || (value.length() % 2 != 0)) {
            return new byte[0];
        }
        byte[] out = new byte[value.length() / 2];
        for (int i = 0; i < value.length(); i += 2) {
            int hi = Character.digit(value.charAt(i), 16);
            int lo = Character.digit(value.charAt(i + 1), 16);
            if (hi < 0 || lo < 0) {
                return new byte[0];
            }
            out[i / 2] = (byte) ((hi << 4) + lo);
        }
        return out;
    }
}
