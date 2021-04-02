package com.example.ex192_vaccineapp;

public class Vaccines {
    private String vaccineLocation, date;

    public Vaccines(){
    }

    public Vaccines (String vaccineLocation, String date){
        this.vaccineLocation = vaccineLocation;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getVaccineLocation() {
        return vaccineLocation;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVaccineLocation(String vaccineLocation) {
        this.vaccineLocation = vaccineLocation;
    }


}
