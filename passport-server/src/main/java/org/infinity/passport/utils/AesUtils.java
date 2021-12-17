package org.infinity.passport.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A simple utility class for easily encrypting and decrypting data using the AES algorithm.
 */
@Slf4j
public class AesUtils {

    private static final String KEY_ALGORITHM            = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * The method that will encrypt data
     *
     * @param rawText  The raw text to encrypt
     * @param password The password that will be the {@link SecretKey}
     * @return the encrypted data encoded by Base64
     */
    public static String encrypt(String rawText, String password) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = rawText.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
            byte[] result = cipher.doFinal(byteContent);
            return Base64Utils.encodeToString(result);
        } catch (Exception ex) {
            log.error("Failed to encrypt!", ex);
        }
        return null;
    }

    /**
     * The method that will decrypt a piece of encrypted data
     *
     * @param encryptedData The encrypted data
     * @param password      The password that will be the {@link SecretKey}
     * @return raw text
     */
    public static String decrypt(String encryptedData, String password) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
            byte[] result = cipher.doFinal(Base64Utils.decodeFromString(encryptedData));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Failed to decrypt!", ex);
        }
        return null;
    }

    /**
     * The method that will generate a random {@link SecretKey}
     *
     * @param password The password that will be the {@link SecretKey}
     * @return the key
     */
    private static SecretKey getSecretKey(String password) {
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            // use only first 128 bit
            kg.init(128, random);
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            log.error("Failed to get secret key!", ex);
        }
        return null;
    }
}