package com.pranes.akuntansisijuproliman.ui.transaksi;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.adapter.TransaksiAdapter;
import com.pranes.akuntansisijuproliman.model.Waktu;
import com.pranes.akuntansisijuproliman.model.Transaksi;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.Calendar;


public class TransaksiFragment extends Fragment {
    //inisialisasi
    private FirebaseFirestore fstore;
    private FirebaseUser firebaseUser;
    private Query query;
    private TransaksiAdapter adapter;
    private FirestoreRecyclerOptions<Transaksi> options;
    private FirebaseAuth fAuth;
    private Transaksi transaksi;
    View view;
    Waktu waktu;
    Calendar c = Calendar.getInstance();
    String txtbulan1, txtbulan2, bul, bulan, bulh,tahun;
    int mMonth, month;
    PowerSpinnerView bulan1, bulan2;
    String sTotal,uid;
    RecyclerView recyclerView;
    EditText edCari;
    public TransaksiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transaksi, container, false);
        //firebase authorisasi
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uid = firebaseUser.getUid();
        initView();
        tampiltransaksi();
    }

    private void initView() {
        //panggil method waktu pada model waktu, panggil tahun
        waktu = new Waktu();
        tahun = waktu.getTahun();
        // kaitkan recycleview dengan layout xml
        recyclerView = view.findViewById(R.id.rc_transaksi);
        //atur layout recycleview
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMonth = c.get(Calendar.MONTH);
        month = mMonth+1;

        bulh=waktu.getBulan();
        txtbulan1=bulh;
        txtbulan2=bulh;

        Toast.makeText(getContext(), waktu.getBulan(), Toast.LENGTH_SHORT).show();
        pilihbulan();
        bulan1.setText(bulh);
        bulan2.setText(bulh);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void tampiltransaksi() {

//        query = fstore.collection("tb_transaksi").document(uid).collection("transaksi").orderBy("input");
        //tunjuk path untuk query firebase firestore, atur value pada field "tanggal" pada firebase firestore
        query=  fstore.collection("tb_transaksi").document("Akuntan").collection("transaksi").whereGreaterThan("tanggal",tahun+"-"+txtbulan1+"-01").whereLessThanOrEqualTo("tanggal",tahun+txtbulan2+"11"+"-32").orderBy("tanggal", Query.Direction.DESCENDING);
//        query=  fstore.collection("tb_transaksi").document(uid).collection("transaksi").whereGreaterThan("tanggal",tahun+"-"+txtbulan1);
        FirestoreRecyclerOptions<Transaksi> options = new FirestoreRecyclerOptions.Builder<Transaksi>().setQuery(query, Transaksi.class).build();
        //panggil adapter transaksi
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
        //jika edit button di tekan lakukan method berikut
        adapter.setOnItemClick(new TransaksiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Toast.makeText(view.getContext(), "EDIT CLICKED"+transaksi.getId()+" "+transaksi.getKeterangan(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleItemDelete(DocumentSnapshot documentSnapshot) {

            }
        });
    }
    //method pilih bulan dari dan sampai
    private void pilihbulan() {
        //koneksikan dengan widget spinner pada layout xml daribulan
        bulan1 = view.findViewById(R.id.drBulan);
        bulan1.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
//                Toast.makeText(PemasukanActivity.this, newItem + " selected!", Toast.LENGTH_SHORT).show();
                txtbulan1 =newItem;
                int bulann1 = Integer.parseInt(txtbulan1);
                int bulann2 = Integer.parseInt(txtbulan2);
                if (bulann1>bulann2){
                    txtbulan2 = txtbulan1;
                    bulan2.setText(txtbulan2);
                }
                //pilah data sesuai dengan value spiner yang di pilih dengan membuat format query
                query=  fstore.collection("tb_transaksi").document("Akuntan").collection("transaksi").whereGreaterThan("tanggal",tahun+"-"+txtbulan1+"-01").whereLessThanOrEqualTo("tanggal",tahun+txtbulan2+"12"+"-32").orderBy("tanggal", Query.Direction.DESCENDING);
                //build adapter transaksi
                FirestoreRecyclerOptions<Transaksi> options2 = new FirestoreRecyclerOptions.Builder<Transaksi>().setQuery(query, Transaksi.class).build();
                adapter.updateOptions(options2);
            }
        });
        //koneksikan dengan widget spinner pada layout xml daribulan
        bulan2 = view.findViewById(R.id.smpBulan);
        bulan2.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
//                Toast.makeText(PemasukanActivity.this, newItem + " selected!", Toast.LENGTH_SHORT).show();
                txtbulan2 =newItem;
                int bulann1 = Integer.parseInt(txtbulan1);
                int bulann2 = Integer.parseInt(txtbulan2);
                if (bulann2<bulann1){
                    txtbulan1 = txtbulan2;
                    bulan1.setText(txtbulan1);
                }
                //pilah data pada firebase yang sesuai dengan value spiner sampai bulan yang dipilih dengan query berikut
                query=  fstore.collection("tb_transaksi").document("Akuntan").collection("transaksi").whereGreaterThan("tanggal",tahun+"-"+txtbulan1+"-01").whereLessThanOrEqualTo("tanggal",tahun+"-"+txtbulan2+"-32").orderBy("tanggal", Query.Direction.ASCENDING);
////                Toast.makeText(getContext(),  txtbulan1+" "+txtbulan2, Toast.LENGTH_SHORT).show();
//                FirestoreRecyclerOptions<Transaksi> opsi2 = new FirestoreRecyclerOptions.Builder<Transaksi>().setQuery(query, Transaksi.class).build();
//                adapter.updateOptions(opsi2);
                FirestoreRecyclerOptions<Transaksi> options2 = new FirestoreRecyclerOptions.Builder<Transaksi>().setQuery(query, Transaksi.class).build();
                adapter.updateOptions(options2);

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}