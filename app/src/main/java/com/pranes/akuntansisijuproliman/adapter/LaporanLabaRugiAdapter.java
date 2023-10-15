package com.pranes.akuntansisijuproliman.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.model.Transaksi;
import com.pranes.akuntansisijuproliman.model.Waktu;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LaporanLabaRugiAdapter extends RecyclerView.Adapter<LaporanLabaRugiAdapter.ViewHolder> {
    private static final String TAG = "TAG";
    private List<String> mBulan;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseFirestore fstore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String uid;
    DecimalFormat formatter;
    Waktu waktu;
    int a1,bersih;
    // data is passed into the constructor
    public LaporanLabaRugiAdapter(Context context, List<String> bulan) {
        this.mInflater = LayoutInflater.from(context);

        this.mBulan = bulan;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_laporan_labarugi, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        fstore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        waktu = new Waktu();
        String namaBulan = mBulan.get(position);
        String tahun = waktu.getTahun();
//        Toast.makeText(mInflater.getContext(), tahun, Toast.LENGTH_SHORT).show();
         a1 = Integer.parseInt(namaBulan);
        String bulan0 = waktu.get0bulan(a1);
        String bulan = waktu.getNamaBulan(bulan0);
//        if (a1<10){
//            bulan0 = "0"+a1;
//        }
        holder.tvbulan.setText(bulan);
         uid = firebaseUser.getUid();

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



        Query query6 = fstore.collection("tb_transaksi")
                .document("Akuntan").collection("transaksi")
                .whereGreaterThanOrEqualTo("tanggal",tahun+"-"+bulan0+"-01")
                .whereLessThanOrEqualTo("tanggal",tahun+"-"+bulan0+"-32")
                .orderBy("tanggal", Query.Direction.DESCENDING);
        query6.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int saldo = 0;
                Long lDebet;
                int IntDebet = 0, IntKredit = 0;
                Long lKredit;

                //Pendapatan
                int saldoPendapatanUsaha=0, saldoPendapatanLain =0, saldoTotalPendapatan=0;

                //Harga Pokok
                int saldoHargaPokok=0, saldoBahanbaku=0, saldoBarangDagang=0;

                //Pengeluaran
                int saldoBebanGaji=0,saldoBebanListrik=0,saldoBebanAir=0,saldoBebansewa=0, saldoBebanLain =0, saldoTotalBeban=0;





                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    Transaksi transaksi = documentSnapshot.toObject(Transaksi.class);
                    if (transaksi.getJenis_transaksi().equals(akun1)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoPendapatanUsaha += lDebet;
                        saldoTotalPendapatan += lDebet;
                        saldo += lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun2)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoPendapatanLain += lDebet;
                        saldoTotalPendapatan += lDebet;
                        saldo += lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun3)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoHargaPokok += lDebet;
                        saldo -= lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun4)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoBahanbaku += lDebet;
                        saldoTotalBeban += lDebet;
                        saldo -= lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun5)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoBarangDagang += lDebet;
                        saldoTotalBeban += lDebet;
                        saldo -= lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun6)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoBebanGaji += lDebet;
                        saldoTotalBeban += lDebet;
                        saldo -= lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun7)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoBebanListrik += lDebet;
                        saldoTotalBeban += lDebet;
                        saldo -= lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun8)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoBebanAir += lDebet;
                        saldoTotalBeban += lDebet;
                        saldo -= lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun9)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoBebansewa += lDebet;
                        saldoTotalBeban += lDebet;
                        saldo -= lDebet;
                    }else if (transaksi.getJenis_transaksi().equals(akun10)){
                        lDebet = Long.parseLong(convertHarga(transaksi.getNominal()));
                        saldoBebanLain += lDebet;
                        saldoTotalBeban += lDebet;
                        saldo -= lDebet;
                    }



                    String totsaldo = formatter.format(saldo);

                    //Pendapatan
                    holder.tvpendapatanusaha.setText(formatter.format(saldoPendapatanUsaha));//1
                    holder.tvpendapatanlain.setText(formatter.format(saldoPendapatanLain));//2
                    holder.tvtotalpendapatan.setText(formatter.format(saldoTotalPendapatan));

                    //Harga Pokok
                    holder.tvhargapokok.setText(formatter.format(saldoHargaPokok));//3
                    holder.tvLabaRugikotor.setText(formatter.format(saldoTotalPendapatan-saldoHargaPokok));

                    //Beban
                    holder.tvbarangbaku.setText(formatter.format(saldoBahanbaku));//4
                    holder.tvbarangdagang.setText(formatter.format(saldoBarangDagang));//5
                    holder.tvbebabgaji.setText(formatter.format(saldoBebanGaji));//6
                    holder.tvbebanlistrik.setText(formatter.format(saldoBebanListrik));//7
                    holder.tvbebanair.setText(formatter.format(saldoBebanAir));//8
                    holder.tvbebansewa.setText(formatter.format(saldoBebansewa));//9
                    holder.tvbebanlain.setText(formatter.format(saldoBebanLain));//10
                    holder.tvtotalbeban.setText(formatter.format(saldoTotalBeban));
                    int pajak = saldoTotalPendapatan*5/1000;

                    holder.tvLabaRugiBelumpajak.setText("\n"+formatter.format(saldo));
                    holder.tvpajak.setText(formatter.format(pajak));

                    //Total Laba Rugi
                    int labarugi = saldo-pajak;
                    holder.tvLabaRugi.setText(formatter.format(saldo-pajak));

                }
            } else {
                Log.d(TAG, "Hasil Query Gagal");
            }
        });



    }



    // total number of rows
    @Override
    public int getItemCount() {
        return mBulan.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Pendapatan
        TextView tvbulan, tvpendapatanusaha, tvpendapatanlain, tvtotalpendapatan, tvbebabgaji, tvbebanlistrik, tvtotalbeban, tvpajak, tvLabaRugi, tvLabaRugikotor, tvhargapokok;
        TextView tvbebanlain, tvbarangbaku, tvbarangdagang, tvbebanair, tvbebansewa, tvLabaRugiBelumpajak;



        ViewHolder(View itemView) {
            super(itemView);
            tvbulan = itemView.findViewById(R.id.bulantahun);

            //Pendapatan
            tvpendapatanusaha = itemView.findViewById(R.id.tv_pendapatan_usaha);
            tvpendapatanlain = itemView.findViewById(R.id.tv_pendapatan_lain);
            tvtotalpendapatan = itemView.findViewById(R.id.tv_total_pendapatan);

            //Harga pokok
            tvhargapokok = itemView.findViewById(R.id.tv_harga_pokok);

            //Beban
            tvbarangbaku = itemView.findViewById(R.id.tv_bahanbaku);
            tvbarangdagang = itemView.findViewById(R.id.tv_barangdagang);

            tvbebabgaji = itemView.findViewById(R.id.tv_beban_gaji);
            tvbebanlistrik = itemView.findViewById(R.id.tv_beban_listrik);
            tvbebanair = itemView.findViewById(R.id.tv_beban_air);
            tvbebansewa = itemView.findViewById(R.id.tv_beban_sewa);
            tvbebanlain = itemView.findViewById(R.id.tv_beban_lain);
            tvtotalbeban = itemView.findViewById(R.id.tv_total_beban);

            //Labarugi Kotor
            tvLabaRugikotor = itemView.findViewById(R.id.tv_labarugi_kotor);
            tvLabaRugiBelumpajak = itemView.findViewById(R.id.tv_labarugi_belumpajak);

            //Pajak
            tvpajak = itemView.findViewById(R.id.tv_pajak);

            //Laba Rugi Bersih
            tvLabaRugi = itemView.findViewById(R.id.tv_total_labarugi);

            

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mBulan.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private String convertHarga(String nominal) {
        String txdebet = nominal.replaceAll(",", "");
        if (txdebet.contains(",")) {
            txdebet = txdebet.replaceAll(",", "");
        }
        return txdebet;
    }
}