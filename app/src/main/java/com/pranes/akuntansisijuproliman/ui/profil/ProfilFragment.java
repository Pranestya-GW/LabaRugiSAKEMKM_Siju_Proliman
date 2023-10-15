package com.pranes.akuntansisijuproliman.ui.profil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pranes.akuntansisijuproliman.LoginActivity;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.model.User;
import com.squareup.picasso.Picasso;

public class ProfilFragment extends Fragment implements View.OnClickListener {
    private FirebaseFirestore fstore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView tvNama, tvEmail, tvJabatan;
    private ImageView imgPhoto;

    private Button btnAkun, btnLogout;
    private final String CREDENTIAL_SHARED_PREF = "our_shared_pref";
    String nama, jabatan,email,photo;
    public ProfilFragment() {
        // Required empty public constructor
    }
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profil, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fstore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        initView();
        tampilViewLocal();

    }

    private void tampilViewLocal() {
        SharedPreferences credentials = this.getActivity().getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE);
        nama = credentials.getString("nama", null);
        email = credentials.getString("email", null);
        jabatan = credentials.getString("jabatan", null);
        photo = credentials.getString("foto", null);

        tvNama.setText(nama);
        tvEmail.setText(email);
        tvJabatan.setText(jabatan);
        Picasso.get().load(photo).placeholder(R.drawable.ic_man).into(imgPhoto);
    }

    private void tampilView() {
        String uid = firebaseUser.getUid();
        fstore.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        tvNama.setText(user.getNama());
                        tvEmail.setText(user.getEmail());
                        tvJabatan.setText(user.getJabatan());
                        Picasso.get().load(user.getFoto()).placeholder(R.drawable.ic_man).into(imgPhoto);
                    }
                }).addOnFailureListener(Throwable::getMessage);
    }


    private void initView() {
        //noinspection ConstantConditions
        tvNama = getView().findViewById(R.id.txt_nama);
        tvEmail = getView().findViewById(R.id.txt_email);
        tvJabatan = getView().findViewById(R.id.txt_jabatan);
        imgPhoto = getView().findViewById(R.id.imgPhoto);
        btnAkun = getView().findViewById(R.id.btn_akun);
        btnLogout = getView().findViewById(R.id.btn_logout);

        btnAkun.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_akun:
                startActivity(new Intent(getActivity(), ProfilEditActivity.class));
                break;
            case R.id.btn_logout:
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                getActivity().finishAffinity();
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        tampilViewLocal();
    }

}