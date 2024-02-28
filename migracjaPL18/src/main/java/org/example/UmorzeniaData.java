package org.example;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class UmorzeniaData {

    public String metodaAmortyzacji;
    public String kontoWarBrutto;
    public String kontoUM;
    public float bruttoBilansFloat;
    public float umorzeniaKoniecFloat;
    public float amortyzacja;

    public UmorzeniaData(float bruttoBilansFloat, float umorzeniaKoniecFloat, String metodaAmortyzacji, String kontoWarBrutto, float amortyzacja, String kontoUM) {
        this.bruttoBilansFloat = bruttoBilansFloat;
        this.umorzeniaKoniecFloat = umorzeniaKoniecFloat;
        this.metodaAmortyzacji = metodaAmortyzacji;
        this.kontoWarBrutto = kontoWarBrutto;
        this.amortyzacja = amortyzacja;
        this.kontoUM = kontoUM;
    }
    public UmorzeniaData() {};

    public HashMap<String, UmorzeniaData> loadData(String filePath, int index1, int index2, int index3, int index4, int index5, int index6, int index7) throws IOException {

        FileInputStream srodkiTrwaleUmorzoneFile = new FileInputStream((filePath));
        XSSFWorkbook srodkiTrwaleUmorzoneWorkbook = new XSSFWorkbook(srodkiTrwaleUmorzoneFile);
        Sheet firstSheetFromSrodkiTrwaleUmorzone = srodkiTrwaleUmorzoneWorkbook.getSheetAt(0);
        HashMap<String, UmorzeniaData> umorzoneArray = new HashMap<>();
        String tmpLiniowa = null;
        int amortyzacjaFloatFinal = 0;

        for (Row row : firstSheetFromSrodkiTrwaleUmorzone) {
            if(row.getRowNum() > 0){

                Cell nrInwentarza = row.getCell(index1);
                Cell bruttoBilans = row.getCell(index2);
                Cell wartoscNetto = row.getCell(index3);
                float bruttoBilansFloat = (float) bruttoBilans.getNumericCellValue();
                float wartoscNettoFloat = (float) wartoscNetto.getNumericCellValue();
                float umorzeniaKoniec = bruttoBilansFloat - wartoscNettoFloat;
                DecimalFormat df = new DecimalFormat("#.##");
                String formattedNumber = df.format(umorzeniaKoniec);
                float umorzenieKoniecFinal = Float.parseFloat(formattedNumber);
                Cell metodaAmortyzacji = row.getCell(index4);
                if(metodaAmortyzacji.toString().equals("liniowa")){
                    tmpLiniowa = "L";
                }
                Cell kontoWarBrutto = row.getCell(index5);
                Cell kontoUM = row.getCell(index6);
                Cell amortyzacja = row.getCell(index7);
                DataFormatter formatter = new DataFormatter();
                String percentageString = formatter.formatCellValue(amortyzacja);
                percentageString = percentageString.replace("%", "");
                float percentageFloat = Float.parseFloat(percentageString);
            UmorzeniaData tmpData = new UmorzeniaData(bruttoBilansFloat, umorzenieKoniecFinal, tmpLiniowa, kontoWarBrutto.toString(), percentageFloat, kontoUM.toString());
            umorzoneArray.put(nrInwentarza.toString(), tmpData);

            }
        }
        return umorzoneArray;
    }

    public String getFieldValuesAsString() {
        return "bruttoBilansFloat=" + bruttoBilansFloat +
                ", umorzeniaKoniecFloat=" + umorzeniaKoniecFloat +
                ", metodaAmortyzacji='" + metodaAmortyzacji + '\'' +
                ", kontoWarBrutto='" + kontoWarBrutto + '\'' +
                ", amortyzacja=" + amortyzacja  + '\'' +
                ", kontoUm=" + kontoUM;
    }
}
