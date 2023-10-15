package com.pranes.akuntansisijuproliman.ui.laporan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.adapter.LaporanLabaRugiAdapter;
import com.pranes.akuntansisijuproliman.model.Transaksi;
import com.pranes.akuntansisijuproliman.model.Waktu;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


public class LaporanFragment extends Fragment {

    private LaporanLabaRugiAdapter adapter;
    Waktu waktu;
    String txtbulan1, txtbulan2, bulan,tanggal, TAG="Penghasilan";
    PowerSpinnerView bulan1, bulan2;
    View view;
    private ProgressBar progressBar;
    ImageView btnPrint;
    DecimalFormat formatter;
    private FirebaseFirestore fstore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //Pendapatan
    int saldoPendapatanUsaha=0, saldoPendapatanLain =0, saldoTotalPendapatan=0;
    int saldo = 0;
    //Harga Pokok
    int saldoHargaPokok=0, saldoBahanbaku=0, saldoBarangDagang=0;
    //Pengeluaran
    int saldoBebanGaji=0,saldoBebanListrik=0,saldoBebanAir=0,saldoBebansewa=0, saldoBebanLain =0, saldoTotalBeban=0;
    //Pendapatan
    String akun1 ="Pendapatan Usaha";
    String akun2 ="Pendapatan Lain-lain";
    //Pengeluaran
    String akun3 ="Harga Pokok Penjulalan";
    String akun4 ="Persediaan Bahan Baku";
    String akun5 ="Persediaan Bahan Dagang";
    String akun6 ="Beban Gaji Karyawan";
    String akun7 ="Beban Listrik";
    String akun8 ="Beban Air";
    String akun9 ="Beban Sewa";
    String akun10 = "Beban Lain-lain";
    Document document;
    Cell angka_pendapatan,sPendapatanUsaha,sPendapatanLain,sPendapatanTotal,shargaPokok,slabarugiKotor,pendapatan,pendapatanUsaha,pendapatanLain,pendapatanTotal,hargaPokok,labarugiKotor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_laporan, container, false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        fstore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        initView();
        //permission
        ActivityCompat.requestPermissions((Activity) getContext(),
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                }, 1
        );
    }

    private void initView() {
        progressBar = view.findViewById(R.id.progressbar);
        Sprite sprite = new Circle();
        progressBar.setIndeterminateDrawable(sprite);
        waktu = new Waktu();
        bulan1 = view.findViewById(R.id.drBulan);
        bulan2 = view.findViewById(R.id.smpBulan);
//        mMonth = c.get(Calendar.MONTH);
//        month = mMonth+1;
        bulan=waktu.getBulan();
//        Toast.makeText(LapLabaRugiActivity.this, bulh, Toast.LENGTH_SHORT).show();
        bulan1.setText(bulan);
        bulan2.setText(bulan);
        txtbulan1=bulan;
        txtbulan2=bulan;
        btnPrint = view.findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                ArrayList<Integer> x = new ArrayList<Integer>();


                try {
                    int a1 = Integer.parseInt(txtbulan1);
                    int a2 = Integer.parseInt(txtbulan2);
                    int a = a2-a1+1;
                    String pdfpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
                    File file = new File(pdfpath, waktu.getHari()+"-Laporan Laba Rugi Bulan "+waktu.getNamaBulan(waktu.get0bulan(a1))+"-"+waktu.getNamaBulan(waktu.get0bulan(a2))+".pdf");
                    OutputStream outputStream = new FileOutputStream(file);
                    PdfWriter writer = new PdfWriter(file);
                    PdfDocument pdfDocument = new PdfDocument(writer);
                    document = new Document(pdfDocument);
                    pdfDocument.setDefaultPageSize(PageSize.A4);
                    for (int hitungan = 1; hitungan <= a; hitungan++) {
                        Log.d(TAG, "hitungan: "+hitungan);
                        clearSaldo();
                                createPdf(a1);
                        a1++;
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            document.close();
                            Log.d(TAG, "Penghasilan cetak: ");
                            Toast.makeText(getContext(), "Laporan Telah Dibuat di Folder Document", Toast.LENGTH_SHORT).show();

                        }
                    },3000L *a);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });
        createRecycle(waktu.get1Bulan(),waktu.get1Bulan());
        bulan1.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
//                Toast.makeText(LapLabaRugiActivity.this, newItem + " selected!", Toast.LENGTH_SHORT).show();
                txtbulan1 =newItem;
                int bulann1 = Integer.parseInt(txtbulan1);
                int bulann2 = Integer.parseInt(txtbulan2);
                if (bulann1>bulann2){
                    txtbulan2 = txtbulan1;
                    bulan2.setText(txtbulan2);
                }
//                Toast.makeText(LapLabaRugiActivity.this,  txtbulan1+" "+txtbulan2, Toast.LENGTH_SHORT).show();
                createRecycle(bulann1,bulann2);
            }
        });
        bulan2.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
//                Toast.makeText(LapLabaRugiActivity.this, newItem + " selected!", Toast.LENGTH_SHORT).show();
                txtbulan2 =newItem;
                int bulann1 = Integer.parseInt(txtbulan1);
                int bulann2 = Integer.parseInt(txtbulan2);
                if (bulann2<bulann1){
                    txtbulan1 = txtbulan2;
                    bulan1.setText(txtbulan1);
                }
                createRecycle(bulann1,bulann2);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void createPdf(int bulan1) throws FileNotFoundException {
        // If you have access to the external storage, do whatever you need
        if (Environment.isExternalStorageManager()){

// If you don't have access, launch a new activity to show the user the system's dialog
// to allow access to the external storage
        }else{
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

            document.setMargins(34,70,14,70);
            Query query6 = fstore.collection("tb_transaksi")
                    .document("Akuntan").collection("transaksi")
                    .whereGreaterThanOrEqualTo("tanggal",waktu.getTahun()+"-"+waktu.get0bulan(bulan1)+"-01")
                    .whereLessThanOrEqualTo("tanggal",waktu.getTahun()+"-"+waktu.get0bulan(bulan1)+"-32")
                    .orderBy("tanggal", Query.Direction.DESCENDING);
            Toast.makeText(getContext(), "Menyiapkan Dokumen Bulan "+waktu.getNamaBulan(waktu.get0bulan(bulan1)), Toast.LENGTH_SHORT).show();
            query6.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "Query Berhasil: "+waktu.getNamaBulan(waktu.get0bulan(bulan1)));
                                    clearSaldo();
                                    Long lDebet;
                                    int IntDebet = 0, IntKredit = 0;
                                    Long lKredit;
                                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                        Transaksi transaksi = documentSnapshot.toObject(Transaksi.class);
                                        if (transaksi.getJenis_transaksi().equals(akun1)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun2)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun3)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun4)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun5)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun6)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun7)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun8)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun9)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }else if (transaksi.getJenis_transaksi().equals(akun10)){
                                            lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                                            setSaldoBulanan(transaksi.getJenis_transaksi(),lDebet.intValue());
                                        }
                                    }
                                    Table table = new Table(UnitValue.createPercentArray(20)).useAllAvailableWidth().setFixedLayout();
                                    table.setHorizontalAlignment(HorizontalAlignment.CENTER);
                                    Log.d(TAG, "Deklarasi Tabel: ");

                                    Paragraph spasi = new Paragraph("");
                                    spasi.setHeight(30);

                                    Paragraph spasi2 = new Paragraph("");
                                    spasi2.setHeight(15);

                                    Cell pendapatan = new Cell(1, 15).add(new Paragraph("PENDAPATAN").setBold());
                                    pendapatan.setBorder(Border.NO_BORDER);
                                    table.addCell(pendapatan);
                                    Cell angka_pendapatan = new Cell(1, 5);
                                    angka_pendapatan.setTextAlignment(TextAlignment.LEFT);
                                    angka_pendapatan.setBorder(Border.NO_BORDER);
                                    table.addCell(angka_pendapatan);

                                    Cell pendapatanUsaha = new Cell(1, 15).add(new Paragraph("Pendapatan Usaha"));
                                    pendapatanUsaha.setBorder(Border.NO_BORDER);
                                    table.addCell(pendapatanUsaha);
                                    Cell sPendapatanUsaha = new Cell(1, 5);
                                    sPendapatanUsaha.setBorder(Border.NO_BORDER);
                                    sPendapatanUsaha.setTextAlignment(TextAlignment.LEFT);
                                    table.addCell(sPendapatanUsaha);

                                    Cell pendapatanLain = new Cell(1, 15).add(new Paragraph("Pendapatan Lain"));
                                    pendapatanLain.setBorder(Border.NO_BORDER);
                                    table.addCell(pendapatanLain);
                                    Cell sPendapatanLain = new Cell(1, 5);
                                    sPendapatanLain.setBorder(Border.NO_BORDER);
                                    sPendapatanLain.setTextAlignment(TextAlignment.LEFT);
                                    table.addCell(sPendapatanLain);

                                    Cell pendapatanTotal = new Cell(1, 15).add(new Paragraph("TOTAL PENDAPATAN").setBold().setItalic());
                                    pendapatanTotal.setBorder(Border.NO_BORDER);
                                    table.addCell(pendapatanTotal);
                                    Cell sPendapatanTotal = new Cell(1, 5);
                                    sPendapatanTotal.setTextAlignment(TextAlignment.RIGHT);
                                    sPendapatanTotal.setBorder(Border.NO_BORDER);
                                    table.addCell(sPendapatanTotal);

                                    Cell hargaPokok = new Cell(1, 15).add(new Paragraph("Harga Pokok Penjualan"));
                                    hargaPokok.setBorder(Border.NO_BORDER);
                                    table.addCell(hargaPokok);
                                    Cell shargaPokok = new Cell(1, 5);
                                    shargaPokok.setBorderTop(Border.NO_BORDER);
                                    shargaPokok.setBorderLeft(Border.NO_BORDER);
                                    shargaPokok.setBorderRight(Border.NO_BORDER);
                                    shargaPokok.setTextAlignment(TextAlignment.RIGHT);
                                    table.addCell(shargaPokok);



                                    Cell labarugiKotor = new Cell(1, 15).add(new Paragraph("LABA RUGI KOTOR").setBold().setItalic());
                                    labarugiKotor.setBorder(Border.NO_BORDER);
                                    table.addCell(labarugiKotor);
                                    Cell slabarugiKotor = new Cell(1, 5);
                                    slabarugiKotor.setTextAlignment(TextAlignment.RIGHT);
                                    slabarugiKotor.setBorder(Border.NO_BORDER);
                                    table.addCell(slabarugiKotor);

                                    Cell beban = new Cell(1, 15).add(new Paragraph("BEBAN").setBold());
                                    beban.setBorder(Border.NO_BORDER);
                                    table.addCell(beban);
                                    Cell angka_beban = new Cell(1, 5);
                                    angka_beban.setBorder(Border.NO_BORDER);
                                    table.addCell(angka_beban);

                                    Cell persedaiaanbaku = new Cell(1, 15).add(new Paragraph("Persediaan Bahan Baku"));
                                    persedaiaanbaku.setBorder(Border.NO_BORDER);
                                    table.addCell(persedaiaanbaku);
                                    Cell spersedaiaanbaku = new Cell(1, 5);
                                    spersedaiaanbaku.setTextAlignment(TextAlignment.LEFT);
                                    spersedaiaanbaku.setBorder(Border.NO_BORDER);
                                    table.addCell(spersedaiaanbaku);

                                    Cell persedaiaandagang = new Cell(1, 15).add(new Paragraph("Persediaan Barang Dagang"));
                                    persedaiaandagang.setBorder(Border.NO_BORDER);
                                    table.addCell(persedaiaandagang);
                                    Cell spersedaiaandagang = new Cell(1, 5);
                                    spersedaiaandagang.setTextAlignment(TextAlignment.LEFT);
                                    spersedaiaandagang.setBorder(Border.NO_BORDER);
                                    table.addCell(spersedaiaandagang);

                                    Cell gajikaryawan = new Cell(1, 15).add(new Paragraph("Beban Gaji Karyawan"));
                                    gajikaryawan.setBorder(Border.NO_BORDER);
                                    table.addCell(gajikaryawan);
                                    Cell sgajikaryawan = new Cell(1, 5);
                                    sgajikaryawan.setTextAlignment(TextAlignment.LEFT);
                                    sgajikaryawan.setBorder(Border.NO_BORDER);
                                    table.addCell(sgajikaryawan);

                                    Cell listrik = new Cell(1, 15).add(new Paragraph("Beban Listrik"));
                                    listrik.setBorder(Border.NO_BORDER);
                                    table.addCell(listrik);
                                    Cell slistrik = new Cell(1, 5);
                                    slistrik.setTextAlignment(TextAlignment.LEFT);
                                    slistrik.setBorder(Border.NO_BORDER);
                                    table.addCell(slistrik);

                                    Cell air = new Cell(1, 15).add(new Paragraph("Beban Air"));
                                    air.setBorder(Border.NO_BORDER);
                                    table.addCell(air);
                                    Cell sair = new Cell(1, 5);
                                    sair.setTextAlignment(TextAlignment.LEFT);
                                    sair.setBorder(Border.NO_BORDER);
                                    table.addCell(sair);

                                    Cell sewa = new Cell(1, 15).add(new Paragraph("Beban Sewa"));
                                    sewa.setBorder(Border.NO_BORDER);
                                    table.addCell(sewa);
                                    Cell ssewa = new Cell(1, 5);
                                    ssewa.setTextAlignment(TextAlignment.LEFT);
                                    ssewa.setBorder(Border.NO_BORDER);
                                    table.addCell(ssewa);

                                    Cell bebanlain = new Cell(1, 15).add(new Paragraph("Beban Lain-lain"));
                                    bebanlain.setBorder(Border.NO_BORDER);
                                    table.addCell(bebanlain);
                                    Cell sbebanlain = new Cell(1, 5);
                                    sbebanlain.setTextAlignment(TextAlignment.LEFT);
                                    sbebanlain.setBorder(Border.NO_BORDER);
                                    table.addCell(sbebanlain);

                                    Cell jumlahbeban = new Cell(1, 15).add(new Paragraph("JUMLAH BEBAN").setBold().setItalic());
                                    jumlahbeban.setBorder(Border.NO_BORDER);
                                    table.addCell(jumlahbeban);
                                    Cell sjumlahbeban = new Cell(1, 5);
                                    sjumlahbeban.setTextAlignment(TextAlignment.RIGHT);
                                    sjumlahbeban.setBorder(Border.NO_BORDER);
                                    table.addCell(sjumlahbeban);



                                    Cell labaSpajak = new Cell(1, 15).add(new Paragraph("LABA RUGI SEBELUM PAJAK PENGHASILAN").setBold());
                                    labaSpajak.setBorder(Border.NO_BORDER);
                                    table.addCell(labaSpajak);
                                    Cell slabaSpajak = new Cell(1, 5);
                                    slabaSpajak.setTextAlignment(TextAlignment.RIGHT);
                                    slabaSpajak.setBorder(Border.NO_BORDER);
                                    table.addCell(slabaSpajak);

                                    Cell pajak = new Cell(1, 15).add(new Paragraph("Beban Pajak Penghasilan"));
                                    pajak.setBorder(Border.NO_BORDER);
                                    table.addCell(pajak);
                                    Cell spajak = new Cell(1, 5);
                                    spajak.setTextAlignment(TextAlignment.LEFT);
                                    spajak.setBorder(Border.NO_BORDER);
                                    table.addCell(spajak);

                                    Cell labarugibersih = new Cell(1, 15).add(new Paragraph("LABA RUGI BERSIH").setBold());
                                    labarugibersih.setBorder(Border.NO_BORDER);
                                    table.addCell(labarugibersih);
                                    Cell slabarugibersih = new Cell(1, 5);
                                    slabarugibersih.setTextAlignment(TextAlignment.RIGHT);
                                    slabarugibersih.setBorder(Border.NO_BORDER);
                                    table.addCell(slabarugibersih);

                                    Cell cell = new Cell();
                                    cell.deleteOwnProperty(Property.MIN_HEIGHT);
                                    table.setExtendBottomRow(true);

                                    sPendapatanUsaha.add(new Paragraph(formatter.format(saldoPendapatanUsaha)));
                                    sPendapatanLain.add(new Paragraph(formatter.format(saldoPendapatanLain)));
                                    sPendapatanTotal.add(new Paragraph(formatter.format(saldoTotalPendapatan)));
                                    shargaPokok.add(new Paragraph(formatter.format(saldoHargaPokok)));
                                    slabarugiKotor.add(new Paragraph(formatter.format(saldoTotalPendapatan-saldoHargaPokok)));
                                    spersedaiaanbaku.add(new Paragraph(formatter.format(saldoBahanbaku)));
                                    spersedaiaandagang.add(new Paragraph(formatter.format(saldoBarangDagang)));
                                    sgajikaryawan.add(new Paragraph(formatter.format(saldoBebanGaji)));
                                    slistrik.add(new Paragraph(formatter.format(saldoBebanListrik)));
                                    sair.add(new Paragraph(formatter.format(saldoBebanAir)));
                                    ssewa.add(new Paragraph(formatter.format(saldoBebansewa)));
                                    sbebanlain.add(new Paragraph(formatter.format(saldoBebanLain)));
                                    sjumlahbeban.add(new Paragraph(formatter.format(saldoTotalBeban)));

                                    int apajak = saldoTotalPendapatan*5/1000;

                                    slabaSpajak.add(new Paragraph(formatter.format(saldo)));
                                    spajak.add(new Paragraph(formatter.format(apajak)));

                                    //Total Laba Rugi
                                    int labarugi = saldo-apajak;
                                    slabarugibersih.add(new Paragraph(formatter.format(saldo-apajak)));

                                    Paragraph judul = new Paragraph("Laporan Laba Rugi Bulan: "+waktu.getNamaBulan(waktu.get0bulan(bulan1))+" "+waktu.getTahun()).setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER);

                                    document.add(judul);
                                    document.add(spasi);
                                    document.add(table);
                                    clearSaldo();

                                }
                            }, 2000L *bulan1);
                } else {
                    Log.d(TAG, "Hasil Query Gagal");
                }
            });

    }


    private void clearSaldo() {


        //Pendapatan
        saldoPendapatanUsaha=0; saldoPendapatanLain =0; saldoTotalPendapatan=0;
        saldo = 0;
        //Harga Pokok
        saldoHargaPokok=0; saldoBahanbaku=0; saldoBarangDagang=0;
        //Pengeluaran
        saldoBebanGaji=0;saldoBebanListrik=0;saldoBebanAir=0;saldoBebansewa=0; saldoBebanLain =0; saldoTotalBeban=0;
    }

    private void setSaldoBulanan(String jenis_transaksi, int lDebet) {
        if (jenis_transaksi.equalsIgnoreCase(akun1)){
            saldoPendapatanUsaha += lDebet;
            saldoTotalPendapatan += lDebet;
            saldo += lDebet;
            Log.d(TAG, "Penghasilan Usaha Query Looping bulan: "+" ="+saldoPendapatanUsaha);
        }else if (jenis_transaksi.equalsIgnoreCase(akun2)){
            saldoPendapatanLain += lDebet;
            saldoTotalPendapatan += lDebet;
            saldo += lDebet;
        }else if (jenis_transaksi.equalsIgnoreCase(akun3)){
            saldoHargaPokok += lDebet;
            saldo -= lDebet;
        }else if (jenis_transaksi.equalsIgnoreCase(akun4)){
            saldoBahanbaku += lDebet;
            saldoTotalBeban += lDebet;
            saldo -= lDebet;
        }else if (jenis_transaksi.equalsIgnoreCase(akun5)){
            saldoBarangDagang += lDebet;
            saldoTotalBeban += lDebet;
            saldo -= lDebet;
        }else if (jenis_transaksi.equalsIgnoreCase(akun6)){
            saldoBebanGaji += lDebet;
            saldoTotalBeban += lDebet;
            saldo -= lDebet;
        }else if (jenis_transaksi.equalsIgnoreCase(akun7)){
            saldoBebanListrik += lDebet;
            saldoTotalBeban += lDebet;
            saldo -= lDebet;
        }else if (jenis_transaksi.equalsIgnoreCase(akun8)){
            saldoBebanAir += lDebet;
            saldoTotalBeban += lDebet;
            saldo -= lDebet;
        }else if (jenis_transaksi.equalsIgnoreCase(akun9)){
            saldoBebansewa += lDebet;
            saldoTotalBeban += lDebet;
            saldo -= lDebet;
        }else if (jenis_transaksi.equalsIgnoreCase(akun10)){
            saldoBebanLain += lDebet;
            saldoTotalBeban += lDebet;
            saldo -= lDebet;
        }
    }

    private String convertHarga(String nominal) {
        String txdebet = nominal.replaceAll(",", "");
        if (txdebet.contains(",")) {
            txdebet = txdebet.replaceAll(",", "");
        }
        return txdebet;
    }

    private void createRecycle(int bulann1, int bulann2) {
        ArrayList<String> namaBulan = new ArrayList<>();
        int a1 = bulann1;
        int a2 = bulann2;
        int a = a2-a1;
        for (int hitungan = 0; hitungan <= a; hitungan++) {
            namaBulan.add(String.valueOf(a1));
            a1++;
        }

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rvLabaRugi);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new LaporanLabaRugiAdapter(getContext(),  namaBulan);
//        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "onCreate: "+adapter.getItemCount());
    }
}