package com.cj.dreams.video.bean;

/**
 * Created by fanyafeng on 2015/08/04/0004.
 */
public class UserInfoBean {
    private String UserId;
    private String UserName;
    private String UserImage;

    public UserInfoBean(String userId, String userName, String userImage) {
        UserId = userId;
        UserName = userName;
        UserImage = userImage;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "UserId='" + UserId + '\'' +
                ", UserName='" + UserName + '\'' +
                ", UserImage='" + UserImage + '\'' +
                '}';
    }
}
