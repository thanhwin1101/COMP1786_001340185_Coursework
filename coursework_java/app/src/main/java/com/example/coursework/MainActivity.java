package com.example.coursework;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements HikeAdapter.Listener {

    private static final int REQ_ADD = 100;
    private static final int REQ_EDIT = 101;
    private static final int REQ_OBS = 200;

    private HikeDbHelper dbHelper;
    private HikeAdapter adapter;
    private RecyclerView rvHikes;

    private Button btnAdd, btnReset, btnAdvanced;
    private EditText etSearchName;

    private List<Hike> allHikes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new HikeDbHelper(this);

        rvHikes = findViewById(R.id.rvHikes);
        btnAdd = findViewById(R.id.btnAdd);
        btnReset = findViewById(R.id.btnReset);
        btnAdvanced = findViewById(R.id.btnAdvanced);
        etSearchName = findViewById(R.id.etSearchName);

        rvHikes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HikeAdapter(new ArrayList<>(), this);
        rvHikes.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddEditHikeActivity.class);
            i.putExtra("mode", "add");
            startActivityForResult(i, REQ_ADD);
        });

        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Reset database")
                    .setMessage("This will delete ALL hikes and their observations. Continue?")
                    .setPositiveButton("Reset", (d, w) -> {
                        dbHelper.deleteAllHikes();
                        loadHikes();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnAdvanced.setOnClickListener(v -> showAdvancedSearchDialog());

        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyNameFilter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadHikes();
    }

    private void loadHikes() {
        allHikes = dbHelper.getAllHikes();
        adapter.setHikes(allHikes);
    }

    // Filter đơn giản theo name (contains, case-insensitive)
    private void applyNameFilter(String query) {
        if (query == null) query = "";
        String q = query.toLowerCase().trim();
        if (q.isEmpty()) {
            adapter.setHikes(allHikes);
            return;
        }
        List<Hike> filtered = new ArrayList<>();
        for (Hike h : allHikes) {
            if (h.getName().toLowerCase().contains(q)) {
                filtered.add(h);
            }
        }
        adapter.setHikes(filtered);
    }

    private void showAdvancedSearchDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_advanced_search, null);
        EditText etLoc = dialogView.findViewById(R.id.etFilterLocation);
        EditText etMaxDist = dialogView.findViewById(R.id.etFilterMaxDistance);
        EditText etDate = dialogView.findViewById(R.id.etFilterDate);

        // Không cho gõ trực tiếp, bấm để chọn ngày
        etDate.setInputType(InputType.TYPE_NULL);
        etDate.setOnClickListener(v -> showDatePickerFor(etDate));

        new AlertDialog.Builder(this)
                .setTitle("Advanced search")
                .setView(dialogView)
                .setPositiveButton("Apply", (d, w) -> {
                    String loc = etLoc.getText().toString().trim();
                    String maxDistStr = etMaxDist.getText().toString().trim();
                    String date = etDate.getText().toString().trim();
                    applyAdvancedFilter(loc, maxDistStr, date);
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Clear", (d, w) -> {
                    adapter.setHikes(allHikes);
                    etSearchName.setText("");
                })
                .show();
    }

    // Mở DatePicker và set text theo định dạng MM/dd/yyyy
    private void showDatePickerFor(EditText target) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String text = String.format(Locale.US, "%02d/%02d/%04d",
                            month + 1, dayOfMonth, year);
                    target.setText(text);
                },
                y, m, d
        );
        dp.show();
    }

    // Filter nâng cao theo location (contains), max distance, date (exact match)
    private void applyAdvancedFilter(String loc, String maxDistStr, String date) {
        boolean useLoc = !loc.isEmpty();
        boolean useDist = !maxDistStr.isEmpty();
        boolean useDate = !date.isEmpty();
        double maxDist = 0;
        if (useDist) {
            try {
                maxDist = Double.parseDouble(maxDistStr);
            } catch (NumberFormatException e) {
                maxDist = 0;
                useDist = false;
            }
        }

        List<Hike> filtered = new ArrayList<>();
        for (Hike h : allHikes) {
            boolean ok = true;
            if (useLoc && !h.getLocation().toLowerCase().contains(loc.toLowerCase())) {
                ok = false;
            }
            if (ok && useDist && h.getDistanceKm() > maxDist) {
                ok = false;
            }
            if (ok && useDate && !h.getDate().equals(date)) {
                ok = false;
            }
            if (ok) filtered.add(h);
        }
        adapter.setHikes(filtered);
    }

    // ===== HikeAdapter.Listener =====

    @Override
    public void onEdit(Hike hike) {
        Intent i = new Intent(this, AddEditHikeActivity.class);
        i.putExtra("mode", "edit");
        i.putExtra("id", hike.getId());
        startActivityForResult(i, REQ_EDIT);
    }

    @Override
    public void onDelete(Hike hike) {
        new AlertDialog.Builder(this)
                .setTitle("Delete hike")
                .setMessage("Are you sure you want to delete this hike?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteHike(hike.getId());
                    loadHikes();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onObservations(Hike hike) {
        Intent i = new Intent(this, ObservationListActivity.class);
        i.putExtra("hikeId", hike.getId());
        i.putExtra("hikeName", hike.getName());
        startActivityForResult(i, REQ_OBS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadHikes();
        }
    }
}
