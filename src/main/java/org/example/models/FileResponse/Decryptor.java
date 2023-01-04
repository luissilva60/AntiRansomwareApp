package org.example.models.FileResponse;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;

public class Decryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final byte[] SECRET_KEY = "8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H@".getBytes(StandardCharsets.UTF_8);

    public static String decrypt(String encryptedData) {
        try {
            // Split the IV and encrypted data from the input
            byte[] data = Base64.getDecoder().decode(encryptedData);
            byte[] iv = Arrays.copyOfRange(data, 0, 12);
            byte[] encrypted = Arrays.copyOfRange(data, 12, data.length);

            GCMParameterSpec params = new GCMParameterSpec(128, encrypted, 0, 12);

            // Create the cipher using the '8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H@' key and the IV
            SecretKeySpec keySpec = new SecretKeySpec("8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H@".getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // Decrypt the data
            byte[] decrypted = cipher.doFinal(encrypted);

            // Convert the decrypted data to a string
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Handle the exception
            System.out.println(e);
            return null;
        }
    }

    public static String decryptAes256Gcm(String encrypted, SecretKey secretKey, byte[] iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
    }
}


