package com.example.coursework;

public class Observation {
    private long id;
    private long hikeId;
    private String title;
    private String time;     // dạng "2025-11-25 14:20"
    private String comment;  // optional

    public Observation(long id, long hikeId, String title, String time, String comment) {
        this.id = id;
        this.hikeId = hikeId;
        this.title = title;
        this.time = time;
        this.comment = comment;
    }

    public Observation(long hikeId, String title, String time, String comment) {
        this(-1, hikeId, title, time, comment);
    }

    public long getId() { return id; }
    public long getHikeId() { return hikeId; }
    public String getTitle() { return title; }
    public String getTime() { return time; }
    public String getComment() { return comment; }

    public void setId(long id) { this.id = id; }
}
