package org.example;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
//        Pojedynczy_rekord main = new Pojedynczy_rekord();
//        main.zaczytaj_wzory("/Users/kamilgolawski/CGM/CGM-priv/autoMaping/wzory.xlsx");
//        for (String klucz : main.wzory.keySet()) {
//            int[] poziomy = main.wzory.get(klucz);
//            System.out.println("Klucz: " + klucz);
//            System.out.print("Wartości: ");
//            for (int value : poziomy) {
//                System.out.print(value + " ");
//            }
//            System.out.println();
//        }
        int[] arr = {2, 2, 3};
        String test = Pojedynczy_rekord.wypelnij_zerami("022-1-5-01", arr);
        System.out.println(test);
    }
}