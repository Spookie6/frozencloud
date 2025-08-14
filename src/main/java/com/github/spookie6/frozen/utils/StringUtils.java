package com.github.spookie6.frozen.utils;

import java.util.*;
import java.util.regex.*;

public abstract class StringUtils {

    private static final String[] profile_types = {"normal", "ironman", "stranded"};

    public static HashMap<String, String> Colors = new HashMap<String, String>();

    public static String removeUnicode(String str) {
        Pattern pattern = Pattern.compile("[^\\u0000-\\u007F]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    public static String removeFormatting(String str) {
        Pattern pattern = Pattern.compile("§.", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    public static byte getProfileType(String profile_type_name) {
        return (byte) Arrays.asList(profile_types).indexOf(profile_type_name);
    }


    // https://www.geeksforgeeks.org/roman-number-to-integer/
    public static int romanToDecimal(String s) {
        HashMap<Character, Integer> romanMap = new HashMap<>();
        romanMap.put('I', 1);
        romanMap.put('V', 5);
        romanMap.put('X', 10);
        romanMap.put('L', 50);
        romanMap.put('C', 100);
        romanMap.put('D', 500);
        romanMap.put('M', 1000);

        int res = 0;
        for (int i = 0; i < s.length(); i++) {

            // If the current value is less than the next value,
            // subtract current from next and add to res
            if (i + 1 < s.length() && romanMap.get(s.charAt(i)) <
                    romanMap.get(s.charAt(i + 1))) {
                res += romanMap.get(s.charAt(i + 1)) -
                        romanMap.get(s.charAt(i));

                // Skip the next symbol
                i++;
            }
            else {

                // Otherwise, add the current value to res
                res += romanMap.get(s.charAt(i));
            }
        }

        return res;
    }

    public static String formatTime(float timestamp, boolean showMins) {
        if (timestamp == 0) return "0.00s";

        int mins = (int) (timestamp / 60);
        if (showMins && mins >= 1) {
            float secs = timestamp % 60;
            return mins + "m " + String.format("%.2f", secs) + "s";
        }
        return String.format("%.2f", timestamp) + "s";
    }
}