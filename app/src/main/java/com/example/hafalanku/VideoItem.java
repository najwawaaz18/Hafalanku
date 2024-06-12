package com.example.hafalanku;

public class VideoItem {
    private String videoUrl;
    private String videoTitle;
    private String uploadTime;
    private String videoId;
    private String videoUserName;

    public VideoItem(String videoUrl, String videoTitle) {
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
    }

    public VideoItem(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getVideoUserName() {
        return videoUserName;
    }

    public void setVideoUserName(String videoUserName) {
        this.videoUserName = videoUserName;
    }
}
