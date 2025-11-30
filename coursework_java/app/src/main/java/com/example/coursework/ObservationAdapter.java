package com.example.coursework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObsViewHolder> {

    public interface Listener {
        void onEdit(Observation obs);
        void onDelete(Observation obs);
    }

    private List<Observation> observations;
    private Listener listener;

    public ObservationAdapter(List<Observation> observations, Listener listener) {
        this.observations = observations;
        this.listener = listener;
    }

    public void setObservations(List<Observation> list) {
        this.observations = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ObsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_observation, parent, false);
        return new ObsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ObsViewHolder holder, int position) {
        Observation o = observations.get(position);
        holder.tvTitle.setText(o.getTitle());
        holder.tvTime.setText(o.getTime());
        holder.tvComment.setText(o.getComment());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(o);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(o);
        });
    }

    @Override
    public int getItemCount() {
        return observations == null ? 0 : observations.size();
    }

    static class ObsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvComment;
        Button btnEdit, btnDelete;

        ObsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvObsTitle);
            tvTime = itemView.findViewById(R.id.tvObsTime);
            tvComment = itemView.findViewById(R.id.tvObsComment);
            btnEdit = itemView.findViewById(R.id.btnObsEdit);
            btnDelete = itemView.findViewById(R.id.btnObsDelete);
        }
    }
}
