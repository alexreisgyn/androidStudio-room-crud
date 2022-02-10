package com.example.roomdb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomdb.models.Loli;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

public class LoliAdapter extends RecyclerView.Adapter<LoliAdapter.LoliViewHolder> {

    private List<Loli> nListLoli;
    private ICLickItemLoli icLickItemLoli;

    public LoliAdapter(ICLickItemLoli icLickItemLoli) {
        this.icLickItemLoli = icLickItemLoli;
    }

    public void setData(List<Loli> list) {
        this.nListLoli = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LoliViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_loli, parent, false);

        return new LoliViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoliViewHolder holder, int position) {
        Loli loli = nListLoli.get(position);

        if (loli == null) {
            return;
        }

        // Use Picasso to load avatar from the Internet
        ImageView imgView = holder.ivAvatar;
        Picasso.get().load(loli.getImgUrl()).into(imgView);

        // Name & Age
        holder.tvLoliName.setText(loli.getLoliName());
        holder.tvLoliAge.setText(loli.getLoliAge());

        // Update
        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icLickItemLoli.updateLoli(loli);
            }
        });

        // Delete
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icLickItemLoli.deleteLoli(loli);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (nListLoli != null) {
            return nListLoli.size();
        }

        return 0;
    }

    public class LoliViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivAvatar;
        private TextView tvLoliName;
        private TextView tvLoliAge;
        private Button btnUpdate;
        private Button btnDelete;

        public LoliViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.img_avatar);
            tvLoliName = itemView.findViewById(R.id.tv_loliName);
            tvLoliAge = itemView.findViewById(R.id.tv_loliAge);
            btnUpdate = itemView.findViewById(R.id.btn_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface ICLickItemLoli {
        void updateLoli(Loli loli);
        void deleteLoli(Loli loli);
    }
}
