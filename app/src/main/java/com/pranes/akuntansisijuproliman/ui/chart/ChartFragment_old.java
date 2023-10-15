package com.pranes.akuntansisijuproliman.ui.chart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.model.DataBulanan;
import com.pranes.akuntansisijuproliman.model.Transaksi;
import com.pranes.akuntansisijuproliman.model.Waktu;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


public class ChartFragment_old extends Fragment {
    private static final String TAG = "TAG";
    View view;
    int cek=0;
    Waktu waktu = new Waktu();
    BarChart barChartHarian;
    BarChart barChartBulanan;
    BarData barDataHarian;
    BarData barDataBulanan;
    BarDataSet barDataSetHarian;
    BarDataSet barDataSetBulanan;
    ArrayList<BarEntry> barEntriesHarianArrayList;
    ArrayList<BarEntry> barEntriesBulananArrayList;
    ArrayList<String> lableNameHarian;
    ArrayList<String> lableNameBulanan;
    ArrayList<DataBulanan> dataHari = new ArrayList<>();
    ArrayList<DataBulanan> dataBulan = new ArrayList<>();
    PowerSpinnerView filterBulan;
    String txtbulan,tanggal;
    DecimalFormat formatter;
    String akun1,akun2,akun3,akun4,akun5,akun6,akun7,akun8,akun9,akun10,akun11,akun12,akun13,akun14,akun15,akun16,akun17,akun18,akun19,akun20,akun21,akun22,akun23,akun24,akun25,akun26;
    int saldo = 0;
    int saldo1=0,saldo2=0,saldo3=0,saldo4=0,saldo5=0,saldo6=0,saldo7=0,saldo8=0,saldo9=0,saldo10=0,saldo11=0,saldo12=0,saldo13=0,saldo14=0,saldo15=0;
    int saldo16=0,saldo17=0,saldo18=0,saldo19=0,saldo20=0,saldo21=0,saldo22=0,saldo23=0,saldo24=0,saldo25=0,saldo26=0,saldo27=0,saldo28=0,saldo29=0,saldo30=0,saldo31=0;
    int saldoBulan1=0, saldoBulan2=0, saldoBulan3=0, saldoBulan4=0, saldoBulan5=0, saldoBulan6=0, saldoBulan7=0, saldoBulan8=0, saldoBulan9=0, saldoBulan10=0, saldoBulan11=0, saldoBulan12=0;
    private FirebaseFirestore fstore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chart, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fstore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        barChartHarian = view.findViewById(R.id.barchartHarian);
        barChartBulanan = view.findViewById(R.id.barchartBulanan);
        barEntriesHarianArrayList = new ArrayList<>();
        barEntriesBulananArrayList = new ArrayList<>();
        lableNameHarian = new ArrayList<>();
        lableNameBulanan = new ArrayList<>();
        createRecycleHarian(waktu.getBulan());
        createRecycleBulanan(waktu.getTahun());
        TextView tahun = view.findViewById(R.id.tahun);
        tahun.setText(waktu.getTahun());
        filterBulan = view.findViewById(R.id.filterBulan);
        txtbulan = waktu.getBulan();
        filterBulan.setText(txtbulan);
        filterBulan.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                txtbulan = newItem;
                createRecycleHarian(txtbulan);
                Log.d(TAG, "onItemSelected: "+txtbulan);
                saldoHarianClear();
            }
        });


    }


    //chart Harian
    private void createRecycleHarian(String txtbulan) {
        Log.d(TAG, "chartHarian: Masuk create cycle");
        getAkun();
        //Pendapatan


        Query query6 = fstore.collection("tb_transaksi")
                .document("Akuntan").collection("transaksi")
                .whereGreaterThanOrEqualTo("tanggal",waktu.getTahun()+"-"+txtbulan+"-01")
                .whereLessThanOrEqualTo("tanggal",waktu.getTahun()+"-"+txtbulan+"-32")
                .orderBy("tanggal", Query.Direction.DESCENDING);
        query6.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "chartHarian: Query Berhasil");

                Long lDebet;
                Long lKredit;

                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    Transaksi transaksi = documentSnapshot.toObject(Transaksi.class);
                    if (transaksi.getAkun2().equals(akun1)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                        Log.d(TAG, "chartHarian: Masuk Pendapatan debit, saldo :"+lDebet);
//                        Log.d(TAG, "Pendapatan: "+saldo+" "+saldoPendapatan);
                    }
                    else if(transaksi.getAkun1().equals(akun1)){
                        String txkredit = transaksi.getNominal().replaceAll(",", "");
                        if (txkredit.contains(",")) {
                            txkredit = txkredit.replaceAll(",", "");
                        }
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                        Log.d(TAG, "chartHarian: Masuk Pendapatan kredit, saldo :"+lKredit.intValue()*-1);
                    }

                    if(transaksi.getAkun1().equals(akun2)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun2)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }

                    if(transaksi.getAkun1().equals(akun3)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun3)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }

                    if(transaksi.getAkun1().equals(akun4)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun4)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }
                    if(transaksi.getAkun1().equals(akun5)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun5)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());

                    }

                    if(transaksi.getAkun1().equals(akun6)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun6)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }

                    if (transaksi.getAkun2().equals(akun7)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun7)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun8)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun8)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }
                    //Beban Penjualan
                    if (transaksi.getAkun2().equals(akun9)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun9)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun10)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun10)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }
                    //Beban Admin dan Umum
                    if (transaksi.getAkun2().equals(akun11)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun11)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun12)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun12)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun13)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun13)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun14)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }

                    if(transaksi.getAkun1().equals(akun14)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun15)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun15)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun16)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun16)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun17)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun17)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun18)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun18)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun19)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun19)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun20)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun20)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun21)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun21)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun22)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun22)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun23)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun23)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }
                    //Pendapatan Luar Usaha
                    if(transaksi.getAkun1().equals(akun24)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun24)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }

                    if(transaksi.getAkun1().equals(akun25)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun25)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }

                    if(transaksi.getAkun2().equals(akun26)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun1().equals(akun26)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));    
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lDebet.intValue());
                    }

                    String totsaldo = formatter.format(saldo);



                }
            } else {
                Log.d(TAG, "Hasil Query Gagal");
            }
            chartHarian();
        });
    }

    private void getAkun() {
        akun1 ="Pendapatan";
        akun2 ="Penjualan Barang";
        akun3 ="Potongan Penjualan";
        akun4 ="Retur Penjualan";
        akun5 ="Ikhtisar Laba/Rugi";

        //Harga Pokok Penjualan
        akun6 ="Beban Pokok Penjualan";
        akun7 ="Potongan Pembelian";
        akun8 ="Retur Pembelian";

        //Beban Penjualan
        akun9 ="Beban Pengiriman";
        akun10 ="Beban Penjualan Lain-lain";

        //Beban Admin dan Umum
        akun11 ="Beban Gaji Karyawan";
        akun12 ="Beban Telepon";
        akun13 ="Beban Listrik";
        akun14 ="Beban Air";
        akun15 = "Beban Depresiasi Peralatan";
        akun16 = "Beban Perlengkapan";
        akun17 = "Beban Konsumsi";
        akun18 = "Beban Sewa";
        akun19 = "Beban Umum Lain-lain";
        akun20 = "Beban Piutang Tak Tertagih";
        akun21 = "Beban Penyusutan Bangunan";
        akun22 = "Beban Penyusutan Peralatan";
        akun23 = "Beban Penyusutan Kendaraan";

        //Pendapatan Diluar Usaha
        akun24 = "Pendapatan Bunga Bank";
        akun25 = "Hasil Panen";

        //Beban Diluar Usaha
        akun26 = "Beban Administrasi Bank";
    }

    private void chartHarian() {
        getDataHarian();
        for (int i = 0; i < dataHari.size(); i++){
            String month = dataHari.get(i).getBulan();
            int sales = dataHari.get(i).getAngka();
            barEntriesHarianArrayList.add(new BarEntry(i,sales));
            lableNameHarian.add(month);
        }
        barDataSetHarian = new BarDataSet(barEntriesHarianArrayList,"Transaksi Bulanan");
        barDataSetHarian.setColors(ColorTemplate.COLORFUL_COLORS);

        Description description = new Description();
        description.setText("Transaksi Harian Bulan "+waktu.getNamaBulan(txtbulan));
        barChartHarian.setDescription(description);
        barDataHarian = new BarData(barDataSetHarian);
        barDataHarian.setValueTextSize(8f);
        barChartHarian.setData(barDataHarian);
        barChartHarian.setMaxVisibleValueCount(12);
        barChartHarian.setAutoScaleMinMaxEnabled(true);
        barChartHarian.setVisibleXRangeMaximum(10);


        XAxis xAxis= barChartHarian.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(lableNameHarian));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(lableNameHarian.size());
//        xAxis.setAxisMaxValue(12f);

        YAxis yAxis = barChartHarian.getAxisRight();
        yAxis.setEnabled(false);
//        xAxis.setLabelRotationAngle(270);
        barChartHarian.animateY(2000);
        barChartHarian.invalidate();
    }

    private void setSaldo(String tanggal, int saldo) {
        if (tanggal.equals("01")) {
                saldo1 += saldo;
            Log.d(TAG, "setSaldo: "+saldo1);
        }else if (tanggal.equals("02")) {
            saldo2 += saldo;
        }else if (tanggal.equals("03")) {
            saldo3 += saldo;
        }else if (tanggal.equals("04")) {
            saldo4 += saldo;
        }else if (tanggal.equals("05")) {
            saldo5 += saldo;
        }else if (tanggal.equals("06")) {
            saldo6 += saldo;
        }else if (tanggal.equals("07")) {
            saldo7 += saldo;
        }else if (tanggal.equals("08")) {
            saldo8 += saldo;
        }else if (tanggal.equals("09")) {
            saldo9 += saldo;
        }else if (tanggal.equals("10")) {
            saldo10 += saldo;
        }else if (tanggal.equals("11")) {
            saldo11 += saldo;
        }else if (tanggal.equals("12")) {
            saldo12 += saldo;
        }else if (tanggal.equals("13")) {
            saldo13 += saldo;
        }else if (tanggal.equals("14")) {
            saldo14 += saldo;
        }else if (tanggal.equals("15")) {
            saldo15 += saldo;
        }else if (tanggal.equals("16")) {
            saldo16 += saldo;
        }else if (tanggal.equals("17")) {
            saldo17 += saldo;
        }else if (tanggal.equals("18")) {
            saldo18 += saldo;
        }else if (tanggal.equals("19")) {
            saldo19 += saldo;
        }else if (tanggal.equals("20")) {
            saldo20 += saldo;
        }else if (tanggal.equals("21")) {
            saldo21 += saldo;
        }else if (tanggal.equals("22")) {
            saldo22 += saldo;
        }else if (tanggal.equals("23")) {
            saldo23 += saldo;
        }else if (tanggal.equals("24")) {
            saldo24 += saldo;
        }else if (tanggal.equals("25")) {
            saldo25 += saldo;
        }else if (tanggal.equals("26")) {
            saldo26 += saldo;
        }else if (tanggal.equals("27")) {
            saldo27 += saldo;
        }else if (tanggal.equals("28")) {
            saldo28 += saldo;
        }else if (tanggal.equals("29")) {
            saldo29 += saldo;
        }else if (tanggal.equals("30")) {
            saldo30 += saldo;
        }else if (tanggal.equals("31")) {
            saldo31 += saldo;
        }
    }

    private void getDataHarian(){
        dataHari.clear();
        dataHari.add(new DataBulanan("1",saldo1));
        Log.d(TAG, "getDataHarian tgl1: "+saldo1+"bulan "+txtbulan);
        dataHari.add(new DataBulanan("2",saldo2));
        dataHari.add(new DataBulanan("3",saldo3));
        Log.d(TAG, "getDataHarian tgl3: "+saldo3+"bulan "+txtbulan);
        dataHari.add(new DataBulanan("4",saldo4));
        dataHari.add(new DataBulanan("5",saldo5));
        dataHari.add(new DataBulanan("6",saldo6));
        dataHari.add(new DataBulanan("7",saldo7));
        dataHari.add(new DataBulanan("8",saldo8));
        dataHari.add(new DataBulanan("9",saldo9));
        dataHari.add(new DataBulanan("10",saldo10));
        dataHari.add(new DataBulanan("11",saldo11));
        dataHari.add(new DataBulanan("12",saldo12));
        dataHari.add(new DataBulanan("13",saldo13));
        dataHari.add(new DataBulanan("14",saldo14));
        dataHari.add(new DataBulanan("15",saldo15));
        dataHari.add(new DataBulanan("16",saldo16));
        dataHari.add(new DataBulanan("17",saldo17));
        dataHari.add(new DataBulanan("18",saldo18));
        dataHari.add(new DataBulanan("19",saldo19));
        dataHari.add(new DataBulanan("20",saldo20));
        dataHari.add(new DataBulanan("21",saldo21));
        dataHari.add(new DataBulanan("22",saldo22));
        dataHari.add(new DataBulanan("23",saldo23));
        Log.d(TAG, "getDataHarian tgl23: "+saldo23+"bulan "+txtbulan);
        dataHari.add(new DataBulanan("24",saldo24));
        dataHari.add(new DataBulanan("25",saldo25));
        dataHari.add(new DataBulanan("26",saldo26));
        dataHari.add(new DataBulanan("27",saldo27));
        dataHari.add(new DataBulanan("28",saldo28));
        if (!txtbulan.equals("02")){
            dataHari.add(new DataBulanan("29",saldo29));
            dataHari.add(new DataBulanan("30",saldo30));
        }
        switch (txtbulan){
            case"01":
            case"03":
            case"05":
            case"07":
            case"08":
            case"10":
            case"12":
                dataHari.add(new DataBulanan("31",saldo31));
                break;
        }
    }

    private void saldoHarianClear() {
        saldo=0;
        saldo1=0;
        saldo2=0;
        saldo3=0;
        saldo4=0;
        saldo5=0;
        saldo6=0;
        saldo7=0;
        saldo8=0;
        saldo9=0;
        saldo10=0;
        saldo11=0;
        saldo12=0;
        saldo13=0;
        saldo14=0;
        saldo15=0;
        saldo16=0;
        saldo17=0;
        saldo18=0;
        saldo19=0;
        saldo20=0;
        saldo21=0;
        saldo22=0;
        saldo23=0;
        saldo24=0;
        saldo25=0;
        saldo26=0;
        saldo27=0;
        saldo28=0;
        saldo29=0;
        saldo30=0;
        saldo31=0;
        dataHari.clear();
        barChartHarian.fitScreen();
        barChartHarian.clear();
        barChartHarian.invalidate();
        barChartHarian.getXAxis().setValueFormatter(null);
        barDataSetHarian.clear();
        barDataHarian.clearValues();
    }

    //chart Bulanan
    private void createRecycleBulanan(String tahun) {
        Query query6 = fstore.collection("tb_transaksi")
                .document("Akuntan").collection("transaksi")
                .whereGreaterThanOrEqualTo("tanggal",tahun+"-"+"01"+"-01")
                .whereLessThanOrEqualTo("tanggal",tahun+"-"+"12"+"-32")
                .orderBy("tanggal", Query.Direction.DESCENDING);
        query6.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "chartHarian: Query Berhasil");

                Long lDebet;
                Long lKredit;

                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    Transaksi transaksi = documentSnapshot.toObject(Transaksi.class);
                    if (transaksi.getAkun2().equals(akun1)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                        Log.d(TAG, "chartHarian: Masuk Pendapatan debit, saldo :"+lDebet);
//                        Log.d(TAG, "Pendapatan: "+saldo+" "+saldoPendapatan);
                    }
                    else if(transaksi.getAkun1().equals(akun1)){
                        String txkredit = transaksi.getNominal().replaceAll(",", "");
                        if (txkredit.contains(",")) {
                            txkredit = txkredit.replaceAll(",", "");
                        }
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                        Log.d(TAG, "chartHarian: Masuk Pendapatan kredit, saldo :"+lKredit.intValue()*-1);
                    }

                    if(transaksi.getAkun1().equals(akun2)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun2)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }

                    if(transaksi.getAkun1().equals(akun3)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun3)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }

                    if(transaksi.getAkun1().equals(akun4)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun4)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }
                    if(transaksi.getAkun1().equals(akun5)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun5)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());

                    }

                    if(transaksi.getAkun1().equals(akun6)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun6)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }

                    if (transaksi.getAkun2().equals(akun7)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun7)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun8)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun8)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }
                    //Beban Penjualan
                    if (transaksi.getAkun2().equals(akun9)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun9)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun10)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun10)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }
                    //Beban Admin dan Umum
                    if (transaksi.getAkun2().equals(akun11)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun11)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun12)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun12)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun13)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun13)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun14)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }

                    if(transaksi.getAkun1().equals(akun14)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun15)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun15)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun16)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun16)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun17)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun17)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun18)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun18)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun19)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun19)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun20)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun20)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun21)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun21)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun22)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun22)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }

                    if (transaksi.getAkun2().equals(akun23)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }else if(transaksi.getAkun1().equals(akun23)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }
                    //Pendapatan Luar Usaha
                    if(transaksi.getAkun1().equals(akun24)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun24)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }

                    if(transaksi.getAkun1().equals(akun25)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun2().equals(akun25)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }

                    if(transaksi.getAkun2().equals(akun26)){
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1);
                    }else if (transaksi.getAkun1().equals(akun26)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue());
                    }

                    String totsaldo = formatter.format(saldo);



                }
            } else {
                Log.d(TAG, "Hasil Query Gagal");
            }
            chartBulanan();
        });
    }

    private void chartBulanan() {
        getDataBulanan();
        for (int i = 0; i < dataBulan.size(); i++){
            String month = dataBulan.get(i).getBulan();
            int sales = dataBulan.get(i).getAngka();
            barEntriesBulananArrayList.add(new BarEntry(i,sales));
            lableNameBulanan.add(month);
        }
        barDataSetBulanan = new BarDataSet(barEntriesBulananArrayList,"Transaksi Bulanan");
        barDataSetBulanan.setColors(ColorTemplate.COLORFUL_COLORS);

        Description description = new Description();
        description.setText("Transaksi Harian Tahun "+waktu.getTahun());
        barChartBulanan.setDescription(description);
        barDataBulanan = new BarData(barDataSetBulanan);
        barDataBulanan.setValueTextSize(8f);
        barChartBulanan.setData(barDataBulanan);



        XAxis xAxis= barChartBulanan.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(lableNameBulanan));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(lableNameBulanan.size());

        YAxis yAxis = barChartBulanan.getAxisRight();
        yAxis.setEnabled(false);

        barChartBulanan.animateY(2000);
        barChartBulanan.invalidate();
//        xAxis.setLabelRotationAngle(270);
        barChartBulanan.animateY(2000);
        barChartBulanan.invalidate();
    }

    private void getDataBulanan() {
        dataBulan.clear();
        dataBulan.add(new DataBulanan("Jan",saldoBulan1));
        dataBulan.add(new DataBulanan("Feb",saldoBulan2));
        dataBulan.add(new DataBulanan("Mar",saldoBulan3));
        dataBulan.add(new DataBulanan("Apr",saldoBulan4));
        dataBulan.add(new DataBulanan("Mei",saldoBulan5));
        dataBulan.add(new DataBulanan("Jun",saldoBulan6));
        dataBulan.add(new DataBulanan("Jul",saldoBulan7));
        dataBulan.add(new DataBulanan("Agt",saldoBulan8));
        dataBulan.add(new DataBulanan("Sep",saldoBulan9));
        dataBulan.add(new DataBulanan("Okt",saldoBulan10));
        dataBulan.add(new DataBulanan("Nov",saldoBulan11));
        dataBulan.add(new DataBulanan("Des",saldoBulan12));
    }

    private void setSaldoBulanan(String bulan, int saldoBulan) {
        if (bulan.equals("01")) {
            saldoBulan1 += saldoBulan;
            Log.d(TAG, "setSaldo Januari: " + saldoBulan1);
        }else if (bulan.equals("02")) {
            saldoBulan2 += saldoBulan;
            Log.d(TAG, "setSaldo Februari: " + saldoBulan2);
        }else if (bulan.equals("03")) {
            saldoBulan3 += saldoBulan;
            Log.d(TAG, "setSaldo Maret: " + saldoBulan3);
        }else if (bulan.equals("04")) {
            saldoBulan4 += saldoBulan;
            Log.d(TAG, "setSaldo April: " + saldoBulan4);
        }else if (bulan.equals("05")) {
            saldoBulan5 += saldoBulan;
            Log.d(TAG, "setSaldo Mei: " + saldoBulan5);
        }else if (bulan.equals("06")) {
            saldoBulan6 += saldoBulan;
            Log.d(TAG, "setSaldo Juni: " + saldoBulan6);
        }else if (bulan.equals("07")) {
            saldoBulan7 += saldoBulan;
            Log.d(TAG, "setSaldo Juli: " + saldoBulan7);
        }else if (bulan.equals("08")) {
            saldoBulan8 += saldoBulan;
            Log.d(TAG, "setSaldo Agustus: " + saldoBulan8);
        }else if (bulan.equals("09")) {
            saldoBulan9 += saldoBulan;
            Log.d(TAG, "setSaldo September: " + saldoBulan9);
        }else if (bulan.equals("10")) {
            saldoBulan10 += saldoBulan;
            Log.d(TAG, "setSaldo Oktober: " + saldoBulan10);
        }else if (bulan.equals("11")) {
            saldoBulan11 += saldoBulan;
            Log.d(TAG, "setSaldo November: " + saldoBulan11);
        }else{
            saldoBulan12 += saldoBulan;
            Log.d(TAG, "setSaldo November: " + saldoBulan12);
        }
    }

    private String convertHarga(String nominal) {
        String txdebet = nominal.replaceAll(",", "");
        if (txdebet.contains(",")) {
            txdebet = txdebet.replaceAll(",", "");
        }
        return txdebet;
    }
}