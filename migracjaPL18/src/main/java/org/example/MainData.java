package org.example;

import jxl.write.Blank;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class MainData {

    public String nazwaPelna;
    public String nrInwenatrzowy;
    public int typ; //zapytac ocb
    public String dataPrzyjecia;
    public String dataNabycia;
    public String dokNabycia;
    public String groupaKST;
    public String nrSeryjny;
    public String kodPracownika;
    public String charakterystyka;
    public String dostawca;
    public String miejsceUzytkowania;
    HashMap<String, String> kodyPracownikow = new HashMap<>();
    HashMap<String, String> kodyMiejsc = new HashMap<>();

    MainData() {
        try {
            loadPersonnelCodes();
            loadWorkSpaceCodes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainData(String nazwaPelna, String dataPrzyjecia, int typ, String nrInwenatrzowy, String dataNabycia, String dokNabycia, String groupaKST, String nrSeryjny, String kodPracownika, String charakterystyka, String dostawca, String miejsceUzytkowania) {
        this.nazwaPelna = nazwaPelna;
        this.dataPrzyjecia = dataPrzyjecia;
        this.typ = typ;
        this.nrInwenatrzowy = nrInwenatrzowy;
        this.dataNabycia = dataNabycia;
        this.dokNabycia = dokNabycia;
        this.groupaKST = groupaKST;
        this.nrSeryjny = nrSeryjny;
        this.kodPracownika = kodPracownika;
        this.charakterystyka = charakterystyka;
        this.dostawca = dostawca;
        this.miejsceUzytkowania = miejsceUzytkowania;
    }

    public void loadPersonnelCodes() throws IOException {
        FileInputStream kodyPracownikowkSourceFile = new FileInputStream("/Users/kamilgolawski/CGM/dane/Słownik Osób Odpowiedzialnych.xlsx");
        XSSFWorkbook kodPracownikowWorkbook = new XSSFWorkbook(kodyPracownikowkSourceFile);
        Sheet firstSheetFromkodyPracownikow = kodPracownikowWorkbook.getSheetAt(0);
        String fullName = "";
        String name = "";
        int dashIndex = -1;
        for (Row row : firstSheetFromkodyPracownikow) {
            if (row.getRowNum() > 0) {
                Cell kod = row.getCell(0);
                Cell nazwa = row.getCell(1);
                if (kod != null && nazwa != null && !kod.toString().isEmpty() && !nazwa.toString().isEmpty()) {
                    fullName = nazwa.toString();
                    dashIndex = fullName.indexOf("-");
                    if (dashIndex != -1) {
                        name = fullName.substring(0, dashIndex);
                    } else {
                        name = nazwa.toString();
                    }
                    kodyPracownikow.put(name, kod.toString());
                }
            }
        }
    }

    public void loadWorkSpaceCodes() throws IOException {
        FileInputStream WorkSpaceCodesSourceFile = new FileInputStream("/Users/kamilgolawski/CGM/dane/Skróty.xlsx");
        XSSFWorkbook WorkSpaceCodesWorkbook = new XSSFWorkbook(WorkSpaceCodesSourceFile);
        Sheet firstSheetFromWorkSpaceCodes = WorkSpaceCodesWorkbook.getSheetAt(0);
        String code = "";
        String workSpace = "";
        int dashIndex = -1;
        for (Row row : firstSheetFromWorkSpaceCodes) {
            if (row.getRowNum() > 1) {
                Cell codeCell = row.getCell(1);
                Cell workSpaceCell  = row.getCell(2);
                kodyMiejsc.put(workSpaceCell.toString(), codeCell.toString());
            }
        }
    }
    public HashMap<Integer, MainData> LoadData(String path) throws IOException {
        FileInputStream majatekSourceFile = new FileInputStream(path);
        XSSFWorkbook majatekWorkbook = new XSSFWorkbook(majatekSourceFile);
        Sheet firstSheetFromMajatek = majatekWorkbook.getSheetAt(0);
        HashMap<Integer, MainData> resultArray = new HashMap<>();
        int typOfValue = 404; //basic value in case of error
        int dashIndex;
        String grupaKST = ""; //basic value in case of error
        String numSeryjny = "";
        String pracownikBez = "";
        String imie = "";
        String kodPracownika = "";
        String dostawcaFinal = "";
        String miejsceUzytkowania = "";
        String charakterysytykaFinal = "";
        for (Row row : firstSheetFromMajatek) {
            if (row.getRowNum() > 3) {
                Cell typ = row.getCell(4);
                if(typ != null) {
                    if (typ.toString().equals("n") || typ.toString().equals("wnp") || typ.toString().equals("st")) {
                        typOfValue = switch (typ.toString()) {
                            case "st" -> 0;
                            case "n" -> 4;
                            case "wnp" -> 3;
                            default -> typOfValue;
                        };
                        Cell nazwaPelna = row.getCell(1);
                        Cell nrInwentarzowyCell = row.getCell(3);
                        Cell datPrzyjecia = row.getCell(8);
                        Cell datNabycia = row.getCell(9);
                        Cell dokNabycia = row.getCell(10);
                        Cell grupaKst = row.getCell(13);
                        if (grupaKst != null) {
                            grupaKST = grupaKst.toString();
                        } else {
                            grupaKST = ""; //rozwiazanie do poprawy
                        }
                        Cell numerSeryjny = row.getCell(20);
                        if (numerSeryjny != null) {
                            numSeryjny = numerSeryjny.toString();
                        } else {
                            numSeryjny = "";
                        }
                        Cell charakterystyka = row.getCell(22);
                        if (charakterystyka != null) {
                            charakterysytykaFinal = charakterystyka.toString();
                        } else {
                            charakterysytykaFinal = "";
                        }
                        Cell dostawca = row.getCell(23);
                        if (dostawca != null) {
                            dostawcaFinal = dostawca.toString();
                        } else {
                            dostawcaFinal = "";
                        }
                        Cell pracownikImie = row.getCell(24);
                        if (pracownikImie != null) {
                            pracownikBez = pracownikImie.toString();
                            dashIndex = pracownikBez.indexOf("-");
                            if (dashIndex != -1) {
                                imie = pracownikBez.substring(0, dashIndex);
                            } else {
                                imie = pracownikBez;
                            }
                            if (kodyPracownikow.containsKey(imie)) {
                                kodPracownika = kodyPracownikow.get(imie);
                            }
                        }
                        Cell miejsceUzytk = row.getCell(27);
                        Cell miejsceUzytkNazwa = row.getCell(28);
                        if(miejsceUzytk.toString().equals("OBROŃCÓW WYBRZEŻA")){
                            miejsceUzytkowania = "OBROŃCÓW WYBRZEŻA";
                        } else {
                            int firstPos = miejsceUzytk.toString().indexOf("--");
                            int secondPos = miejsceUzytk.toString().indexOf("--", firstPos + 1);
                            String middlePart = miejsceUzytk.toString().substring(firstPos, secondPos).trim();
                            for (String key : kodyMiejsc.keySet()) {
                                if (key.toLowerCase().contains(middlePart)) {
                                    miejsceUzytkowania = kodyMiejsc.get(key) + " " +  miejsceUzytkNazwa.toString();
                                }
                            }

                        }

                        MainData tmpMainData = new MainData(
                                nazwaPelna.toString(),
                                datPrzyjecia.toString(),
                                typOfValue,
                                nrInwentarzowyCell.toString().toUpperCase(),
                                datNabycia.toString(),
                                dokNabycia.toString(),
                                grupaKST,
                                numSeryjny,
                                kodPracownika,
                                charakterysytykaFinal,
                                dostawcaFinal,
                                miejsceUzytkowania
                        );
                        resultArray.put(row.getRowNum() + 1, tmpMainData);
                    }
                }

            }
        }
        return resultArray;
    }

    @Override
    public String toString() {
        return "MainData{" +
                "nazwaPelna='" + nazwaPelna + '\'' +
                ", nrInwenatrzowy='" + nrInwenatrzowy + '\'' +
                ", typ=" + typ +
                ", dataPrzyjecia='" + dataPrzyjecia + '\'' +
                ", dataNabycia='" + dataNabycia + '\'' +
                ", dokNabycia='" + dokNabycia + '\'' +
                ", groupaKST=" + groupaKST +
                ", nrSeryjny='" + nrSeryjny + '\'' +
                ", kodPracownika='" + kodPracownika + '\'' +
                ", charakterystyka='" + charakterystyka + '\'' +
                ", dostawca='" + dostawca + '\'' +
                ", miejsce='" + miejsceUzytkowania;
    }

}

