package com.example.wearapp;

import java.util.Random;

public enum TempEnum {

    HOT(
            "lniana koszula, ołówkowa spódnica, czółenka na niskim obcasie",
            "lniany komplet garniturowy, lekka koszula, czółenka na słupku",
            "sukienka z krótkim rękawem, czółenka na szpilce",
            6
    ),
    WARM(
            "spodnie z nogawką w kant, koszula z rękawem 3/4, żakiet, czółenka na niskim obcasie",
            "ołówkowa spódnica, koszula, marynarka, czółenka na słupku",
            "sukienka z rękawem 3/4 , żakiet, czółenka na szpilce",
            8
    ),
    COLD(
            "koszula z długim rękawem, spodnie z nogawką w kant, żakiet, jesienny płaszcz, sztyblety",
            "sukienka z długim rękawem, trench, rajstopy lekkie, botki na obcasie",
            "ołówkowa spódnica, koszula z długim rękawem, żakiet, trench, botki na szpilce",
            10
    ),
    FREEZING(
            "koszula z długim rękawem, kardigan, spodnie w kant, rajstopy, zimowy płaszcz, szalik, rękawiczki, ocieplane botki",
            "sukienka z długim rękawem, żakiet, grube rajstopy, futro, rękawiczki, ocieplane botki na słupku",
            "koszula z długim rękawem, marynarka, spodnie w kant, rajstopy, puchowa kurtka, szalik, rękawiczki",
            16
    );

    private String set1;
    private String set2;
    private String set3;
    private int time;

    TempEnum(String set1, String set2, String set3, int time) {
        this.set1 = set1;
        this.set2 = set2;
        this.set3 = set3;
        this.time = time;
    }

    TempEnum() {
    }


    public String getRandomClothes(){
        String[] sets = new String[]{this.set1, this.set2, this.set3};
        Random random = new Random();
        return sets[random.nextInt(sets.length)];
    }

    public int getTime(){
        return this.time;
    }


}
