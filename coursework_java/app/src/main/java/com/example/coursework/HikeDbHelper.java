package com.example.coursework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class HikeDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mhike.db";
    private static final int DB_VERSION = 3; // tăng version vì đổi schema

    // ----- Hikes -----
    public static final String TABLE_HIKES = "hikes";

    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_LOCATION = "location";
    public static final String COL_DATE = "date";
    public static final String COL_DIFFICULTY = "difficulty";
    public static final String COL_DISTANCE = "distance_km";
    public static final String COL_DURATION = "duration_h";
    public static final String COL_ELEVATION = "elevation_m";
    public static final String COL_PARKING = "parking"; // 0/1
    public static final String COL_GROUP_SIZE = "group_size";
    public static final String COL_TERRAIN = "terrain";
    public static final String COL_DESC = "description";

    // ----- Observations -----
    public static final String TABLE_OBS = "observations";
    public static final String COL_OBS_ID = "_id";
    public static final String COL_OBS_HIKE_ID = "hike_id";
    public static final String COL_OBS_TITLE = "title";
    public static final String COL_OBS_TIME = "time";
    public static final String COL_OBS_COMMENT = "comment";

    public HikeDbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    // Bật foreign key
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_HIKES =
                "CREATE TABLE " + TABLE_HIKES + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME + " TEXT NOT NULL, " +
                        COL_LOCATION + " TEXT NOT NULL, " +
                        COL_DATE + " TEXT NOT NULL, " +
                        COL_DIFFICULTY + " TEXT NOT NULL, " +
                        COL_DISTANCE + " REAL NOT NULL, " +
                        COL_DURATION + " REAL NOT NULL, " +
                        COL_ELEVATION + " INTEGER NOT NULL, " +
                        COL_PARKING + " INTEGER NOT NULL, " +
                        COL_GROUP_SIZE + " INTEGER NOT NULL, " +
                        COL_TERRAIN + " TEXT, " +
                        COL_DESC + " TEXT" +
                        ");";

        String SQL_CREATE_OBS =
                "CREATE TABLE " + TABLE_OBS + " (" +
                        COL_OBS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_OBS_HIKE_ID + " INTEGER NOT NULL, " +
                        COL_OBS_TITLE + " TEXT NOT NULL, " +
                        COL_OBS_TIME + " TEXT NOT NULL, " +
                        COL_OBS_COMMENT + " TEXT, " +
                        "FOREIGN KEY(" + COL_OBS_HIKE_ID + ") REFERENCES " +
                        TABLE_HIKES + "(" + COL_ID + ") ON DELETE CASCADE" +
                        ");";

        db.execSQL(SQL_CREATE_HIKES);
        db.execSQL(SQL_CREATE_OBS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // đơn giản cho coursework: drop tất cả rồi tạo lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }

    // ===== Hike CRUD =====

    public long insertHike(Hike hike) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, hike.getName());
        cv.put(COL_LOCATION, hike.getLocation());
        cv.put(COL_DATE, hike.getDate());
        cv.put(COL_DIFFICULTY, hike.getDifficulty());
        cv.put(COL_DISTANCE, hike.getDistanceKm());
        cv.put(COL_DURATION, hike.getDurationHours());
        cv.put(COL_ELEVATION, hike.getElevationM());
        cv.put(COL_PARKING, hike.hasParking() ? 1 : 0);
        cv.put(COL_GROUP_SIZE, hike.getGroupSize());
        cv.put(COL_TERRAIN, hike.getTerrain());
        cv.put(COL_DESC, hike.getDescription());
        long id = db.insert(TABLE_HIKES, null, cv);
        db.close();
        return id;
    }

    public int updateHike(Hike hike) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, hike.getName());
        cv.put(COL_LOCATION, hike.getLocation());
        cv.put(COL_DATE, hike.getDate());
        cv.put(COL_DIFFICULTY, hike.getDifficulty());
        cv.put(COL_DISTANCE, hike.getDistanceKm());
        cv.put(COL_DURATION, hike.getDurationHours());
        cv.put(COL_ELEVATION, hike.getElevationM());
        cv.put(COL_PARKING, hike.hasParking() ? 1 : 0);
        cv.put(COL_GROUP_SIZE, hike.getGroupSize());
        cv.put(COL_TERRAIN, hike.getTerrain());
        cv.put(COL_DESC, hike.getDescription());
        int rows = db.update(TABLE_HIKES, cv, COL_ID + "=?",
                new String[]{String.valueOf(hike.getId())});
        db.close();
        return rows;
    }

    public int deleteHike(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_HIKES, COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public void deleteAllHikes() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_HIKES, null, null);
        db.close();
    }

    public List<Hike> getAllHikes() {
        List<Hike> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_HIKES, null,
                null, null, null, null,
                COL_DATE + " ASC");

        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow(COL_ID));
            String name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
            String location = c.getString(c.getColumnIndexOrThrow(COL_LOCATION));
            String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
            String difficulty = c.getString(c.getColumnIndexOrThrow(COL_DIFFICULTY));
            double distance = c.getDouble(c.getColumnIndexOrThrow(COL_DISTANCE));
            double duration = c.getDouble(c.getColumnIndexOrThrow(COL_DURATION));
            int elevation = c.getInt(c.getColumnIndexOrThrow(COL_ELEVATION));
            boolean parking = c.getInt(c.getColumnIndexOrThrow(COL_PARKING)) == 1;
            int groupSize = c.getInt(c.getColumnIndexOrThrow(COL_GROUP_SIZE));
            String terrain = c.getString(c.getColumnIndexOrThrow(COL_TERRAIN));
            String desc = c.getString(c.getColumnIndexOrThrow(COL_DESC));

            result.add(new Hike(id, name, location, date, difficulty,
                    distance, duration, elevation, parking,
                    groupSize, terrain, desc));
        }
        c.close();
        db.close();
        return result;
    }

    public Hike getHike(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_HIKES, null,
                COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        Hike h = null;
        if (c.moveToFirst()) {
            String name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
            String location = c.getString(c.getColumnIndexOrThrow(COL_LOCATION));
            String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
            String difficulty = c.getString(c.getColumnIndexOrThrow(COL_DIFFICULTY));
            double distance = c.getDouble(c.getColumnIndexOrThrow(COL_DISTANCE));
            double duration = c.getDouble(c.getColumnIndexOrThrow(COL_DURATION));
            int elevation = c.getInt(c.getColumnIndexOrThrow(COL_ELEVATION));
            boolean parking = c.getInt(c.getColumnIndexOrThrow(COL_PARKING)) == 1;
            int groupSize = c.getInt(c.getColumnIndexOrThrow(COL_GROUP_SIZE));
            String terrain = c.getString(c.getColumnIndexOrThrow(COL_TERRAIN));
            String desc = c.getString(c.getColumnIndexOrThrow(COL_DESC));

            h = new Hike(id, name, location, date, difficulty,
                    distance, duration, elevation, parking,
                    groupSize, terrain, desc);
        }
        c.close();
        db.close();
        return h;
    }

    // ===== Observation CRUD =====

    public long insertObservation(Observation obs) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_OBS_HIKE_ID, obs.getHikeId());
        cv.put(COL_OBS_TITLE, obs.getTitle());
        cv.put(COL_OBS_TIME, obs.getTime());
        cv.put(COL_OBS_COMMENT, obs.getComment());
        long id = db.insert(TABLE_OBS, null, cv);
        db.close();
        return id;
    }

    public int updateObservation(Observation obs) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_OBS_TITLE, obs.getTitle());
        cv.put(COL_OBS_TIME, obs.getTime());
        cv.put(COL_OBS_COMMENT, obs.getComment());
        int rows = db.update(TABLE_OBS, cv,
                COL_OBS_ID + "=?",
                new String[]{String.valueOf(obs.getId())});
        db.close();
        return rows;
    }

    public int deleteObservation(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_OBS, COL_OBS_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public List<Observation> getObservationsForHike(long hikeId) {
        List<Observation> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_OBS, null,
                COL_OBS_HIKE_ID + "=?",
                new String[]{String.valueOf(hikeId)},
                null, null, COL_OBS_TIME + " ASC");

        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow(COL_OBS_ID));
            String title = c.getString(c.getColumnIndexOrThrow(COL_OBS_TITLE));
            String time = c.getString(c.getColumnIndexOrThrow(COL_OBS_TIME));
            String comment = c.getString(c.getColumnIndexOrThrow(COL_OBS_COMMENT));
            result.add(new Observation(id, hikeId, title, time, comment));
        }
        c.close();
        db.close();
        return result;
    }

    public Observation getObservation(long obsId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_OBS, null,
                COL_OBS_ID + "=?",
                new String[]{String.valueOf(obsId)},
                null, null, null);
        Observation o = null;
        if (c.moveToFirst()) {
            long hikeId = c.getLong(c.getColumnIndexOrThrow(COL_OBS_HIKE_ID));
            String title = c.getString(c.getColumnIndexOrThrow(COL_OBS_TITLE));
            String time = c.getString(c.getColumnIndexOrThrow(COL_OBS_TIME));
            String comment = c.getString(c.getColumnIndexOrThrow(COL_OBS_COMMENT));
            o = new Observation(obsId, hikeId, title, time, comment);
        }
        c.close();
        db.close();
        return o;
    }
}
