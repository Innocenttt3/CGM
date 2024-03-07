package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class Pojedynczy_rekord {

    private String obecny_numer_konta;
    private String przemapowany_numer_konta;
    private String konto_syntetyczne;
    public HashMap<String, int[]> wzory;

    public Pojedynczy_rekord() {
        wzory = new HashMap<>();
    }

    public void zaczytaj_wzory(String sciezka_do_pliku) throws IOException {
        FileInputStream plik_wejsciowy = new FileInputStream(sciezka_do_pliku);
        XSSFWorkbook plik_wejsciowy_excel = new XSSFWorkbook(plik_wejsciowy);
        Sheet pierwszy_arkusz = plik_wejsciowy_excel.getSheetAt(0);

        for (Row row : pierwszy_arkusz) {
            if (row.getRowNum() > 1) {
                Cell komorka_klucz = row.getCell(0);
                String klucz = getCellValueAsString(komorka_klucz);

                Cell komorka_poziomy = row.getCell(1);
                String poziomy = getCellValueAsString(komorka_poziomy);

                int[] nowe_poziomy = new int[9];
                int i = 0;
                for (char pojedyncza_literka : poziomy.toCharArray()) {
                    nowe_poziomy[i] = Character.getNumericValue(pojedyncza_literka);
                    i++;
                }
                wzory.put(klucz, nowe_poziomy);
            }
        }
        plik_wejsciowy_excel.close();
        plik_wejsciowy.close();
    }

    public void zaczytaj_dane(String sciezka_do_pliku) throws IOException {
        FileInputStream plik_wejsciowy = new FileInputStream((sciezka_do_pliku));
        XSSFWorkbook plik_wejsciowy_excel = new XSSFWorkbook(plik_wejsciowy);
        Sheet pierwszy_arkusz = plik_wejsciowy_excel.getSheetAt(0);
        for (Row row : pierwszy_arkusz) {
            if (row.getRowNum() > 1) {
                Cell tmp = row.getCell(0);
                if (tmp.getCellType() == CellType.NUMERIC) {
                    przemapowany_numer_konta = String.valueOf(tmp.getNumericCellValue());
                } else if (tmp.getCellType() == CellType.STRING) {
                    konto_syntetyczne = (String.valueOf(tmp.getCellType()));
                    if (!wzory.containsKey(konto_syntetyczne)) {
                        System.out.println("brak wzoru dla konta:" + konto_syntetyczne);
                    } else {
                        wzory.get(konto_syntetyczne);
                    }
                }
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            System.out.println("Nieobsługiwany typ komórki");
            return "";
        }
    }
    private String wypelnij_zerami(String input, int[] arr){
        String result = "";
        input = extract_poziom(input);
        while() //uzyc polimorfizumu do extract poziom

    }

    public static String extract_poziom(String input) {
        int indexOfDash = input.indexOf('-');
        if (indexOfDash != -1) {
            return input.substring(0, indexOfDash);
        } else {
            return input;
        }
    }
}
