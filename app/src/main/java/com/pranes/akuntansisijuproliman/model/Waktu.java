package com.pranes.akuntansisijuproliman.model;

import java.util.Calendar;

public class Waktu {
    String menit,jam,tanggal,hari,bulan,tahun,hariini,jamini, minggu, periode, bulanlalu, tahunbulan, pengambilan, haribulan;
    Calendar c = Calendar.getInstance();




    public Waktu(String menit, String jam, String tanggal, String hari, String bulan, String tahun, String hariini, String jamini, String minggu, String periode, String haribulan) {
        this.menit = menit;
        this.jam = jam;
        this.tanggal = tanggal;
        this.hari = hari;
        this.bulan = bulan;
        this.tahun = tahun;
        this.hariini = hariini;
        this.jamini = jamini;
        this.minggu = minggu;
        this.periode = periode;
        this.haribulan = haribulan;
    }

    public Waktu() {

    }

    public String getMenit() {
        int mMinute = c.get(Calendar.MINUTE);
        if (mMinute<10){
            menit = "0"+String.valueOf(mMinute);
        }else  {
            menit = String.valueOf(mMinute);
        }
        return menit;
    }

    public void setMenit(String menit) {
        this.menit = menit;
    }

    public String getJam() {
        int mTime = c.get(Calendar.HOUR_OF_DAY);
        if (mTime<10){
            jam = "0"+String.valueOf(mTime);
        }else{
            jam = String.valueOf(mTime);
        }
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getHari() {
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        if (mDay<10){
            hari = "0"+String.valueOf(mDay);
        }else {
            hari = String.valueOf(mDay);
        }
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getBulan() {
        int mMonth = c.get(Calendar.MONTH)+1;
        if (mMonth<10){
            bulan = "0"+String.valueOf(mMonth);
        }else {
            bulan = String.valueOf(mMonth);
        }
        return bulan;
    }

    public int get1Bulan() {
        int mMonth = c.get(Calendar.MONTH)+1;
        return mMonth;
    }

    public String get0bulan(int bulan0){
        if (bulan0<10){
            bulan = "0"+ bulan0;
        }else {
            bulan = String.valueOf(bulan0);
        }
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getTahun() {
        int itahun = c.get(Calendar.YEAR);
        tahun = String.valueOf(itahun);
        return tahun;
    }

    public String getSubstringTanggal(String tgl){
        tanggal = tgl.substring(0,10);
        return tanggal;
    }

    public String getSubstringBulan(String tgl){
        String ss=tgl.substring(5,7);
        return ss;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getHariini() {
        hariini = getTahun()+"-"+getBulan()+"-"+getTanggal();
        return hariini;
    }

    public void setHariini(String hariini) {
        this.hariini = hariini;
    }

    public String getTanggal() {
        int mDate = c.get(Calendar.DATE);
        if (mDate<10){
            tanggal = "0"+mDate;
        }else {
            tanggal = String.valueOf(mDate);
        }
        return tanggal;
    }

    public String get0tanggal(int tanggal0){
        if (tanggal0<10){
            tanggal = "0"+ tanggal0;
        }else {
            tanggal = String.valueOf(tanggal0);
        }
        return tanggal;
    }

    public String getHariBulan(){
        haribulan = getTanggal()+" "+getNamaBulan(getBulan())+" "+getTahun();
        return haribulan;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJamini() {
        jamini = getJam()+":"+getMenit();
        return jamini;
    }

    public void setJamini(String jamini) {
        this.jamini = jamini;
    }




    public String getTahunbulan() {
        tahunbulan = getTahun()+"-"+getBulan();
        return tahunbulan;
    }

    public void setTahunbulan(String tahunbulan) {
        this.tahunbulan = tahunbulan;
    }


    public String getNamaBulan(String angkaBulan) {
        if (angkaBulan.equals("01")){
            bulan="Januari";
        }else if (angkaBulan.equals("02")){
            bulan="Februari";
        }else if (angkaBulan.equals("03")){
            bulan="Maret";
        }else if (angkaBulan.equals("04")){
            bulan="April";
        }else if (angkaBulan.equals("05")){
            bulan="Mei";
        }else if (angkaBulan.equals("06")){
            bulan="Juni";
        }else if (angkaBulan.equals("07")){
            bulan="Juli";
        }else if (angkaBulan.equals("08")){
            bulan="Agustus";
        }else if (angkaBulan.equals("09")){
            bulan="September";
        }else if (angkaBulan.equals("10")){
            bulan="Oktober";
        }else if (angkaBulan.equals("11")){
            bulan="November";
        }else {
            bulan="Desember";
        }
        return bulan;
    }

    public String getNamaSingkatBulan(String angkaBulan) {
        if (angkaBulan.equals("01")){
            bulan="Januari";
        }else if (angkaBulan.equals("02")){
            bulan="Februari";
        }else if (angkaBulan.equals("03")){
            bulan="Maret";
        }else if (angkaBulan.equals("04")){
            bulan="April";
        }else if (angkaBulan.equals("05")){
            bulan="Mei";
        }else if (angkaBulan.equals("06")){
            bulan="Juni";
        }else if (angkaBulan.equals("07")){
            bulan="Juli";
        }else if (angkaBulan.equals("08")){
            bulan="Agustus";
        }else if (angkaBulan.equals("09")){
            bulan="September";
        }else if (angkaBulan.equals("10")){
            bulan="Oktober";
        }else if (angkaBulan.equals("11")){
            bulan="November";
        }else {
            bulan="Desember";
        }
        return bulan;
    }
}
