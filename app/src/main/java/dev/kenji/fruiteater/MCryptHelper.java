package dev.kenji.fruiteater;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MCryptHelper {

    private MCryptHelper() {
        // Private constructor to prevent instantiation
    }

    private static final String METHOD = "AES/CBC/PKCS5Padding";
    private static final String IV = "fedcba9876543210";

    public static String encrypt(String message, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");

            // Generate a random IV
            byte[] ivBytes = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(ivBytes);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(message.getBytes());

            // Combine IV and encrypted data into a single byte array
            byte[] combinedBytes = new byte[ivBytes.length + encryptedBytes.length];
            System.arraycopy(ivBytes, 0, combinedBytes, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, combinedBytes, ivBytes.length, encryptedBytes.length);

            return java.util.Base64.getEncoder().encodeToString(combinedBytes);
        } catch (Exception e) {
            Log.e("MCrypt:Error", "Encryption failed", e);
            return ""; // or throw an exception
        }
    }

    public static String decrypt(String message, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message));

            byte[] trimmedBytes = new byte[decryptedBytes.length - 16];
            System.arraycopy(decryptedBytes, 16, trimmedBytes, 0, trimmedBytes.length);

            return new String(trimmedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("MCrypt:Error", "Decryption failed", e);
            return ""; // or throw an exception
        }
    }

    private static MCryptHelper instance;

    public static MCryptHelper getInstance() {
        if (instance == null) {
            instance = new MCryptHelper();
        }
        return instance;
    }

}
