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
            if (row.getRowNum() > 0) {
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
                    obecny_numer_konta = tmp.getStringCellValue();
                    konto_syntetyczne = extract_poziom(obecny_numer_konta);
                    if (!wzory.containsKey(konto_syntetyczne)) {
                        System.out.println("brak wzoru dla konta:" + konto_syntetyczne + "we wzorach");
                    } else {
                        przemapowany_numer_konta = wypelnij_zerami(obecny_numer_konta, wzory.get(konto_syntetyczne));
                        System.out.println(przemapowany_numer_konta);
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

    public static String wypelnij_zerami(String input, int[] arr) {
        String start = extract_poziom(input);
        String result = "";
        result += start;
        String tmp_poziom;
        int i = 0;
        while (input.contains("-")) {
            input = extract_nizszy_poziom(input);
            tmp_poziom = extract_poziom(input);
            result += "-";
            int potrzebne_zera = arr[i] - tmp_poziom.length();
            if (potrzebne_zera > 0) {
                for (int j = 0; j < potrzebne_zera; j++) {
                    result += "0";
                }
            }
            result += tmp_poziom;
            i++;
        }
        return result;
    }

    public static String extract_poziom(String input) {
        int indexOfDash = input.indexOf('-');
        if (indexOfDash != -1) {
            return input.substring(0, indexOfDash);
        } else {
            return input;
        }
    }

    public static String extract_nizszy_poziom(String input) {
        int indexOfDash = input.indexOf('-');
        if (indexOfDash != -1) {
            return input.substring(indexOfDash + 1, input.length());
        } else {
            return input;
        }
    }
}
