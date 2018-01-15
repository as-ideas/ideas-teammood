package de.axelspringer.ideas.team.mood.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Arrays;

public class SymetricEncryption {

    private static final String ALGORITHM = "AES";

    private static final String password = "mypasswordforeverything";

    public static String encryptAndBase58(String valueToEncrypt) {
        String encryptedString = encrypt(valueToEncrypt);
        return Base58.encode(encryptedString.getBytes());
    }


    public static String decryptAndBase58(String encryptedValueInBase58) {
        try {
            String encryptedValue = new String(Base58.decode(encryptedValueInBase58), "UTF-8");
            return decrypt(encryptedValue);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static String encrypt(String valueToEncrypt) {
        try {
            String encryptedVal = null;

            final Key key = generateKeyFromString(password);
            final Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            final byte[] encValue = c.doFinal(valueToEncrypt.getBytes());
            encryptedVal = new BASE64Encoder().encode(encValue);

            return encode(encryptedVal);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static String decrypt(String encryptedValue) {
        try {
            encryptedValue = decode(encryptedValue);

            String decryptedValue = null;

            final Key key = generateKeyFromString(password);
            final Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            final byte[] decorVal = new BASE64Decoder().decodeBuffer(encryptedValue);
            final byte[] decValue = c.doFinal(decorVal);
            decryptedValue = new String(decValue);

            return decryptedValue;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static String encode(String value) {
        return value.replaceAll("\\+", "-").replaceAll("/", "_");
    }

    private static String decode(String value) {
        return value.replaceAll("-", "\\+").replaceAll("_", "/");
    }

    private static Key generateKeyFromString(final String secKey) throws Exception {
        byte[] key = new BASE64Decoder().decodeBuffer(Sha1.sha1(secKey));
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        return new SecretKeySpec(key, ALGORITHM);
    }
}
