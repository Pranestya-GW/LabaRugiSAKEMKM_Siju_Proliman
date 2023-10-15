package com.pranes.akuntansisijuproliman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pranes.akuntansisijuproliman.model.User;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    final String CREDENTIAL_SHARED_PREF = "our_shared_pref";
    private String nama,email,jabatan,hp, foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //inisialisasi firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        AuthUser();
    }

    private void AuthUser() {
        if (firebaseUser != null) { //jika masih login akan meload data dari user tersebut
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            CollectionReference users = firebaseFirestore.collection("users");
            users.document(uid).get().addOnCompleteListener(task -> {
//                Toast.makeText(SplashActivity.this, "Berhasil "+uid, Toast.LENGTH_SHORT).show();
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                User user = documentSnapshot.toObject(User.class);
//                assert user != null;
                if (user!=null){
                    nama = user.getNama();
                    email = user.getEmail();
                    hp = user.getHp();
                    foto = user.getFoto();
                    jabatan = user.getJabatan();
//                    Toast.makeText(SplashActivity.this, nama +jabatan+email+hp+foto, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SplashActivity.this, "gagal", Toast.LENGTH_SHORT).show();
                }


                //Menyimpan ke sharedPreference penyimpanan lokal sementara
                SharedPreferences credentials = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = credentials.edit();
                editor.putString("nama", nama);
                editor.putString("email", email);
                editor.putString("hp", hp);
                editor.putString("jabatan", jabatan);
                editor.putString("foto", foto);
                editor.commit();

                //Pindah ke halaman Main
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            });
        }else {//jika belum login akan diarahkan ke halaman login
            login();
        }
    }
    private void login() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}