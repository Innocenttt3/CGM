package org.example;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Wartosci_bazy main = new Wartosci_bazy();
        Wczytane_dane dane = main.zaczytaj_dane("/Users/kamilgolawski/CGM/CGM-priv/autoImport/planKont.xlsx");
    }
}