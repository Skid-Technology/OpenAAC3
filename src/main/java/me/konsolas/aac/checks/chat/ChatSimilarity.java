package me.konsolas.aac.checks.chat;

public class ChatSimilarity {
    public static boolean isSimilar(String str, String str2) {
        if (str.equalsIgnoreCase(str2)) {
            return true;
        }
        if (str.length() < 3 || str2.length() < 3) {
            return false;
        }
        if (str.toLowerCase().replaceAll("[^a-z]", "").equals("") || str2.toLowerCase().replaceAll("[^a-z]", "").equals("")) {
            return false;
        }
        return str.toLowerCase().replaceAll("[^a-z]", "").equals(str2.toLowerCase().replaceAll("[^a-z]", ""));
    }
}
