package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Pojedynczy_rekord {
    public int ilosc_poziomow_analityki;
    public char[] kod_roku = new char[2];
    public int obecny_poziom;
    public String nr_konta;
    public String sub_konto;
    public String prekonto;
    public String opis;
    public char[] indywidualna_analityka = new char[ilosc_poziomow_analityki];

    private String extract_sub_konto(String inputString, int n) {
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
                return inputString.substring(0, lastPosition);
            }
        } else if (obecny_poziom == ilosc_poziomow_analityki) {
            for (int i = 0; i < inputString.length(); i++) {
                if (inputString.charAt(i) == character) {
                    lastPosition = i;
                }
            }
            return inputString.substring(lastPosition + 1);
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
                        return inputString.substring(startIndex, lastIndex);
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
        if (amount > 1){
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

    public void zaczytaj_dane(String sciezka_do_pliku) throws IOException {
        FileInputStream plik_wejsciowy = new FileInputStream((sciezka_do_pliku));
        XSSFWorkbook plik_wejsciowy_excel = new XSSFWorkbook(plik_wejsciowy);
        Sheet pierwszy_arkusz = plik_wejsciowy_excel.getSheetAt(0);
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

                kod_roku = kod_roku_excel.getStringCellValue().toCharArray();
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

            }

        }
    }

}
