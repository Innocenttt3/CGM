package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Pojedynczy_rekord test = new Pojedynczy_rekord();
        test.zaczytaj_dane("/Users/kamilgolawski/CGM/CGM-priv/autoImport/planKont.xlsx");
    }
}