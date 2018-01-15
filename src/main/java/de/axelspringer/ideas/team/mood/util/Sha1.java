package de.axelspringer.ideas.team.mood.util;

import java.security.MessageDigest;

public class Sha1 {

    public static String sha1(String input) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
