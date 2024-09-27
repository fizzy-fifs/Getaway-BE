package com.example.holidayplanner.helpers;

public class Helper {

    public static String toSentenceCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char[] chars = str.toLowerCase().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            chars[0] = Character.toUpperCase(chars[0]);
            if (Character.isWhitespace(chars[i])) {
                chars[i + 1] = Character.toUpperCase(chars[i + 1]);
            }
        }
        return String.valueOf(chars);
    }
}
