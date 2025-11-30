package com.example.coursework;

public class Hike {
    private long id;
    private String name;
    private String location;
    private String date;        // ví dụ "11/22/2025"
    private String difficulty;  // Easy, Moderate, Hard, Expert
    private double distanceKm;
    private double durationHours;
    private int elevationM;
    private boolean parking;
    private int groupSize;      // NEW - số người tham gia
    private String terrain;     // NEW - địa hình
    private String description; // ghi chú

    public Hike(long id,
                String name,
                String location,
                String date,
                String difficulty,
                double distanceKm,
                double durationHours,
                int elevationM,
                boolean parking,
                int groupSize,
                String terrain,
                String description) {

        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.difficulty = difficulty;
        this.distanceKm = distanceKm;
        this.durationHours = durationHours;
        this.elevationM = elevationM;
        this.parking = parking;
        this.groupSize = groupSize;
        this.terrain = terrain;
        this.description = description;
    }

    public Hike(String name,
                String location,
                String date,
                String difficulty,
                double distanceKm,
                double durationHours,
                int elevationM,
                boolean parking,
                int groupSize,
                String terrain,
                String description) {

        this(-1, name, location, date, difficulty,
                distanceKm, durationHours, elevationM,
                parking, groupSize, terrain, description);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getDifficulty() { return difficulty; }
    public double getDistanceKm() { return distanceKm; }
    public double getDurationHours() { return durationHours; }
    public int getElevationM() { return elevationM; }
    public boolean hasParking() { return parking; }
    public int getGroupSize() { return groupSize; }
    public String getTerrain() { return terrain; }
    public String getDescription() { return description; }
}
