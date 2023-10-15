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


public class ChartFragment extends Fragment {
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
    int pajak1=0,pajak3=0,pajak2=0,pajak4=0,pajak5=0,pajak6=0,pajak7=0,pajak8=0,pajak9=0,pajak10=0,pajak11=0,pajak12=0,pajak13=0,pajak14=0,pajak15=0,pajak16=0,pajak17=0,pajak18=0,pajak19=0,pajak20=0,pajak21=0,pajak22=0,pajak23=0,pajak24=0,pajak25=0,pajak26=0,pajak27=0,pajak28=0,pajak29=0,pajak30=0,pajak31;
    int pajakBulan1=0,pajakBulan2=0,pajakBulan3=0,pajakBulan4=0,pajakBulan5=0,pajakBulan6=0,pajakBulan7=0,pajakBulan8=0,pajakBulan9=0,pajakBulan10=0,pajakBulan11=0,pajakBulan12=0;
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
                    if (transaksi.getTransaksi().equalsIgnoreCase("Pemasukan")){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        Log.d(TAG, "chartHarian: Masuk Pendapatan debit, saldo :"+lDebet);
                        setSaldo(tanggal.substring(8,10),lDebet.intValue(),lDebet.intValue());
                    }else{
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldo(tanggal.substring(8,10),lKredit.intValue()*-1,0);
                        Log.d(TAG, "chartHarian: Masuk Pendapatan kredit, saldo :"+lKredit);
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
        //Pendapatan
        String akun1 ="Pendapatan Usaha";
        String akun2 ="Pendapatan Lain-lain";
        String akun3 ="Beban Usaha";
        String akun4 ="Beban Lain-lain";

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

    private void setSaldo(String tanggal, int saldo, int pajak) {
        if (tanggal.equals("01")) {
                saldo1 += saldo;
                pajak1 += pajak;
        }else if (tanggal.equals("02")) {
            saldo2 += saldo;
            pajak2 += pajak;
        }else if (tanggal.equals("03")) {
            saldo3 += saldo;
            pajak3 += pajak;
        }else if (tanggal.equals("04")) {
            saldo4 += saldo;
            pajak4 += pajak;
        }else if (tanggal.equals("05")) {
            saldo5 += saldo;
            pajak5 += pajak;
        }else if (tanggal.equals("06")) {
            saldo6 += saldo;
            pajak6 += pajak;
        }else if (tanggal.equals("07")) {
            saldo7 += saldo;
            pajak7 += pajak;
        }else if (tanggal.equals("08")) {
            saldo8 += saldo;
            pajak8 += pajak;
        }else if (tanggal.equals("09")) {
            saldo9 += saldo;
            pajak9 += pajak;
        }else if (tanggal.equals("10")) {
            saldo10 += saldo;
            pajak10 += pajak;
        }else if (tanggal.equals("11")) {
            saldo11 += saldo;
            pajak11 += pajak;
        }else if (tanggal.equals("12")) {
            saldo12 += saldo;
            pajak12 += pajak;
        }else if (tanggal.equals("13")) {
            saldo13 += saldo;
            pajak13 += pajak;
        }else if (tanggal.equals("14")) {
            saldo14 += saldo;
            pajak14 += pajak;
        }else if (tanggal.equals("15")) {
            saldo15 += saldo;
            pajak15 += pajak;
        }else if (tanggal.equals("16")) {
            saldo16 += saldo;
            pajak16 += pajak;
        }else if (tanggal.equals("17")) {
            saldo17 += saldo;
            pajak17 += pajak;
        }else if (tanggal.equals("18")) {
            saldo18 += saldo;
            pajak18 += pajak;
        }else if (tanggal.equals("19")) {
            saldo19 += saldo;
            pajak19 += pajak;
        }else if (tanggal.equals("20")) {
            saldo20 += saldo;
            pajak20 += pajak;
        }else if (tanggal.equals("21")) {
            saldo21 += saldo;
            pajak21 += pajak;
        }else if (tanggal.equals("22")) {
            saldo22 += saldo;
            pajak22 += pajak;
        }else if (tanggal.equals("23")) {
            saldo23 += saldo;
            pajak23 += pajak;
        }else if (tanggal.equals("24")) {
            saldo24 += saldo;
            pajak24 += pajak;
        }else if (tanggal.equals("25")) {
            saldo25 += saldo;
            pajak25 += pajak;
        }else if (tanggal.equals("26")) {
            saldo26 += saldo;
            pajak26 += pajak;
        }else if (tanggal.equals("27")) {
            saldo27 += saldo;
            pajak27 += pajak;
        }else if (tanggal.equals("28")) {
            saldo28 += saldo;
            pajak28 += pajak;
        }else if (tanggal.equals("29")) {
            saldo29 += saldo;
            pajak29 += pajak;
        }else if (tanggal.equals("30")) {
            saldo30 += saldo;
            pajak30 += pajak;
        }else if (tanggal.equals("31")) {
            saldo31 += saldo;
            pajak31 += pajak;
        }
    }

    private void getDataHarian(){
        dataHari.clear();
        dataHari.add(new DataBulanan("1",saldo1-(pajak1*5/1000)));
        dataHari.add(new DataBulanan("2",saldo2-(pajak2*5/1000)));
        dataHari.add(new DataBulanan("3",saldo3-(pajak3*5/1000)));
        dataHari.add(new DataBulanan("4",saldo4-(pajak4*5/1000)));
        dataHari.add(new DataBulanan("5",saldo5-(pajak5*5/1000)));
        dataHari.add(new DataBulanan("6",saldo6-(pajak6*5/1000)));
        dataHari.add(new DataBulanan("7",saldo7-(pajak7*5/1000)));
        dataHari.add(new DataBulanan("8",saldo8-(pajak8*5/1000)));
        dataHari.add(new DataBulanan("9",saldo9-(pajak9*5/1000)));
        dataHari.add(new DataBulanan("10",saldo10-(pajak10*5/1000)));
        dataHari.add(new DataBulanan("11",saldo11-(pajak11*5/1000)));
        dataHari.add(new DataBulanan("12",saldo12-(pajak12*5/1000)));
        dataHari.add(new DataBulanan("13",saldo13-(pajak13*5/1000)));
        dataHari.add(new DataBulanan("14",saldo14-(pajak14*5/1000)));
        dataHari.add(new DataBulanan("15",saldo15-(pajak15*5/1000)));
        dataHari.add(new DataBulanan("16",saldo16-(pajak16*5/1000)));
        dataHari.add(new DataBulanan("17",saldo17-(pajak17*5/1000)));
        dataHari.add(new DataBulanan("18",saldo18-(pajak18*5/1000)));
        dataHari.add(new DataBulanan("19",saldo19-(pajak19*5/1000)));
        dataHari.add(new DataBulanan("20",saldo20-(pajak20*5/1000)));
        dataHari.add(new DataBulanan("21",saldo21-(pajak21*5/1000)));
        dataHari.add(new DataBulanan("22",saldo22-(pajak22*5/1000)));
        dataHari.add(new DataBulanan("23",saldo23-(pajak23*5/1000)));
        dataHari.add(new DataBulanan("24",saldo24-(pajak24*5/1000)));
        dataHari.add(new DataBulanan("25",saldo25-(pajak25*5/1000)));
        dataHari.add(new DataBulanan("26",saldo26-(pajak26*5/1000)));
        dataHari.add(new DataBulanan("27",saldo27-(pajak27*5/1000)));
        dataHari.add(new DataBulanan("28",saldo28-(pajak28*5/1000)));
        if (!txtbulan.equals("02")){
            dataHari.add(new DataBulanan("29",saldo29-(pajak29*5/1000)));
            dataHari.add(new DataBulanan("30",saldo30-(pajak30*5/1000)));
        }
        switch (txtbulan){
            case"01":
            case"03":
            case"05":
            case"07":
            case"08":
            case"10":
            case"12":
                dataHari.add(new DataBulanan("31",saldo31-(pajak31*5/1000)));
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
        pajak1=0;
        pajak3=0;
        pajak2=0;
        pajak4=0;
        pajak5=0;
        pajak6=0;
        pajak7=0;
        pajak8=0;
        pajak9=0;
        pajak10=0;
        pajak11=0;
        pajak12=0;
        pajak13=0;
        pajak14=0;
        pajak15=0;
        pajak16=0;
        pajak17=0;
        pajak18=0;
        pajak19=0;
        pajak20=0;
        pajak21=0;
        pajak22=0;
        pajak23=0;
        pajak24=0;
        pajak25=0;
        pajak26=0;
        pajak27=0;
        pajak28=0;
        pajak29=0;
        pajak30=0;
        pajak31=0;
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
                    if (transaksi.getTransaksi().equalsIgnoreCase("Pemasukan")){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        Log.d(TAG, "chartHarian: Masuk Pendapatan debit, saldo :"+lDebet);
                        setSaldoBulanan(tanggal.substring(5,7),lDebet.intValue(),lDebet.intValue());
                    }else{
                        lKredit = Long.parseLong(convertHarga(transaksi.getNominal()));
                        tanggal = transaksi.getTanggal();
                        setSaldoBulanan(tanggal.substring(5,7),lKredit.intValue()*-1,0);
                        Log.d(TAG, "chartHarian: Masuk Pendapatan kredit, saldo :"+lKredit);
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
        dataBulan.add(new DataBulanan("Jan",saldoBulan1-(pajakBulan1*5/1000)));
        dataBulan.add(new DataBulanan("Feb",saldoBulan2-(pajakBulan2*5/1000)));
        dataBulan.add(new DataBulanan("Mar",saldoBulan3-(pajakBulan3*5/1000)));
        dataBulan.add(new DataBulanan("Apr",saldoBulan4-(pajakBulan4*5/1000)));
        dataBulan.add(new DataBulanan("Mei",saldoBulan5-(pajakBulan5*5/1000)));
        dataBulan.add(new DataBulanan("Jun",saldoBulan6-(pajakBulan6*5/1000)));
        dataBulan.add(new DataBulanan("Jul",saldoBulan7-(pajakBulan7*5/1000)));
        dataBulan.add(new DataBulanan("Agt",saldoBulan8-(pajakBulan8*5/1000)));
        dataBulan.add(new DataBulanan("Sep",saldoBulan9-(pajakBulan9*5/1000)));
        dataBulan.add(new DataBulanan("Okt",saldoBulan10-(pajakBulan10*5/1000)));
        dataBulan.add(new DataBulanan("Nov",saldoBulan11-(pajakBulan11*5/1000)));
        dataBulan.add(new DataBulanan("Des",saldoBulan12-(pajakBulan12*5/1000)));
    }

    private void setSaldoBulanan(String bulan, int saldoBulan, int pajak) {
        if (bulan.equals("01")) {
            saldoBulan1 += saldoBulan;
            pajakBulan1 += pajak;
            Log.d(TAG, "setSaldo Januari: " + saldoBulan1);
        }else if (bulan.equals("02")) {
            saldoBulan2 += saldoBulan;
            pajakBulan2 += pajak;
            Log.d(TAG, "setSaldo Februari: " + saldoBulan2);
        }else if (bulan.equals("03")) {
            saldoBulan3 += saldoBulan;
            pajakBulan3 += pajak;
            Log.d(TAG, "setSaldo Maret: " + saldoBulan3);
        }else if (bulan.equals("04")) {
            saldoBulan4 += saldoBulan;
            pajakBulan4 += pajak;
            Log.d(TAG, "setSaldo April: " + saldoBulan4);
        }else if (bulan.equals("05")) {
            saldoBulan5 += saldoBulan;
            pajakBulan5 += pajak;
            Log.d(TAG, "setSaldo Mei: " + saldoBulan5);
        }else if (bulan.equals("06")) {
            saldoBulan6 += saldoBulan;
            pajakBulan6 += pajak;
            Log.d(TAG, "setSaldo Juni: " + saldoBulan6);
        }else if (bulan.equals("07")) {
            saldoBulan7 += saldoBulan;
            pajakBulan7 += pajak;
            Log.d(TAG, "setSaldo Juli: " + saldoBulan7);
        }else if (bulan.equals("08")) {
            saldoBulan8 += saldoBulan;
            pajakBulan8 += pajak;
            Log.d(TAG, "setSaldo Agustus: " + saldoBulan8);
        }else if (bulan.equals("09")) {
            saldoBulan9 += saldoBulan;
            pajakBulan9 += pajak;
            Log.d(TAG, "setSaldo September: " + saldoBulan9);
        }else if (bulan.equals("10")) {
            saldoBulan10 += saldoBulan;
            pajakBulan10 += pajak;
            Log.d(TAG, "setSaldo Oktober: " + saldoBulan10);
        }else if (bulan.equals("11")) {
            saldoBulan11 += saldoBulan;
            pajakBulan11 += pajak;
            Log.d(TAG, "setSaldo November: " + saldoBulan11);
        }else{
            saldoBulan12 += saldoBulan;
            pajakBulan12 += pajak;
            Log.d(TAG, "setSaldo Desember: " + saldoBulan12);
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