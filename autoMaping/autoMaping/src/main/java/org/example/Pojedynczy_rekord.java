package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pojedynczy_rekord {

    private String obecny_numer_konta;
    private String przemapowany_numer_konta;
    private String konto_syntetyczne;
    public HashMap<String, int[]> wzory;
    public List<String> pelny_plan_kont;

    public Pojedynczy_rekord() {
        wzory = new HashMap<>();
        pelny_plan_kont = new ArrayList<>();
    }

    public void zaczytaj_wzory(String sciezka_do_pliku) throws IOException {
        FileInputStream plik_wejsciowy = new FileInputStream(sciezka_do_pliku);
        XSSFWorkbook plik_wejsciowy_excel = new XSSFWorkbook(plik_wejsciowy);
        Sheet pierwszy_arkusz = plik_wejsciowy_excel.getSheetAt(0);

        for (Row row : pierwszy_arkusz) {
            if (row.getRowNum() > 0) {
                Cell komorka_klucz = row.getCell(0);
                String klucz = getCellValueAsString(komorka_klucz);
                klucz = klucz.trim();

                Cell komorka_poziomy = row.getCell(1);
                String poziomy = getCellValueAsString(komorka_poziomy);
                poziomy = poziomy.trim();

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

    public void zaczytaj_dane(String sciezka_do_pliku, String sciezka_do_planu_kont) throws IOException {
        FileInputStream plik_wejsciowy = new FileInputStream((sciezka_do_pliku));
        XSSFWorkbook plik_wejsciowy_excel = new XSSFWorkbook(plik_wejsciowy);
        Sheet pierwszy_arkusz = plik_wejsciowy_excel.getSheetAt(0);
        zaczytaj_pelny_plan_kont(sciezka_do_planu_kont);
        for (Row row : pierwszy_arkusz) {
            if (row.getRowNum() > 0) {
                Cell tmp = row.getCell(0);
                if (tmp.getCellType() == CellType.NUMERIC) {
                    przemapowany_numer_konta = String.valueOf(tmp.getNumericCellValue());
                    przemapowany_numer_konta = przemapowany_numer_konta.trim();
                } else if (tmp.getCellType() == CellType.STRING) {
                    obecny_numer_konta = tmp.getStringCellValue();
                    obecny_numer_konta = obecny_numer_konta.trim();
                    konto_syntetyczne = extract_poziom(obecny_numer_konta);
                    if (!wzory.containsKey(konto_syntetyczne)) {
                        System.out.println("brak wzoru dla konta:" + konto_syntetyczne + "we wzorach");
                    } else {
                        przemapowany_numer_konta = wypelnij_zerami(obecny_numer_konta, wzory.get(konto_syntetyczne));
                        przemapowany_numer_konta = przemapowany_numer_konta.trim();
                        System.out.println(przemapowany_numer_konta);
                        Cell cell = row.createCell(1);
                        cell.setCellValue(przemapowany_numer_konta);
                        if (pelny_plan_kont.contains(przemapowany_numer_konta)) {
                            System.out.println("zawiera");
                        }
                    }
                }
            }
        }
        plik_wejsciowy_excel.write(new FileOutputStream(new File(sciezka_do_pliku)));
        plik_wejsciowy_excel.close();
    }


    private void zaczytaj_pelny_plan_kont(String sciezka_do_pliku) throws IOException {
        FileInputStream plik_wejsciowy = new FileInputStream((sciezka_do_pliku));
        XSSFWorkbook plik_wejsciowy_excel = new XSSFWorkbook(plik_wejsciowy);
        Sheet pierwszy_arkusz = plik_wejsciowy_excel.getSheetAt(0);
        for (Row row : pierwszy_arkusz) {
            Cell tmp = row.getCell(2);
            pelny_plan_kont.add(tmp.getStringCellValue().trim());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue()).trim();
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
            } else if (potrzebne_zera < 0) {
                tmp_poziom = tmp_poziom.substring(0, tmp_poziom.length() + potrzebne_zera);
            }
            result += tmp_poziom;
            i++;
        }
        if(result.endsWith("-")){
            result = result.substring(0, result.length()-1);
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

    public static void main(String[] args) throws IOException {
        Pojedynczy_rekord main = new Pojedynczy_rekord();
        main.zaczytaj_wzory("/Users/kamilgolawski/CGM/CGM-priv/autoMaping/wzory_14.xlsx");
        main.zaczytaj_dane("/Users/kamilgolawski/CGM/CGM-priv/autoMaping/Konta_PL14.xlsx",
                "/Users/kamilgolawski/CGM/CGM-priv/autoMaping/PL14_pelny_plan_kont_2024.xlsx");
    }
}
