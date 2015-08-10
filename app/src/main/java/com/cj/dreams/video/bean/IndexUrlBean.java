package com.cj.dreams.video.bean;

/**
 * Created by fanyafeng on 2015/7/2.
 */
public class IndexUrlBean extends BaseBean{
    private int Position;
    private String LeftTop;
    private String RightTop;
    private String LeftBottom;
    private String RightBottom;

    public IndexUrlBean(int position, String leftTop, String rightTop, String leftBottom, String rightBottom) {
        Position = position;
        LeftTop = leftTop;
        RightTop = rightTop;
        LeftBottom = leftBottom;
        RightBottom = rightBottom;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public String getLeftTop() {
        return LeftTop;
    }

    public void setLeftTop(String leftTop) {
        LeftTop = leftTop;
    }

    public String getRightTop() {
        return RightTop;
    }

    public void setRightTop(String rightTop) {
        RightTop = rightTop;
    }

    public String getLeftBottom() {
        return LeftBottom;
    }

    public void setLeftBottom(String leftBottom) {
        LeftBottom = leftBottom;
    }

    public String getRightBottom() {
        return RightBottom;
    }

    public void setRightBottom(String rightBottom) {
        RightBottom = rightBottom;
    }

    @Override
    public String toString() {
        return "IndexUrlBean{" +
                "RightBottom='" + RightBottom + '\'' +
                ", LeftBottom='" + LeftBottom + '\'' +
                ", RightTop='" + RightTop + '\'' +
                ", LeftTop='" + LeftTop + '\'' +
                ", Position=" + Position +
                '}';
    }
}
