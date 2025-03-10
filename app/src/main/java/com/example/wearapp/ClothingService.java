package com.example.wearapp;

public class ClothingService {

    private String clothes;
    private int time;


    public void setClothes(double temp){

        if(temp >= 21){
            this.clothes = TempEnum.HOT.getRandomClothes();
            this.time = TempEnum.HOT.getTime();
        }
        if(temp < 21 && temp >= 15){
            this.clothes = TempEnum.WARM.getRandomClothes();
            this.time = TempEnum.WARM.getTime();
        }
        if(temp < 15 && temp >= 1){
            this.clothes = TempEnum.COLD.getRandomClothes();
            this.time = TempEnum.COLD.getTime();
        }
        if(temp < 1){
            this.clothes = TempEnum.FREEZING.getRandomClothes();
            this.time = TempEnum.FREEZING.getTime();
        }
    }


    public void addAccessories(String weather){
        String x = weather.toLowerCase();
        if(x.contains("snow") || x.contains("blizzard")){
            this.clothes = this.clothes + ", czapka";
            this.time = this.time + 2;
        }else if(x.contains("thunder")){
            this.clothes = this.clothes + ", płaszcz przeciwdeszczowy";
            this.time = this.time + 2;
        }else if(x.contains("rain") || x.contains("drizzle") || x.contains("pellets") || x.contains("sleet")){
            this.clothes = this.clothes + ", parasol";
            this.time = this.time + 2;
        }else if(x.contains("sunny") || x.contains("clear")){
            this.clothes = this.clothes + ", okulary przeciwsłoneczne";
            this.time = this.time + 2;
        }

    }

    public String getClothes(){
        return this.clothes;
    }
    public int getTime(){
        return this.time;
    }

}
