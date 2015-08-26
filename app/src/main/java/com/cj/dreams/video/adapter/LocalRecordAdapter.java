package com.cj.dreams.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.bean.LocalRecordBean;
import com.cj.dreams.video.util.ListViewImageTaskUtil;

import java.util.List;

/**
 * Created by fanyafeng on 2015/7/27/0027.
 */
public class LocalRecordAdapter extends ABaseAdapter{
    private Context context;
    private List<LocalRecordBean> localRecordBeanList;
    private ViewHolder holder;

    // 获取当前应用程序所分配的最大内存
    private final int maxMemory = (int) Runtime.getRuntime().maxMemory();
    // 只用五分之一用来做图片缓存
    private final int cacheSize = maxMemory / 5;

    private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(
            cacheSize) {

        // 重写sizeof（）方法
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // TODO Auto-generated method stub
            // 这里用多少kb来计算
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        }

    };


    public LocalRecordAdapter(Context context, List<LocalRecordBean> localRecordBeanList) {
        this.context = context;
        this.localRecordBeanList = localRecordBeanList;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public int getCount() {
        return localRecordBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return localRecordBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_activity_local_record, null);
            holder = new ViewHolder();
            view.setTag(holder);
            holder.localRecordBeanListIcon = (ImageView) view.findViewById(R.id.local_record_icon);
            holder.localRecordBeanListTitle = (TextView) view.findViewById(R.id.local_record_title);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        loadBitmap(localRecordBeanList.get(position).getVideoImageUrl(), holder.localRecordBeanListIcon);
        holder.localRecordBeanListTitle.setText(localRecordBeanList.get(position).getVideoTitle());
        return view;
    }

    private void loadBitmap(String urlStr, ImageView image) {

        ListViewImageTaskUtil asyncLoader = new ListViewImageTaskUtil(image, mLruCache);// 一个异步图片加载对象
        Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);// 首先从内存缓存中获取图片
        if (bitmap != null) {
            image.setImageBitmap(bitmap);// 如果缓存中存在这张图片则直接设置给ImageView
        } else {
            image.setImageResource(R.drawable.wait);// 否则先设置成默认的图片
            asyncLoader.execute(urlStr);// 然后执行异步任务AsycnTask 去网上加载图片
        }
    }

    class ViewHolder {
        ImageView localRecordBeanListIcon;
        TextView localRecordBeanListTitle;
    }
}
