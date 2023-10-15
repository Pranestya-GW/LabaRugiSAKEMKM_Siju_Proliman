package com.pranes.akuntansisijuproliman;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pranes.akuntansisijuproliman.model.User;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    //inisiasi
    private EditText edtEmail, edtPass;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    final String CREDENTIAL_SHARED_PREF = "our_shared_pref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inisialisasi
        Button btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressbar);
        Sprite sprite = new Circle();
        progressBar.setIndeterminateDrawable(sprite);

        //form edit text
        edtEmail = findViewById(R.id.input_email);
        edtPass = findViewById(R.id.input_password);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance(); //firebase Authentication untuk cek user yg login
        firebaseFirestore = FirebaseFirestore.getInstance(); // firebase firestore
        btnLogin.setOnClickListener(v -> login()); //onClick tombol login ke method login()
    }


    private void login() {
        final String email = edtEmail.getText().toString();//mengambil nilai dr form email
        final String pass = edtPass.getText().toString();//mengambil nilai dr form password

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email tidak boleh kosong");
        } else if (TextUtils.isEmpty(pass)) {
            edtPass.setError("Password tidak boleh kosong");
        } else {
            onShowProgress();//method memanggil progressbar / loading
            getAuth(email, pass);//method utk memasukan email pass ke login firebase authentication
        }
    }
    private void getAuth(String email, String pass) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                assert user != null; //memastikan user tidak kososng
                onSuccessAuth(user.getUid());
            } else {
                onFailedAuth();
            }
        });
    }

    private void onFailedAuth() {
        Toast.makeText(LoginActivity.this, "Gagal login, cek email dan password kembali", Toast.LENGTH_SHORT).show();
        onHideProgress();
    }

    private void onSuccessAuth(String uid) {//jika login sukses
        String userID = firebaseAuth.getCurrentUser().getUid();
        CollectionReference user = firebaseFirestore.collection("users");
        user.document(userID).get().addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            assert documentSnapshot != null;
            User User = documentSnapshot.toObject(User.class);
            assert User != null;
            String nama = User.getNama();
            String email = User.getEmail();
            String pass = User.getPassword();
            String hp = User.getHp();
            String foto = User.getFoto();

            //Setelah berhasil login akan Menyimpan ke sharedPreference penyimpanan lokal sementara agar proses load di menu tdk terlalu lama
            SharedPreferences credentials = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = credentials.edit();
            editor.putString("nama", nama);
            editor.putString("email", email);
            editor.putString("password", pass);
            editor.putString("hp", hp);
            editor.putString("uid", uid);
            editor.putString("photo", foto);
            editor.commit();
            if (task.isSuccessful()) {
                if (uid == null) {
                    onHideProgress();
                    // opsional jika ingin masuk dengan email yang sudah diverifikasi
                    firebaseAuth.signOut();
                } else {
                    DocumentSnapshot snapshot = task.getResult();
                    // jika berhasil mendapatkan data verif maka lanjutkan ke activity main
                    onHideProgress();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                }
            }
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: gagal login"));
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void onShowProgress() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void onHideProgress() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}