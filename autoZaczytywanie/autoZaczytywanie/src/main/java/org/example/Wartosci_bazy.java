package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Wartosci_bazy {

    public Integer ilosc_poziomow_analityki;
    public String kod_roku;
    public Integer obecny_poziom;
    public String nr_konta;
    public String sub_konto;
    public String prekonto;
    public String opis;
    public String indywidualna_analityka = "111";
    public String gotowy_rekord;


    private String extract_sub_konto(String inputString, int n) {
        String result = "";
        char character = '-';
        int lastPosition = 0;
        if (n == 0) {
            for (int i = 0; i < inputString.length(); i++) {
                if (inputString.charAt(i) == character) {
                    lastPosition = i;
                    break;
                }
            }
            if (lastPosition == 0) {
                return inputString;
            } else {
                result = inputString.substring(0, lastPosition);
                return result;
            }
        } else if (obecny_poziom.equals(ilosc_poziomow_analityki)) {
            for (int i = 0; i < inputString.length(); i++) {
                if (inputString.charAt(i) == character) {
                    lastPosition = i;
                }
            }
            result = inputString.substring(lastPosition + 1);
            if (result.length() < 2) {
                String tmpResult = result;
                result = "0" + tmpResult;
            }
            return result;
        } else {
            int amount = 0;
            Vector<Integer> positions = new Vector<>();
            for (int i = 0; i < inputString.length(); i++) {
                if (inputString.charAt(i) == character) {
                    positions.add(i);
                    amount++;
                    if (amount == n + 1) {
                        int lastIndex = positions.lastElement();
                        int startIndex = positions.get(positions.size() - 2) + 1;
                        result = inputString.substring(startIndex, lastIndex);
                        if (result.length() < 2) {
                            String tmpResult = result;
                            result = "0" + tmpResult;
                        }
                        return result;
                    }
                }
            }

        }
        return "bledny format konta";
    }

    private String extract_prekonto(String inputString, int n) {
        int amount = 0;
        String result = "";
        Vector<Integer> positions = new Vector<>();
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.charAt(i) == '-') {
                positions.add(i);
                amount++;
                if (amount == n + 1) {
                    break;
                } else if (amount == 0) {
                    return "pusto";
                }
            }
        }
        if (amount > 1) {
            while (!positions.isEmpty()) {
                if (positions.size() > 1) {
                    int startIndex = positions.get(positions.size() - 2) + 1;
                    int endIndex = positions.lastElement();
                    String addToResult = inputString.substring(startIndex, endIndex);
                    result = addToResult + "     " + result;
                    positions.remove(positions.size() - 1);
                } else {
                    break;
                }
            }
        }
        return result;
    }


    private String zwroc_nr_konta(String inputString) {
        int index = inputString.indexOf('-');
        if (index == -1) {
            return inputString;
        } else {
            return inputString.substring(0, index);
        }
    }


    private int oblicz_poziom(String inputString) {
        char targetChar = '-';
        int count = 0;
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.charAt(i) == targetChar) {
                count++;
            }
        }
        return count;
    }

    public Pair<String, Boolean> utnij_gwiazdki(String inputString) {
        int index = inputString.indexOf('*');
        boolean containsAsterisk = index != -1;
        String modifiedString = containsAsterisk ? inputString.substring(0, index) : inputString;
        return new Pair<>(modifiedString, containsAsterisk);
    }

    public void zaczytaj_dane(String sciezka_do_pliku, String sciezka_do_pliku_csv) throws IOException {
        FileInputStream plik_wejsciowy = new FileInputStream((sciezka_do_pliku));
        XSSFWorkbook plik_wejsciowy_excel = new XSSFWorkbook(plik_wejsciowy);
        Sheet pierwszy_arkusz = plik_wejsciowy_excel.getSheetAt(0);
        try (PrintWriter writer = new PrintWriter(new File(sciezka_do_pliku_csv))) {
            writer.println("KOD_ROKU;POZIOM;NR_KONTA;SUBKONTO;PREKONTO;INDYWIDUALNYPOZIOMANALITYKI;OPIS");
            for (Row row : pierwszy_arkusz) {
                if (row.getRowNum() > 1) {
                    Cell symbol_konta = row.getCell(0);
                    Cell nazwa = row.getCell(1);
                    Cell kod_roku_excel = row.getCell(2);
                    String tmp = "";
                    if (symbol_konta.getCellType() == CellType.NUMERIC) {
                        double tmpNumeric = symbol_konta.getNumericCellValue();
                        int tmpInt = (int) tmpNumeric;
                        tmp = String.valueOf(tmpInt);
                    } else if (symbol_konta.getCellType() == CellType.STRING) {
                        tmp = symbol_konta.getStringCellValue();
                    }
                    String rowId = row.getRowNum() + 1 + "";
                    String tmp2 = nazwa.getStringCellValue();

                    //cala linijka wejsciowa jest juz odczytana czas na jej przetworzenie

                    kod_roku = kod_roku_excel.getStringCellValue();
                    ilosc_poziomow_analityki = oblicz_poziom(tmp);
                    Pair<String, Boolean> result = utnij_gwiazdki(tmp);
                    if (result.getSecond()) {
                        obecny_poziom = oblicz_poziom(result.getFirst()) - 1;
                    } else {
                        obecny_poziom = oblicz_poziom(result.getFirst());
                    }
                    nr_konta = zwroc_nr_konta(tmp);
                    sub_konto = extract_sub_konto(tmp, obecny_poziom);
                    prekonto = extract_prekonto(tmp, obecny_poziom);
                    opis = tmp2;
                    gotowy_rekord = kod_roku + ";" + obecny_poziom.toString() + ";" + nr_konta + ";" + sub_konto + ";" + prekonto + ";" + indywidualna_analityka + ";" + opis;
                    writer.println(gotowy_rekord);
                }
            }
        }
    }
}
