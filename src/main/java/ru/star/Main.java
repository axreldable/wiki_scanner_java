package ru.star;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
//        String[] ar = new String[]{"Искусство", "Автомобили", "Бизнес", "Путешествия", "Религия", "Политика", "Наука", "Домашние животные", "Спорт", "Технология"};
//        Arrays.sort(ar);
//        System.out.println(Arrays.toString(ar));
//        List<String> lines = Arrays.asList("The first line", "The second line");
////        Path file = Paths.get("/directory/the-file-name.txt");
//        File tmp = new File("logs/error.log");
//        tmp.getParentFile().mkdirs();
//        tmp.createNewFile();
//
//        File file = new File("directory/the-file-name.txt");
//        if(!file.exists()) {
//            System.out.println(file.canWrite());
//            System.out.println(file.getParentFile().mkdirs());
//        }
//        Files.write(Paths.get(file.getPath()), lines, Charset.forName("UTF-8"));

        new WikiPrinter().print("Политика", 1, "");
        new WikiPrinter().print("Искусство", 1, "");
        new WikiPrinter().print("Автомобили", 1, "");
    }
}
