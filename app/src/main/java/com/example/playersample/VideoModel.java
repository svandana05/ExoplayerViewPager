package com.example.playersample;

public class VideoModel {
    private String videoUrl;
    private long seekValue;

    public VideoModel(String videoUrl, long seekValue) {
        this.videoUrl = videoUrl;
        this.seekValue = seekValue;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public long getSeekValue() {
        return seekValue;
    }

    public void setSeekValue(long seekValue) {
        this.seekValue = seekValue;
    }
}
