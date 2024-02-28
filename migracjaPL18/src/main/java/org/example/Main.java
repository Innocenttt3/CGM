package org.example;

import java.io.IOException;
import java.util.HashMap;


public class Main {
    public static void main(String[] args) throws IOException {

//        MainData test = new MainData();
//        HashMap<Integer, MainData> testResults = new HashMap<>();
//        testResults = test.LoadData("/Users/kamilgolawski/CGM/dane/ST_KST/Majątek 31.10.2023.xlsx");
//        testResults.forEach((key, value) -> {
//            if (key == 184) {
//                System.out.println("Key: " + key + ", Value: " + value.toString());
//            }
//        });
        String input = "OBROŃCÓW WYBRZEŻA -- Administracja -- -- 403 Dział TA - Serwerownia Górna";

        int secondDash = input.indexOf("--");
        int thirdDash = input.indexOf("--", secondDash + 1);

        String middlePart = input.substring(secondDash + 3, thirdDash).trim();
        System.out.println(middlePart);


    }
}