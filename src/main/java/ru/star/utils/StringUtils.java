package ru.star.utils;

public class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("Utility classes are not supposed to be instantiated");
    }

    public static String threeDigit(String number) {
        if (number.length() >= 3) return number;
        return threeDigit("0" + number);
    }
}
