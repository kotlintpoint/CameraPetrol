package com.example.admin.myapplication.model;

import java.sql.Time;

public class TimeLine {
    private String image;
    private String video;
    private String description;
    public TimeLine(){

    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
