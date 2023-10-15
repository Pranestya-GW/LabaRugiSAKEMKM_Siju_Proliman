package com.pranes.akuntansisijuproliman.model;


import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String uid, nama, foto, hp, email, password, jabatan;

    public User() {
    }

    public User(String uid, String name, String foto, String hp, String email, String password, String jabatan) {
        this.uid = uid;
        this.nama = name;
        this.foto = foto;
        this.hp = hp;
        this.email = email;
        this.password = password;
        this.jabatan = jabatan;
    }

    protected User(Parcel in) {
        uid = in.readString();
        nama = in.readString();
        foto = in.readString();
        hp = in.readString();
        email = in.readString();
        password = in.readString();
        jabatan = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(nama);
        dest.writeString(foto);
        dest.writeString(hp);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(jabatan);
    }
}

