package com.umair.weathercli;

/**
 * Extremely tiny JSON "extractor" for specific simple fields.
 * This is NOT a full JSON parser; it's just enough for the fields we use.
 * For production, prefer a real JSON library (e.g., Gson/Jackson).
 */
public class JsonMini {

    /** Find a string value for a given key (e.g., "\"name\"") inside the JSON fragment. */
    public static String findString(String json, String key) {
        int i = json.indexOf(key);
        if (i < 0) return null;
        i = json.indexOf(':', i);
        if (i < 0) return null;
        // Find the first quote after the colon
        int q1 = json.indexOf('"', i + 1);
        if (q1 < 0) return null;
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) return null;
        return json.substring(q1 + 1, q2);
    }

    /** Find a number (double) value for a given key. */
    public static Double findNumber(String json, String key) {
        int i = json.indexOf(key);
        if (i < 0) return null;
        i = json.indexOf(':', i);
        if (i < 0) return null;
        int start = i + 1;
        // Skip whitespace
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if ((c >= '0' && c <= '9') || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E') {
                end++;
            } else break;
        }
        try {
            return Double.parseDouble(json.substring(start, end));
        } catch (Exception e) {
            return null;
        }
    }

    /** Find an integer value for a given key. */
    public static Integer findInt(String json, String key) {
        Double d = findNumber(json, key);
        if (d == null) return null;
        return (int)Math.round(d);
    }

    /** Find a JSON object value for a given key and return the substring of that object. */
    public static String findObject(String json, String key) {
        int i = json.indexOf(key);
        if (i < 0) return null;
        i = json.indexOf('{', i);
        if (i < 0) return null;
        int depth = 0;
        for (int j = i; j < json.length(); j++) {
            char c = json.charAt(j);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) {
                    return json.substring(i, j + 1);
                }
            }
        }
        return null;
    }

    /** Returns the first object from an array with a given key (e.g., "results"). */
    public static String firstObjectFromArray(String json, String arrayKey) {
        int k = json.indexOf(arrayKey);
        if (k < 0) return null;
        int arrStart = json.indexOf('[', k);
        if (arrStart < 0) return null;
        int firstObj = json.indexOf('{', arrStart);
        if (firstObj < 0) return null;
        int depth = 0;
        for (int j = firstObj; j < json.length(); j++) {
            char c = json.charAt(j);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) {
                    return json.substring(firstObj, j + 1);
                }
            }
        }
        return null;
    }
}
