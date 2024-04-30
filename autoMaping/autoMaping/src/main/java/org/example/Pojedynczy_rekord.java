package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;
import java.sql.SQLOutput;
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
        przygotuj_ppk(pelny_plan_kont);
        for (Row row : pierwszy_arkusz) {
            if (row.getRowNum() > 0) {
                Cell tmp = row.getCell(0);
                if (tmp != null) {
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
                            if (konto_syntetyczne.equals("201")) {
                                przemapowany_numer_konta = zastap_zero_jedynkami_dla201(przemapowany_numer_konta);
                            }
                            System.out.println(przemapowany_numer_konta);
                            Cell cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            Cell czy_zawiera = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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
            pelny_plan_kont.add(getCellValueAsString(tmp).trim());
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
        String result2 = wypelnij_zerami_przed(input, arr);
        if (result2.endsWith("-")) {
            result2 = result2.substring(0, result2.length() - 1);
        }
        if (pelny_plan_kont.contains(result2)) {
            return result2;
        }
        String result1 = wypelnij_zerami_za(input, arr);
        if (result1.endsWith("-")) {
            result1 = result1.substring(0, result1.length() - 1);
        }
        if (pelny_plan_kont.contains(result1)) {
            return result1;
        }
        String result3 = wypelnij_zerami_przed_po(input, arr);
        if (result3.endsWith("-")) {
            result3 = result3.substring(0, result3.length() - 1);
        }
        if (pelny_plan_kont.contains(result3)) {
            return result3;
        }

        return result2 + "/" + result1 + "/" + result3;
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
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(i));
                } else if (parts[i].endsWith("0")) {
                    result += parts[i].substring(0, arr.elementAt(i));
                } else if (parts[i].startsWith("0")) {
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(i));
                }
            }
        }
        if (result.endsWith("-")) {
            result = result.substring(0, result.length() - 1);
        }
        if (parts.length < arr.size()) {
            for (int i = 0; i < arr.size() - parts.length; i++) {
                result += "-";
                if (arr.size() - parts.length + i < arr.size()) {
                    for (int y = 0; y < arr.elementAt(arr.size() - parts.length + i); y++) {
                        result += "0";
                    }
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
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(i));
                } else if (parts[i].endsWith("0")) {
                    result += parts[i].substring(0, arr.elementAt(i));
                } else if (parts[i].startsWith("0")) {
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(i));
                }
            }
        }
        return result;
    }

    public String wypelnij_zerami_przed_po(String input, Vector<Integer> arr) {
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
                if ((arr.elementAt(i) - parts[i].length()) % 2 == 0) {
                    for (int x = 0; x < (arr.elementAt(i) - parts[i].length()) / 2; x++) {
                        result += "0";
                    }
                    result += parts[i];
                    for (int x = 0; x < (arr.elementAt(i) - parts[i].length()) / 2; x++) {
                        result += "0";
                    }
                } else {
                    result += parts[i];
                }
            } else if (arr.elementAt(i) < parts[i].length()) {
                result += parts[i];
                result += "-";
                if (parts[i].endsWith("00")) {
                    result += parts[i].substring(0, arr.elementAt(i));
                } else if (parts[i].startsWith("00")) {
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(i));
                } else if (parts[i].endsWith("0")) {
                    result += parts[i].substring(0, arr.elementAt(i));
                } else if (parts[i].startsWith("0")) {
                    result += parts[i].substring(parts[i].length() - arr.elementAt(i), parts[i].length() - arr.elementAt(i) + arr.elementAt(i));
                }
            }
        }
        return result;
    }

    public static void przygotuj_ppk(List<String> wzory) {
        for (String tmp : wzory) {
            tmp = removeUntil(tmp);
        }
    }

    private static String removeUntil(String input) {
        String until = "-*";
        int index = input.indexOf(until);
        if (index == -1) {
            return input;
        }
        return input.substring(index + until.length());
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


    public String zastap_zero_jedynkami_dla201(String input) {
        return input.replaceFirst("-0", "-1");
    }

    public void poprawExcela(String path, String pathToDoubleCheck) throws IOException {
        zaczytaj_pelny_plan_kont(pathToDoubleCheck);
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String cellValue = cell.getStringCellValue();
                    if (cellValue.startsWith("201")) {
                        int slashIndex = cellValue.indexOf("/");
                        if (slashIndex != -1) {
                            String newValue = cellValue.substring(0, slashIndex);
                            if (!pelny_plan_kont.contains(newValue)) {
                                System.out.println("nie dla konta " + newValue);
                            }
                            Cell newCell = row.createCell(cell.getColumnIndex() + 1);
                            newCell.setCellValue(newValue);
                        }
                    }
                }

            }
            try (FileOutputStream fos = new FileOutputStream(path)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Pojedynczy_rekord main = new Pojedynczy_rekord();
        String ppkPath = "/Users/kamilgolawski/CGM/CGM-priv/pl07/pseudoppk.xlsx";
        main.zaczytaj_wzory("/Users/kamilgolawski/CGM/CGM-priv/pl07/pl07wzory.xlsx");
        float result = main.zaczytaj_dane("/Users/kamilgolawski/CGM/CGM-priv/pl07/kontaMApl07.xlsx", ppkPath);
        System.out.println(main.pelny_plan_kont);
        System.out.println(result);


    }

}
