package org.example.models.FileResponse;



import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;


public class Decryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final byte[] SECRET_KEY = "8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H@".getBytes(StandardCharsets.UTF_8);

    public static String decrypt(String encryptedData) throws InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        // Decode the base64-encoded string to a byte array
        byte[] decoded = Base64.getDecoder().decode(encryptedData);

        // Extract the initialization vector (IV) from the decoded byte array
        byte[] iv = new byte[12];
        System.arraycopy(decoded, 0, iv, 0, 12);

        // Extract the encrypted data from the decoded byte array
        byte[] encrypted = new byte[decoded.length - 12];
        System.arraycopy(decoded, 12, encrypted, 0, encrypted.length);

        // Create the cipher using the '8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H@' key and the IV
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey key = new SecretKeySpec("8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H@".getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

        // Decrypt the data
        byte[] decrypted = cipher.doFinal(encrypted);

        // Convert the decrypted data to a string
        String decryptedData = new String(decrypted, StandardCharsets.UTF_8);

        System.out.println("Decrypted data: " + decryptedData);
        return decryptedData;
    }

    public static String decryptAes256Gcm(String encryptedData) throws GeneralSecurityException {

        byte[] data = Base64.getDecoder().decode(encryptedData);
        byte[] iv = Arrays.copyOfRange(data, 0, 16);
        SecretKeySpec keySpec = new SecretKeySpec("8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H@".getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }
}


