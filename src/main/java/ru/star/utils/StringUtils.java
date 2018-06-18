package ru.star.utils;

import static ru.star.Constants.ZERO;

public class StringUtils {
    public static String threeDigit(String number) {
        if (number.length() >= 3) return number;
        return threeDigit(ZERO + number);
    }
}
