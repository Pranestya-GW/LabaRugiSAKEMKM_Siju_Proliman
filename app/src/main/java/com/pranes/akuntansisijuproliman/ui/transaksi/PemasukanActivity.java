package com.pranes.akuntansisijuproliman.ui.transaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.model.Transaksi;
import com.pranes.akuntansisijuproliman.model.Waktu;
import com.skydoves.powerspinner.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PemasukanActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TRANSAKSI = "TRANSAKSI";
    private LinearLayout LayoutTanggal;
    String bulan, spin1, keterangan, nominal, tanggal, input,bulh,mTanggal,ss;
    int month;
    EditText etKeterangan, etNominal;
    PowerSpinnerView spinner1;
    TextView tvTanggal;
    Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    Button btnSimpan;
    private ProgressBar progressBar;
    private Transaksi transaksi;
    private Waktu waktu;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemasukan);
        waktu = new Waktu();
        initView();
        transaksi = getIntent().getParcelableExtra(TRANSAKSI);
        if (transaksi!=null){
            getDataTransaksi();
        }

    }



    private void initView() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        btnSimpan = findViewById(R.id.btn_simpan);
        btnSimpan.setOnClickListener(this);
        progressBar = findViewById(R.id.progressbar);
        Sprite sprite = new Circle();
        progressBar.setIndeterminateDrawable(sprite);

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        month = mMonth+1;

        input = mYear+"-"+waktu.get0bulan(month)+"-"+waktu.getTanggal()+" "+ waktu.getJam() +":"+waktu.getMenit();
        mTanggal = mYear+"-"+waktu.get0bulan(month)+"-"+waktu.getTanggal();

        spinner1 = findViewById(R.id.spinner_1);

        spinner1.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            spin1=newItem;
            spinner1.setError(null);
        });


        etKeterangan = findViewById(R.id.edtKeterangan);
        keterangan = etKeterangan.getText().toString();
        etNominal = findViewById(R.id.edtNominal);
        etNominal.addTextChangedListener(onTextChangedListener());
        nominal =  etNominal.getText().toString().replaceAll(",", "");
        tvTanggal = findViewById(R.id.tanggal);
        tvTanggal.setOnClickListener(this);
        LayoutTanggal = findViewById(R.id.tanggall);
        LayoutTanggal.setOnClickListener(this);
        tvTanggal.setText(waktu.getHariBulan());
    }

    @SuppressLint("SetTextI18n")
    private void getDataTransaksi() {
        spin1 = transaksi.getAkun2();
        nominal = transaksi.getNominal();
        String txtgl = transaksi.getInput();
        keterangan = transaksi.getKeterangan();

        spinner1.setText(spin1);
        btnSimpan.setText("Edit");
        etNominal.setText(nominal);
        etKeterangan.setText(keterangan);

        String t =txtgl.substring(8,10);
        ss=txtgl.substring(5,7);
        String th = txtgl.substring(0,4);
        String j = txtgl.substring(11,16);

        tvTanggal.setText(t+" "+waktu.getNamaBulan(ss)+" "+th);
//        input = th+"-"+bulh+"-"+t+" "+j;
        mTanggal = transaksi.getTanggal();
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etNominal.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    etNominal.setText(formattedString);
                    etNominal.setSelection(etNominal.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                etNominal.addTextChangedListener(this);
            }
        };
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tanggal:
                datetimepicker();
                break;
            case R.id.tanggall:
                datetimepicker();
                break;
            case R.id.btn_simpan:
                keterangan = etKeterangan.getText().toString();
                nominal = etNominal.getText().toString();
                if (transaksi!=null){
                    edit();
                }else {
                    simpan();
                }
                break;
        }
    }

    private void edit() {
        if (TextUtils.isEmpty(keterangan)) {
            etKeterangan.setError("Keterangan tidak boleh kosong");
        } else if (spin1==null) {
            spinner1.setError("Tidak boleh kosong");
        }else if (TextUtils.isEmpty(nominal)) {
            etNominal.setError("Nominal tidak boleh kosong");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            String uid = firebaseUser.getUid();
            DocumentReference documentReference = firebaseFirestore.collection("tb_transaksi").document("Akuntan").collection("transaksi").document(transaksi.getId());
//                    DocumentReference documentReference = fStore.collection("transaksi").document(uid);
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", transaksi.getId());
            userMap.put("user_id", uid);
            userMap.put("keterangan", keterangan);
            userMap.put("nominal", nominal);
            userMap.put("tanggal", mTanggal);
            userMap.put("jenis_transaksi", spin1);
            userMap.put("transaksi", "Pemasukan");
            userMap.put("input", transaksi.getInput());

            documentReference.set(userMap).addOnSuccessListener(aVoid -> {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PemasukanActivity.this, "Edit Data Berhasil", Toast.LENGTH_SHORT).show();
                etNominal.setText("");
                etKeterangan.setText("");
                spinner1.clearSelectedItem();
                finish();
            });
        }
    }

    private void simpan() {

        if (TextUtils.isEmpty(keterangan)) {
            etKeterangan.setError("Keterangan tidak boleh kosong");
        } else if (spin1==null) {
            spinner1.setError("Tidak boleh kosong");
        }else if (TextUtils.isEmpty(nominal)) {
            etNominal.setError("Nominal tidak boleh kosong");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            String uid = firebaseUser.getUid();
            DocumentReference documentReference = firebaseFirestore.collection("tb_transaksi").document("Akuntan").collection("transaksi").document();
//                    DocumentReference documentReference = fStore.collection("transaksi").document(uid);
            String id = documentReference.getId();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", id);
            userMap.put("user_id", uid);
            userMap.put("keterangan", keterangan);
            userMap.put("nominal", nominal);
            Toast.makeText(PemasukanActivity.this, input+"\n"+mTanggal, Toast.LENGTH_SHORT).show();
            userMap.put("tanggal", mTanggal);
            userMap.put("jenis_transaksi", spin1);
            userMap.put("transaksi", "Pemasukan");
            userMap.put("input", input);

            documentReference.set(userMap).addOnSuccessListener(aVoid -> {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PemasukanActivity.this, "Simpan Data Berhasil", Toast.LENGTH_SHORT).show();
                etNominal.setText("");
                etKeterangan.setText("");
                spinner1.clearSelectedItem();
                finish();
            });

        }
    }

    private void datetimepicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        month = monthOfYear + 1;
                        mDay = dayOfMonth;

                        tanggal=dayOfMonth +" "+ waktu.getNamaBulan(waktu.get0bulan(month)) +" "+  year;
                        tvTanggal.setText(tanggal);

                        input = year+"-"+waktu.get0bulan(month)+"-"+waktu.getTanggal()+" "+ waktu.getJam() +":"+waktu.getMenit();
//                        tanggal = mTanggal +" "+ bulan +" "+  mYear;
                        mTanggal = year+"-"+waktu.get0bulan(month)+"-"+waktu.get0tanggal(dayOfMonth);
                        Toast.makeText(PemasukanActivity.this, input+"\n"+mTanggal, Toast.LENGTH_SHORT).show();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


}

