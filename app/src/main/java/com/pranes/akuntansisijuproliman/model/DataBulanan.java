package com.pranes.akuntansisijuproliman.model;

public class DataBulanan {
    String bulan;
    int angka;

    public DataBulanan(String bulan, int angka) {
        this.bulan = bulan;
        this.angka = angka;
    }

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public int getAngka() {
        return angka;
    }

    public void setAngka(int angka) {
        this.angka = angka;
    }
}
