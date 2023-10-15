package com.pranes.akuntansisijuproliman.ui.profil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.model.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProfilEditActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 120;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fstore;
    private FirebaseUser user;
    private EditText edtNama, edtUsaha, edtHp;
    private ImageView imgPhoto;
    private Uri uriProfile;
    private String imgUrl;
    private ProgressBar progressBar;
    final String CREDENTIAL_SHARED_PREF = "our_shared_pref";
    private SharedPreferences credentials;

    private String nama,email,usaha,hp,photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_edit);
        initView();
        viewDataLocal();

    }

    private void viewDataLocal() {
        credentials = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE);
        nama = credentials.getString("nama", null);
        photo = credentials.getString("photo", null);

        edtNama.setText(nama);
        edtUsaha.setText(usaha);
        edtHp.setText(hp);
        imgUrl = photo;
        Picasso.get()
                .load(photo)
                .placeholder(R.drawable.ic_man)
                .into(imgPhoto);
    }

    private void initView() {
        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();

        progressBar = findViewById(R.id.progress_bar);
        Sprite sprite = new Circle();
        progressBar.setIndeterminateDrawable(sprite);
        edtNama = findViewById(R.id.edt_name);

        imgPhoto = findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(v -> updatePhoto());
        Button btnBatal = findViewById(R.id.btnBatal);
        btnBatal.setOnClickListener(v -> finish());
        Button btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(v -> changeData());
        viewData();


    }



    private void viewData() {
        fstore.collection("users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                edtNama.setText(user.getNama());
//                edtUsaha.setText(modelUser.getUsaha());
//                edtHp.setText(modelUser.getHp());
                imgUrl = user.getFoto();
                Picasso.get()
                        .load(user.getFoto())
                        .placeholder(R.drawable.ic_man)
                        .into(imgPhoto);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfile = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfile);
                imgPhoto.setImageBitmap(bitmap);
                uploadProfileImage();
                Uri imgUri = data.getData();
                imgPhoto.setImageURI(imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage() {
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (uriProfile != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading File...");
            dialog.show();
            profileImageRef.putFile(uriProfile)
                    .addOnSuccessListener(taskSnapshot -> {
//                        Toasty.success(this, "Poto berhasil diupload", Toast.LENGTH_SHORT, true).show();
                        Toast.makeText(this, "Poto berhasil diupload", Toast.LENGTH_SHORT).show();
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imgUrl = uri.toString();
                            Log.d("TAG", "upload gambar : " + imgUrl);
                        });
                        dialog.dismiss();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        //noinspection IntegerDivisionInFloatingPointContext
                        double progres = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded " + (int) progres + " %");
                    }).addOnFailureListener(e -> Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void changeData() {
        final String nama = edtNama.getText().toString();
        final String image = imgUrl;
        final String usaha = edtUsaha.getText().toString();
        final String hp = edtHp.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        //noinspection deprecation
//        new Handler().postDelayed(() -> {
            DocumentReference documentReference = fstore.collection("users").document(user.getUid());
            Map<String, Object> map = new HashMap<>();
            map.put("nama", nama);
            map.put("foto", image);
            SharedPreferences.Editor editor = credentials.edit();
            editor.putString("nama", nama);
            editor.putString("foto", image);
            editor.commit();

            documentReference.update(map).addOnSuccessListener(aVoid -> {
                Log.d("TAG", "changeData: sukses");
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Berhasil ubah data", Toast.LENGTH_SHORT).show();

            });
//        }, 1500);


    }

    private void updatePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), CHOOSE_IMAGE);
    }
}