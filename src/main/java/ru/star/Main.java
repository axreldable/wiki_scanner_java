package ru.star;

import ru.star.csv.CsvWorker;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String[] arr = new String[]{"Политика", "Искусство", "Автомобили"};
        Arrays.sort(arr);
        for (int i = 1; i <= arr.length; i++) {
            new WikiPrinter().print("", "0" + i, arr[i-1]);
        }

        CsvWorker.printArticles("file.csv");
    }
}
