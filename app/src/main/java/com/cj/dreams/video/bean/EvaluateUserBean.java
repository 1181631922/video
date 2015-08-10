package com.cj.dreams.video.bean;

/**
 * Created by fanyafeng on 2015/7/15/0015.
 */
public class EvaluateUserBean extends BaseBean {
    private String EvaluateUser;
    private String EvaluateTime;
    private String EvaluateDetail;
    private String EvaluateGoodTimes;

    public EvaluateUserBean(String evaluateUser, String evaluateTime, String evaluateDetail, String evaluateGoodTimes) {
        EvaluateUser = evaluateUser;
        EvaluateTime = evaluateTime;
        EvaluateDetail = evaluateDetail;
        EvaluateGoodTimes = evaluateGoodTimes;
    }

    public String getEvaluateUser() {
        return EvaluateUser;
    }

    public void setEvaluateUser(String evaluateUser) {
        EvaluateUser = evaluateUser;
    }

    public String getEvaluateTime() {
        return EvaluateTime;
    }

    public void setEvaluateTime(String evaluateTime) {
        EvaluateTime = evaluateTime;
    }

    public String getEvaluateDetail() {
        return EvaluateDetail;
    }

    public void setEvaluateDetail(String evaluateDetail) {
        EvaluateDetail = evaluateDetail;
    }

    public String getEvaluateGoodTimes() {
        return EvaluateGoodTimes;
    }

    public void setEvaluateGoodTimes(String evaluateGoodTimes) {
        EvaluateGoodTimes = evaluateGoodTimes;
    }

    @Override
    public String toString() {
        return "EvaluateUserBean{" +
                "EvaluateUser='" + EvaluateUser + '\'' +
                ", EvaluateTime='" + EvaluateTime + '\'' +
                ", EvaluateDetail='" + EvaluateDetail + '\'' +
                ", EvaluateGoodTimes='" + EvaluateGoodTimes + '\'' +
                '}';
    }
}
