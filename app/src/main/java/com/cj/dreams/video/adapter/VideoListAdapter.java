package com.cj.dreams.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.bean.VideoListBean;
import com.cj.dreams.video.dbhelper.CollectTableCourse;
import com.cj.dreams.video.dbhelper.GoodTableCourse;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.dbhelper.RecordTableCourse;
import com.cj.dreams.video.dboperate.CollectOperate;
import com.cj.dreams.video.dboperate.GoodOperate;
import com.cj.dreams.video.dboperate.RecordOperate;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.ListViewImageTaskUtil;
import com.cj.dreams.video.util.PostUtil;
import com.cj.dreams.video.util.T;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanyafeng on 2015/7/7/0007.
 */
public class VideoListAdapter extends ABaseAdapter {
    private Context context;
    private List<VideoListBean> videoListBeanList;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private RecordOperate recordOperate;
    private CollectOperate collectOperate;
    private GoodOperate goodOperate;
    //用来记录播放状态的图标显示，测试
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

    public VideoListAdapter(Context context, List<VideoListBean> videoListBeanList) {
        this.context = context;
        this.videoListBeanList = videoListBeanList;

    }

    public void update() {
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return videoListBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoListBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_activity_video_list, null);
            holder = new ViewHolder();
            view.setTag(holder);
            holder.item_background=(LinearLayout)view.findViewById(R.id.item_background);
            holder.videoListIcon = (ImageView) view.findViewById(R.id.videolist_icon);
            holder.videoListTitle = (TextView) view.findViewById(R.id.videolist_title);
            holder.videoPlayTimes = (TextView) view.findViewById(R.id.videolist_play_times);
            holder.videoCollectTimes = (TextView) view.findViewById(R.id.videolist_collect_times);
            holder.videoGoodTimes = (TextView) view.findViewById(R.id.videolist_good_times);
            holder.videoPlayIcon = (ImageView) view.findViewById(R.id.videolist_play_icon);
            holder.videoCollectIcon = (ImageView) view.findViewById(R.id.videolist_collect_icon);
            holder.videoGoodIcon = (ImageView) view.findViewById(R.id.videolist_good_icon);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(context);
        laughSQLiteOpenHelper.getWritableDatabase();
        if (new RecordTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchRecord(videoListBeanList.get(position).getVideoId())) {
            holder.videoPlayIcon.setImageResource(R.drawable.icon_bof);
        } else {
            holder.videoPlayIcon.setImageResource(R.drawable.icon_bof_d);
        }

        holder.videoCollectIcon.setOnClickListener(new collectClick(position));
        if (new CollectTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchCollect(videoListBeanList.get(position).getVideoId())) {
            holder.videoCollectIcon.setImageResource(R.drawable.icon_shouc);
        } else {
            holder.videoCollectIcon.setImageResource(R.drawable.icon_shouc_d);
        }

        holder.videoGoodIcon.setOnClickListener(new goodClick(position));
        if (new GoodTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchGood(videoListBeanList.get(position).getVideoId())) {
            holder.videoGoodIcon.setImageResource(R.drawable.icon_z);
        } else {
            holder.videoGoodIcon.setImageResource(R.drawable.icon_z_d);
        }
        loadBitmap(videoListBeanList.get(position).getVideoImage(), holder.videoListIcon);
        holder.item_background.getBackground().setAlpha(50);
        holder.videoListTitle.setText(videoListBeanList.get(position).getVideoTitle());
        holder.videoPlayTimes.setText(videoListBeanList.get(position).getVideoPlayTimes());
        holder.videoCollectTimes.setText(videoListBeanList.get(position).getVideoCollectTimes());
        holder.videoGoodTimes.setText(videoListBeanList.get(position).getVideoGoodTimes());

        return view;
    }

    class collectClick implements View.OnClickListener {
        private int position;

        collectClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Thread loadThread = new Thread(new LoadThread(videoListBeanList.get(position).getVideoId(), "collect"));
            loadThread.start();
            int collectId = v.getId();
            if (collectId == holder.videoCollectIcon.getId()) {
                if (new CollectTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchCollect(videoListBeanList.get(position).getVideoId())) {
                    collectOperate = new CollectOperate(laughSQLiteOpenHelper.getReadableDatabase());
                    collectOperate.insert(videoListBeanList.get(position).getVideoId(), videoListBeanList.get(position).getVideoImage(), videoListBeanList.get(position).getVideoTitle(), videoListBeanList.get(position).getVideoUrl());
                    holder.videoCollectIcon.setImageResource(R.drawable.icon_shouc_d);
                } else {
                    collectOperate = new CollectOperate(laughSQLiteOpenHelper.getReadableDatabase());
                    collectOperate.delete(videoListBeanList.get(position).getVideoId());
                    holder.videoCollectIcon.setImageResource(R.drawable.icon_shouc);
                }
                notifyDataSetChanged();
            }
        }
    }

    class goodClick implements View.OnClickListener {
        private int position;

        goodClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Thread loadThread = new Thread(new LoadThread(videoListBeanList.get(position).getVideoId(), "praise"));
            loadThread.start();
            int playId = v.getId();
            if (playId == holder.videoGoodIcon.getId()) {

                if (new GoodTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchGood(videoListBeanList.get(position).getVideoId())) {
                    goodOperate = new GoodOperate(laughSQLiteOpenHelper.getReadableDatabase());
                    goodOperate.insert(videoListBeanList.get(position).getVideoId());
                    holder.videoGoodIcon.setImageResource(R.drawable.icon_z_d);
                } else {
                    goodOperate = new GoodOperate(laughSQLiteOpenHelper.getReadableDatabase());
                    goodOperate.delete(videoListBeanList.get(position).getVideoId());
                    holder.videoGoodIcon.setImageResource(R.drawable.icon_z);
                }
                notifyDataSetChanged();
            }
        }
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

    class LoadThread implements Runnable {
        private String id;
        private String type;

        LoadThread(String id, String type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public void run() {
            loadData(id, type);
        }
    }

    private void loadData(String videoId, String buttonType) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("videoid", videoId);
        map.put("type", buttonType);
        try {
            String backMsg = PostUtil.postData(BaseUrl + PostVideoInfo, map);
            L.d("运行到此，点赞，收藏加1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder {
        LinearLayout item_background;
        ImageView videoListIcon;
        TextView videoListTitle;
        TextView videoPlayTimes;
        TextView videoCollectTimes;
        TextView videoGoodTimes;
        ImageView videoPlayIcon;
        ImageView videoCollectIcon;
        ImageView videoGoodIcon;
    }
}
