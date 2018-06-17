package ru.star.utils;

public class StringUtils {
    public static String threeDigit(String number) {
        if (number.length() >= 3) return number;
        return threeDigit("0" + number);
    }
}
