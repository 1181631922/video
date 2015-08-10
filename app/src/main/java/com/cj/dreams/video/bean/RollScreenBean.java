package com.cj.dreams.video.bean;

/**
 * Created by fanyafeng on 2015/7/17/0017.
 */
public class RollScreenBean extends BaseBean{
    private String RollScreenTitle;
    private String Title;
    private String Index;
    private String RollScreenImage;
    private String Image;
    private String VideoUrl;
    private String VideoId;
    private String PlayNumber;
    private String GoodNumber;
    private String CollectNumber;

    public RollScreenBean(String rollScreenTitle, String title, String index, String rollScreenImage, String image, String videoUrl, String videoId, String playNumber, String goodNumber, String collectNumber) {
        RollScreenTitle = rollScreenTitle;
        Title = title;
        Index = index;
        RollScreenImage = rollScreenImage;
        Image = image;
        VideoUrl = videoUrl;
        VideoId = videoId;
        PlayNumber = playNumber;
        GoodNumber = goodNumber;
        CollectNumber = collectNumber;
    }

    public String getRollScreenTitle() {
        return RollScreenTitle;
    }

    public void setRollScreenTitle(String rollScreenTitle) {
        RollScreenTitle = rollScreenTitle;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getIndex() {
        return Index;
    }

    public void setIndex(String index) {
        Index = index;
    }

    public String getRollScreenImage() {
        return RollScreenImage;
    }

    public void setRollScreenImage(String rollScreenImage) {
        RollScreenImage = rollScreenImage;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getPlayNumber() {
        return PlayNumber;
    }

    public void setPlayNumber(String playNumber) {
        PlayNumber = playNumber;
    }

    public String getGoodNumber() {
        return GoodNumber;
    }

    public void setGoodNumber(String goodNumber) {
        GoodNumber = goodNumber;
    }

    public String getCollectNumber() {
        return CollectNumber;
    }

    public void setCollectNumber(String collectNumber) {
        CollectNumber = collectNumber;
    }

    @Override
    public String toString() {
        return "RollScreenBean{" +
                "RollScreenTitle='" + RollScreenTitle + '\'' +
                ", Title='" + Title + '\'' +
                ", Index='" + Index + '\'' +
                ", RollScreenImage='" + RollScreenImage + '\'' +
                ", Image='" + Image + '\'' +
                ", VideoUrl='" + VideoUrl + '\'' +
                ", VideoId='" + VideoId + '\'' +
                ", PlayNumber='" + PlayNumber + '\'' +
                ", GoodNumber='" + GoodNumber + '\'' +
                ", CollectNumber='" + CollectNumber + '\'' +
                '}';
    }
}
