package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class Pojedynczy_rekord {

    private String obecny_numer_konta;
    private String przemapowany_numer_konta;
    private String konto_syntetyczne;
    public HashMap<String, Vector<Integer>> wzory;
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

                Vector<Integer> nowe_poziomy = new Vector<>();
                for (char pojedyncza_literka : poziomy.toCharArray()) {
                    int tmp = Character.getNumericValue(pojedyncza_literka);
                    if (tmp != 0) {
                        nowe_poziomy.add(Character.getNumericValue(pojedyncza_literka));
                    }
                }
                wzory.put(klucz, nowe_poziomy);
            }
        }
        plik_wejsciowy_excel.close();
        plik_wejsciowy.close();
    }

    public float zaczytaj_dane(String sciezka_do_pliku, String sciezka_do_planu_kont) throws IOException {
        int counter = 0;

        FileInputStream plik_wejsciowy = new FileInputStream((sciezka_do_pliku));
        XSSFWorkbook plik_wejsciowy_excel = new XSSFWorkbook(plik_wejsciowy);
        Sheet pierwszy_arkusz = plik_wejsciowy_excel.getSheetAt(0);
        int totalRows = pierwszy_arkusz.getPhysicalNumberOfRows();
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
                        Cell czy_zawiera = row.createCell(2);
                        cell.setCellValue(przemapowany_numer_konta);
                        if (pelny_plan_kont.contains(przemapowany_numer_konta)) {
                            System.out.println("zawiera");
                            counter++;
                            czy_zawiera.setCellValue("TAK");
                        } else {
                            czy_zawiera.setCellValue("NIE");
                        }
                    }
                }
            }
        }
        plik_wejsciowy_excel.write(new FileOutputStream(new File(sciezka_do_pliku)));
        plik_wejsciowy_excel.close();
        return (float) counter / totalRows;
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

    public String wypelnij_zerami(String input, Vector<Integer> arr) {
        String result1 = wypelnij_zerami_za(input, arr);
        String result2 = wypelnij_zerami_przed(input, arr);
        if (pelny_plan_kont.contains(result1)) {
            return result1;
        } else if (pelny_plan_kont.contains(result2)) {
            return result2;
        }
        return input;
    }

    public static String wypelnij_zerami_za(String input, Vector<Integer> arr) {
        String konto_sys = extract_poziom(input);
        String result = "";
        result += konto_sys;
        input = extract_nizszy_poziom(input);
        String[] parts = input.split("-");
        for (int i = 0; i < parts.length && i < arr.size(); i++) {
            if (arr.elementAt(i) == parts[i].length()) {
                result += "-" + parts[i];
            } else if (arr.elementAt(i) > parts[i].length()) {
                result += "-";
                result += parts[i];
                for (int x = 0; x < arr.elementAt(i) - parts[i].length(); x++) {
                    result += "0";
                }
            } else if (arr.elementAt(i) < parts[i].length()) {
                result += "-";
                if (parts[i].endsWith("00")) {
                    result += parts[i].substring(0, arr.elementAt(i));
                } else if (parts[i].startsWith("00")) {
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(1));
                } else if (parts[i].endsWith("0")) {
                    result += parts[i].substring(0, arr.elementAt(i));
                } else if (parts[i].startsWith("0")) {
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(1));
                }
            }
        }
        if (result.endsWith("-")) {
            result = result.substring(0, result.length() - 1);
        }
        if (parts.length < arr.size()) {
            for (int i = 0; i < arr.size() - parts.length; i++) {
                result += "-";
                for (int y = 0; y < arr.elementAt(arr.size() - parts.length + i); y++) {
                    result += "0";
                }
            }
        }
        return result;
    }

    public static String wypelnij_zerami_przed(String input, Vector<Integer> arr) {
        String konto_sys = extract_poziom(input);
        String result = "";
        result += konto_sys;
        input = extract_nizszy_poziom(input);
        String[] parts = input.split("-");
        for (int i = 0; i < parts.length && i < arr.size(); i++) {
            if (arr.elementAt(i) == parts[i].length()) {
                result += "-" + parts[i];
            } else if (arr.elementAt(i) > parts[i].length()) {
                result += "-";
                for (int x = 0; x < arr.elementAt(i) - parts[i].length(); x++) {
                    result += "0";
                }
                result += parts[i];
            } else if (arr.elementAt(i) < parts[i].length()) {
                result += "-";
                if (parts[i].endsWith("00")) {
                    result += parts[i].substring(0, arr.elementAt(i));
                } else if (parts[i].startsWith("00")) {
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(1));
                } else if (parts[i].endsWith("0")) {
                    result += parts[i].substring(0, arr.elementAt(i));
                } else if (parts[i].startsWith("0")) {
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(1));
                }
            }
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
            return input.substring(indexOfDash + 1);
        } else {
            return input;
        }
    }

}
