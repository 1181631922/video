package com.cj.dreams.video.bean;

/**
 * Created by fanyafeng on 2015/7/7/0007.
 */
public class VideoListBean extends BaseBean {
    private String VideoId;
    private String VideoUrl;
    private String VideoImage;
    private String VideoTitle;
    private String VideoPlayTimes;
    private String VideoCollectTimes;
    private String VideoGoodTimes;

    public VideoListBean(String videoId, String videoUrl, String videoImage, String videoTitle, String videoPlayTimes, String videoCollectTimes, String videoGoodTimes) {
        VideoId = videoId;
        VideoUrl = videoUrl;
        VideoImage = videoImage;
        VideoTitle = videoTitle;
        VideoPlayTimes = videoPlayTimes;
        VideoCollectTimes = videoCollectTimes;
        VideoGoodTimes = videoGoodTimes;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public String getVideoImage() {
        return VideoImage;
    }

    public void setVideoImage(String videoImage) {
        VideoImage = videoImage;
    }

    public String getVideoTitle() {
        return VideoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        VideoTitle = videoTitle;
    }

    public String getVideoPlayTimes() {
        return VideoPlayTimes;
    }

    public void setVideoPlayTimes(String videoPlayTimes) {
        VideoPlayTimes = videoPlayTimes;
    }

    public String getVideoCollectTimes() {
        return VideoCollectTimes;
    }

    public void setVideoCollectTimes(String videoCollectTimes) {
        VideoCollectTimes = videoCollectTimes;
    }

    public String getVideoGoodTimes() {
        return VideoGoodTimes;
    }

    public void setVideoGoodTimes(String videoGoodTimes) {
        VideoGoodTimes = videoGoodTimes;
    }

    @Override
    public String toString() {
        return "VideoListBean{" +
                "VideoId='" + VideoId + '\'' +
                ", VideoUrl='" + VideoUrl + '\'' +
                ", VideoImage='" + VideoImage + '\'' +
                ", VideoTitle='" + VideoTitle + '\'' +
                ", VideoPlayTimes='" + VideoPlayTimes + '\'' +
                ", VideoCollectTimes='" + VideoCollectTimes + '\'' +
                ", VideoGoodTimes='" + VideoGoodTimes + '\'' +
                '}';
    }
}
