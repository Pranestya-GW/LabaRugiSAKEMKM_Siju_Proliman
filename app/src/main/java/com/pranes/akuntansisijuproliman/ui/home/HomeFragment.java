package com.pranes.akuntansisijuproliman.ui.home;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pranes.akuntansisijuproliman.model.User;
import com.pranes.akuntansisijuproliman.ui.transaksi.PemasukanActivity;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.ui.transaksi.PengeluaranActivity;
import com.pranes.akuntansisijuproliman.model.Transaksi;
import com.pranes.akuntansisijuproliman.adapter.TransaksiAdapter;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment implements View.OnClickListener {
    //inisialisasi variabel
    private static final String TAG = "TAG";
    private FirebaseFirestore fstore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Query query;
    private TransaksiAdapter adapter;
    private Transaksi transaksi;
    private ImageView imgPhoto;
    private TextView tvNama, photo;
    Button btnPemasukan, btnPengeluaran, btnUtang, btnByrUtang, btnPiutang, btnDbyrPiutang, btnTmbhModal, btnTrkModal, btnPenyesuaian;
    View view;
    String uid;
    private final String CREDENTIAL_SHARED_PREF = "our_shared_pref";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //inflate layout ke arah fragment home
        view = inflater.inflate(R.layout.fragment_home, container, false);
        fstore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        initView();
        tampilViewLocal();
        tampilTransaksi();
        return view;
    }

    private void initView() {
        //profil pada bagian atas layout home
        imgPhoto = view.findViewById(R.id.imgProfile);
        tvNama = view.findViewById(R.id.txt_nama);
        //pemasukan
        btnPemasukan = view.findViewById(R.id.btn_pemasukan);
        btnPemasukan.setOnClickListener(this);
        //pengeluaran
        btnPengeluaran = view.findViewById(R.id.btn_pengeluaran);
        btnPengeluaran.setOnClickListener(this);
    }
    // tampil transaksi pada menu home
    private void tampilTransaksi() {
        // koneksi ke layout xml recycleview
        RecyclerView recyclerView = view.findViewById(R.id.rc_transaksibaruhome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // ambil query dari firebase firestore dengan format sebagai berikut
        query = fstore.collection("tb_transaksi").document("Akuntan").collection("transaksi").orderBy("input", Query.Direction.DESCENDING);
        // panggil adapter transaksi
        FirestoreRecyclerOptions<Transaksi> options = new FirestoreRecyclerOptions.Builder<Transaksi>().setQuery(query, Transaksi.class).build();
        adapter = new TransaksiAdapter(options);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        adapter.notifyDataSetChanged();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.scrollToPosition(0);
            }
        });
        //panggil setter dan getter pada model transaksi dan masukkan dengan adapter
        adapter.setOnItemClick(new TransaksiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                transaksi = documentSnapshot.toObject(Transaksi.class);
//                Intent intent = new Intent(getContext(), TeachDetailSiswaActivity.class);
//                intent.putExtra(TeachDetailSiswaActivity.SISWA, siswa);
//                startActivity(intent);
            }


            @Override
            public void handleItemDelete(DocumentSnapshot documentSnapshot) {

            }
        });

    }

    //tampil layout user dibagian atas
    private void tampilViewLocal() {
        SharedPreferences credentials = this.getActivity().getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE);
        String nama = credentials.getString("nama", null);
        String photo = credentials.getString("foto", null);
        tvNama.setText(nama);
        Picasso.get().load(photo).placeholder(R.drawable.ic_man).into(imgPhoto); //ambil gambar

    }


    //koneksi button pada fragment
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_pemasukan:
                startActivity(new Intent(getActivity(), PemasukanActivity.class));
                break;
            case R.id.btn_pengeluaran:
                startActivity(new Intent(getActivity(), PengeluaranActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}