package com.mycompany.appinventario.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordService {

    private static final String SHA256_PREFIX = "sha256:";

    private PasswordService() {
    }

    public static String hash(String plainPassword) {
        return SHA256_PREFIX + sha256Hex(plainPassword);
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        if (storedPassword.startsWith(SHA256_PREFIX)) {
            String hashEsperado = SHA256_PREFIX + sha256Hex(rawPassword);
            return hashEsperado.equals(storedPassword);
        }

        // Compatibilidad con datos existentes en texto plano (por ejemplo: 123456).
        return rawPassword.equals(storedPassword);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible en el entorno Java", ex);
        }
    }
}
