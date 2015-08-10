package com.cj.dreams.video.bean;

/**
 * Created by fanyafeng on 2015/7/27/0027.
 */
public class LocalRecordBean extends BaseBean {
    private String VideoId;
    private String VideoImageUrl;
    private String VideoTitle;
    private String VideoUrl;

    public LocalRecordBean(String videoId, String videoImageUrl, String videoTitle, String videoUrl) {
        VideoId = videoId;
        VideoImageUrl = videoImageUrl;
        VideoTitle = videoTitle;
        VideoUrl = videoUrl;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getVideoImageUrl() {
        return VideoImageUrl;
    }

    public void setVideoImageUrl(String videoImageUrl) {
        VideoImageUrl = videoImageUrl;
    }

    public String getVideoTitle() {
        return VideoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        VideoTitle = videoTitle;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    @Override
    public String toString() {
        return "LocalRecordBean{" +
                "VideoId='" + VideoId + '\'' +
                ", VideoImageUrl='" + VideoImageUrl + '\'' +
                ", VideoTitle='" + VideoTitle + '\'' +
                ", VideoUrl='" + VideoUrl + '\'' +
                '}';
    }
}
