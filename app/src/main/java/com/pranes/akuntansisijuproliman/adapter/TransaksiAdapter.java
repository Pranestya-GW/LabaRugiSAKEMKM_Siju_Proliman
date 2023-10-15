package com.pranes.akuntansisijuproliman.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.pranes.akuntansisijuproliman.R;
import com.pranes.akuntansisijuproliman.model.Transaksi;
import com.pranes.akuntansisijuproliman.model.Waktu;
import com.pranes.akuntansisijuproliman.ui.transaksi.PemasukanActivity;
import com.pranes.akuntansisijuproliman.ui.transaksi.PengeluaranActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class TransaksiAdapter extends FirestoreRecyclerAdapter<Transaksi, TransaksiAdapter.ViewHolder> {
    private OnItemClickListener listener;
    Waktu waktu;
    public TransaksiAdapter(@NonNull FirestoreRecyclerOptions<Transaksi> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Transaksi transaksi) {
        waktu = new Waktu();
        String tgl= transaksi.getTanggal();

        String nominal =  transaksi.getNominal().toString().replaceAll(",", "");
        Long longval;
        if (nominal.contains(",")) {
            nominal = nominal.replaceAll(",", "");
        }
        longval = Long.parseLong(nominal);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        String newPrice = formatter.format(longval);

        holder.tvKeterangan.setText(transaksi.getKeterangan());
        holder.tvtanggal.setText(tgl);
        holder.tvNominal.setText(newPrice);
        holder.tvTransaksi.setText(transaksi.getJenis_transaksi());
        if (transaksi.getTransaksi().equalsIgnoreCase("Pengeluaran")){
            holder.tvNominal.setTextColor(Color.parseColor("#ff1a02"));
        }else{
            holder.tvNominal.setTextColor(Color.parseColor("#00B109"));
        }

        holder.btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
//                Toast.makeText(view.getContext(), "EDIT CLICKED"+transaksi.getId()+" "+transaksi.getKeterangan(), Toast.LENGTH_SHORT).show();
                switch (transaksi.getTransaksi()){
                    case "Pemasukan":
                        intent = new Intent(view.getContext(), PemasukanActivity.class);
                        intent.putExtra(PemasukanActivity.TRANSAKSI, transaksi);
                        view.getContext().startActivity(intent);
                        break;
                    case "Pengeluaran":
                        intent = new Intent(view.getContext(), PengeluaranActivity.class);
                        intent.putExtra(PengeluaranActivity.TRANSAKSI, transaksi);
                        view.getContext().startActivity(intent);
                        break;
                    default:
                        Toast.makeText(view.getContext(), "Ini Bukan Pemasukan", Toast.LENGTH_SHORT).show();
                }
//                Intent intent = new Intent(view.getContext(), BayarUtangActivity.class);
//                intent.putExtra(BayarUtangActivity.TRANSAKSI, transaksi);
//                view.getContext().startActivity(intent);
            }
        });
        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            String txttransaksi;
            @Override
            public void onClick(View view) {

                getSnapshots().getSnapshot(position).getReference().get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        assert documentSnapshot != null;

                        txttransaksi = documentSnapshot.getString("nominal");
                        Log.d("query", txttransaksi );
                    }else{
                        Log.d("query", "Query gagal " );
                    }
                    getSnapshots().getSnapshot(position).getReference().delete();
                });
                notifyDataSetChanged();

            }
        });
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvNama, tvTransaksi, tvNominal, tvKeterangan, tvtanggal;
        private ImageButton btnedit, btndelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tvTransaksi = itemView.findViewById(R.id.tvTransaksi);
            tvNominal = itemView.findViewById(R.id.tv_nominal);
            tvKeterangan = itemView.findViewById(R.id.tvKeterangan);
            tvtanggal = itemView.findViewById(R.id.tv_tanggal);
            btndelete = itemView.findViewById(R.id.delete_button);
            btnedit = itemView.findViewById(R.id.edit_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(position), position);
            }
        }
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        void handleItemDelete(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClick(OnItemClickListener listener) {
        this.listener = listener;
    }
}
