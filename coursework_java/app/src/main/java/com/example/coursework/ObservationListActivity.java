package com.example.coursework;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ObservationListActivity extends AppCompatActivity implements ObservationAdapter.Listener {

    private static final int REQ_ADD_OBS = 300;
    private static final int REQ_EDIT_OBS = 301;

    private long hikeId;
    private String hikeName;

    private HikeDbHelper dbHelper;
    private ObservationAdapter adapter;

    private TextView tvTitle;
    private RecyclerView rvObs;
    private Button btnAddObs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_list);

        hikeId = getIntent().getLongExtra("hikeId", -1);
        hikeName = getIntent().getStringExtra("hikeName");

        dbHelper = new HikeDbHelper(this);

        tvTitle = findViewById(R.id.tvObsForHike);
        rvObs = findViewById(R.id.rvObservations);
        btnAddObs = findViewById(R.id.btnAddObservation);

        tvTitle.setText("Observations for: " + hikeName);

        rvObs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ObservationAdapter(new ArrayList<>(), this);
        rvObs.setAdapter(adapter);

        btnAddObs.setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditObservationActivity.class);
            i.putExtra("mode", "add");
            i.putExtra("hikeId", hikeId);
            startActivityForResult(i, REQ_ADD_OBS);
        });

        loadObservations();
    }

    private void loadObservations() {
        List<Observation> list = dbHelper.getObservationsForHike(hikeId);
        adapter.setObservations(list);
    }

    @Override
    public void onEdit(Observation obs) {
        Intent i = new Intent(this, AddEditObservationActivity.class);
        i.putExtra("mode", "edit");
        i.putExtra("obsId", obs.getId());
        startActivityForResult(i, REQ_EDIT_OBS);
    }

    @Override
    public void onDelete(Observation obs) {
        new AlertDialog.Builder(this)
                .setTitle("Delete observation")
                .setMessage("Are you sure you want to delete this observation?")
                .setPositiveButton("Delete", (d, w) -> {
                    dbHelper.deleteObservation(obs.getId());
                    loadObservations();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadObservations();
            setResult(RESULT_OK); // để MainActivity reload nếu cần
        }
    }
}
