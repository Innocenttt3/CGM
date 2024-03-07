package org.example;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        Pojedynczy_rekord main = new Pojedynczy_rekord();
        main.zaczytaj_wzory("/Users/kamilgolawski/CGM/CGM-priv/autoMaping/wzory.xlsx");
        System.out.print(Arrays.toString(main.wzory.get("010")));
        main.zaczytaj_dane("/Users/kamilgolawski/CGM/CGM-priv/autoMaping/Konta_PL07.xlsx");

    }
}