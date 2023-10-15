package com.pranes.akuntansisijuproliman.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;

public class Transaksi implements Parcelable {
    private String id;
    private String transaksi;
    private String keterangan;
    private String akun1;
    private String jenis_transaksi;
    private String akun2;
    private String nominal;
    private String tanggal;
    private String input;
    private String id_user;
    private  String utang;
    private  String piutang;
    private String id_utang;
    private String id_piutang;
    private String kode;
    private List<String> akun;

    public Transaksi() {
    }

    public Transaksi(String id, String transaksi, String keterangan, String akun1, String akun2, String nominal, String tanggal, String id_user, String utang, String jenis_transaksi, String observasiColor, String cari, String input, String piutang, String id_utang, String id_piutang, String kode, List<String> akun) {
        this.id = id;
        this.transaksi = transaksi;
        this.keterangan = keterangan;
        this.akun1 = akun1;
        this.akun2 = akun2;
        this.nominal = nominal;
        this.tanggal = tanggal;
        this.id_user = id_user;
        this.input = input;
        this.utang = utang;
        this.piutang = piutang;
        this.id_utang = id_utang;
        this.id_piutang = id_piutang;
        this.kode = kode;
        this.akun = akun;
        this.jenis_transaksi = jenis_transaksi;
    }

    protected Transaksi(Parcel in) {
        id = in.readString();
        transaksi = in.readString();
        keterangan = in.readString();
        akun1 = in.readString();
        akun2 = in.readString();
        nominal = in.readString();
        tanggal = in.readString();
        id_user = in.readString();
        input = in.readString();
        utang = in.readString();
        piutang = in.readString();
        id_utang = in.readString();
        id_piutang = in.readString();
        kode = in.readString();
        jenis_transaksi = in.readString();
        akun = Collections.singletonList(in.readString());
    }

    public static final Creator<Transaksi> CREATOR = new Creator<Transaksi>() {
        @Override
        public Transaksi createFromParcel(Parcel in) {
            return new Transaksi(in);
        }

        @Override
        public Transaksi[] newArray(int size) {
            return new Transaksi[size];
        }
    };

    public String getJenis_transaksi() {
        return jenis_transaksi;
    }

    public void setJenis_transaksi(String jenis_transaksi) {
        this.jenis_transaksi = jenis_transaksi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransaksi() {
        return transaksi;
    }

    public void setTransaksi(String transaksi) {
        this.transaksi = transaksi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getAkun1() {
        return akun1;
    }

    public void setAkun1(String akun1) {
        this.akun1 = akun1;
    }

    public String getAkun2() {
        return akun2;
    }

    public void setAkun2(String akun2) {
        this.akun2 = akun2;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getUtang() {
        return utang;
    }

    public void setUtang(String utang) {
        this.utang = utang;
    }

    public String getPiutang() {
        return piutang;
    }

    public void setPiutang(String piutang) {
        this.piutang = piutang;
    }

    public String getId_utang() {
        return id_utang;
    }

    public void setId_utang(String id_utang) {
        this.id_utang = id_utang;
    }

    public String getId_piutang() {
        return id_piutang;
    }

    public void setId_piutang(String id_piutang) {
        this.id_piutang = id_piutang;
    }

    public List<String> getAkun() {
        return akun;
    }

    public void setAkun(List<String> akun) {
        this.akun = akun;
    }

    public static Creator<Transaksi> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(transaksi);
        parcel.writeString(keterangan);
        parcel.writeString(akun1);
        parcel.writeString(akun2);
        parcel.writeString(nominal);
        parcel.writeString(tanggal);
        parcel.writeString(id_user);
        parcel.writeString(input);
        parcel.writeString(utang);
        parcel.writeString(piutang);
        parcel.writeString(id_utang);
        parcel.writeString(id_piutang);
        parcel.writeString(kode);
        parcel.writeString(String.valueOf(akun));
        parcel.writeString(jenis_transaksi);
    }
}
