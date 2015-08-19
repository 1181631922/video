package com.cj.dreams.video.bean;

/**
 * Created by fanyafeng on 2015/08/19/0019.
 */
public class AppGradeBean extends BaseBean {
    private String appStoreName;
    private String appStroeUrl;

    public AppGradeBean(String appStoreName, String appStroeUrl) {
        this.appStoreName = appStoreName;
        this.appStroeUrl = appStroeUrl;
    }

    public String getAppStoreName() {
        return appStoreName;
    }

    public void setAppStoreName(String appStoreName) {
        this.appStoreName = appStoreName;
    }

    public String getAppStroeUrl() {
        return appStroeUrl;
    }

    public void setAppStroeUrl(String appStroeUrl) {
        this.appStroeUrl = appStroeUrl;
    }

    @Override
    public String toString() {
        return "AppGradeBean{" +
                "appStoreName='" + appStoreName + '\'' +
                ", appStroeUrl='" + appStroeUrl + '\'' +
                '}';
    }
}
