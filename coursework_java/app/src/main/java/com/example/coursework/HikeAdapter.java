package com.example.coursework;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeViewHolder> {

    public interface Listener {
        void onEdit(Hike hike);
        void onDelete(Hike hike);
        void onObservations(Hike hike);
    }

    private List<Hike> hikes;
    private Listener listener;

    public HikeAdapter(List<Hike> hikes, Listener listener) {
        this.hikes = hikes;
        this.listener = listener;
    }

    public void setHikes(List<Hike> newList) {
        this.hikes = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hike, parent, false);
        return new HikeViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull HikeViewHolder holder, int position) {
        Hike h = hikes.get(position);
        holder.tvName.setText(h.getName());
        holder.tvLocation.setText(h.getLocation());
        holder.tvDate.setText(h.getDate());
        holder.tvDifficulty.setText(h.getDifficulty());
        holder.tvDistance.setText(String.format("%.1f km", h.getDistanceKm()));
        holder.tvDuration.setText(String.format("%.1f h", h.getDurationHours()));
        holder.tvElevation.setText(h.getElevationM() + " m");

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(h);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(h);
        });

        holder.btnObs.setOnClickListener(v -> {
            if (listener != null) listener.onObservations(h);
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HikeDetailActivity.class);
            intent.putExtra("id", h.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hikes == null ? 0 : hikes.size();
    }

    static class HikeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvDate, tvDifficulty,
                tvDistance, tvDuration, tvElevation;
        Button btnEdit, btnDelete, btnObs;

        HikeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvElevation = itemView.findViewById(R.id.tvElevation);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnObs = itemView.findViewById(R.id.btnObs);
        }
    }
}
