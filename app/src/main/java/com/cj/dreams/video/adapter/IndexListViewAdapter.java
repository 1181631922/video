package com.cj.dreams.video.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.VideoViewPlayingActivity;
import com.cj.dreams.video.bean.IndexListViewBean;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.dbhelper.RecordTableCourse;
import com.cj.dreams.video.dboperate.RecordOperate;
import com.cj.dreams.video.util.ImageLoaderCache;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.ListViewImageTaskUtil;
import com.cj.dreams.video.util.PostUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanyafeng on 2015/7/1.
 */
public class IndexListViewAdapter extends ABaseAdapter {

    private Context context;
    private List<IndexListViewBean> indexListViewBeans;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private RecordOperate recordOperate;

    //缓存到本地sd卡，并且可以更新ListView图片
    private ImageLoaderCache mImageLoader;

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

    public IndexListViewAdapter(Context context, List<IndexListViewBean> indexListViewBeans) {
        this.context = context;
        this.indexListViewBeans = indexListViewBeans;
        mImageLoader = new ImageLoaderCache(context);
    }

    public ImageLoaderCache getImagerLoader() {
        return mImageLoader;
    }

    public void update() {
        notifyDataSetChanged();
    }


//    public IndexListViewAdapter(ArrayList<ListView.FixedViewInfo> headerViewInfos, ArrayList<ListView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
//        super(headerViewInfos, footerViewInfos, adapter);
//    }


    @Override
    public int getCount() {
        return indexListViewBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return indexListViewBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view = convertView;
        ViewHolder holder = null;
        try {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_fragment_homepage, null);
                holder = new ViewHolder();
                view.setTag(holder);
                holder.Title = (TextView) view.findViewById(R.id.index_title);
                holder.LeftTopImg = (ImageView) view.findViewById(R.id.index_left_up_iv);
                holder.LeftTopTitle = (TextView) view.findViewById(R.id.index_left_up_tv);
                holder.LeftTopTimes = (TextView) view.findViewById(R.id.index_left_up_time);
                holder.RightTopImg = (ImageView) view.findViewById(R.id.index_right_up_iv);
                holder.RightTopTitle = (TextView) view.findViewById(R.id.index_right_up_tv);
                holder.RightTopTimes = (TextView) view.findViewById(R.id.index_right_up_time);
                holder.LeftBottomImg = (ImageView) view.findViewById(R.id.index_left_down_iv);
                holder.LeftBottomTitle = (TextView) view.findViewById(R.id.index_left_down_tv);
                holder.LeftBottomTimes = (TextView) view.findViewById(R.id.index_left_down_time);
                holder.RightBottomImg = (ImageView) view.findViewById(R.id.index_right_down_iv);
                holder.RightBottomTitle = (TextView) view.findViewById(R.id.index_right_down_tv);
                holder.RightBottomTimes = (TextView) view.findViewById(R.id.index_right_down_time);


            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.LeftTopImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread loadThread = new Thread(new LoadThread(indexListViewBeans.get(position).getLeftTopId(), "play"));
                    loadThread.start();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setClass(context, VideoViewPlayingActivity.class);
                    intent.putExtra("url_info", indexListViewBeans.get(position).getLeftTopUrl());
                    intent.putExtra("id_info", indexListViewBeans.get(position).getLeftTopId());
                    intent.putExtra("title_info", indexListViewBeans.get(position).getLeftTopTitle());
                    intent.putExtra("image_info", indexListViewBeans.get(position).getLeftTopImg());
                    intent.putExtra("good_info", indexListViewBeans.get(position).getLeftTopGood());
                    intent.putExtra("collect_info", indexListViewBeans.get(position).getLeftTopCollect());
                    laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(context);
                    laughSQLiteOpenHelper.getWritableDatabase();
                    if (new RecordTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchRecord(indexListViewBeans.get(position).getLeftTopId())) {
                        recordOperate = new RecordOperate(laughSQLiteOpenHelper.getReadableDatabase());
                        recordOperate.insert(indexListViewBeans.get(position).getLeftTopId(), indexListViewBeans.get(position).getLeftTopImg(), indexListViewBeans.get(position).getLeftTopTitle(), indexListViewBeans.get(position).getLeftTopUrl());
                    }
                    context.startActivity(intent);
                }
            });
            holder.RightTopImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread loadThread = new Thread(new LoadThread(indexListViewBeans.get(position).getRightTopId(), "play"));
                    loadThread.start();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setClass(context, VideoViewPlayingActivity.class);
                    intent.putExtra("url_info", indexListViewBeans.get(position).getRightTopUrl());
                    intent.putExtra("id_info", indexListViewBeans.get(position).getRightTopId());
                    intent.putExtra("title_info", indexListViewBeans.get(position).getRightTopTitle());
                    intent.putExtra("image_info", indexListViewBeans.get(position).getRightTopImg());
                    intent.putExtra("good_info", indexListViewBeans.get(position).getRightTopGood());
                    intent.putExtra("collect_info", indexListViewBeans.get(position).getRightTopCollect());
                    laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(context);
                    laughSQLiteOpenHelper.getWritableDatabase();
                    if (new RecordTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchRecord(indexListViewBeans.get(position).getRightTopId())) {
                        recordOperate = new RecordOperate(laughSQLiteOpenHelper.getReadableDatabase());
                        recordOperate.insert(indexListViewBeans.get(position).getRightTopId(), indexListViewBeans.get(position).getRightTopImg(), indexListViewBeans.get(position).getRightTopTitle(), indexListViewBeans.get(position).getRightTopUrl());
                    }
                    context.startActivity(intent);
                }
            });
            holder.LeftBottomImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread loadThread = new Thread(new LoadThread(indexListViewBeans.get(position).getLeftBottomId(), "play"));
                    loadThread.start();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setClass(context, VideoViewPlayingActivity.class);
                    intent.putExtra("url_info", indexListViewBeans.get(position).getLeftBottonUrl());
                    intent.putExtra("id_info", indexListViewBeans.get(position).getLeftBottomId());
                    intent.putExtra("title_info", indexListViewBeans.get(position).getLeftBottomTitle());
                    intent.putExtra("image_info", indexListViewBeans.get(position).getLeftBottomImg());
                    intent.putExtra("good_info", indexListViewBeans.get(position).getLeftBottmoGood());
                    intent.putExtra("collect_info", indexListViewBeans.get(position).getLeftBottomCollect());
                    laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(context);
                    laughSQLiteOpenHelper.getWritableDatabase();
                    if (new RecordTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchRecord(indexListViewBeans.get(position).getLeftBottomId())) {
                        recordOperate = new RecordOperate(laughSQLiteOpenHelper.getReadableDatabase());
                        recordOperate.insert(indexListViewBeans.get(position).getLeftBottomId(), indexListViewBeans.get(position).getLeftBottomImg(), indexListViewBeans.get(position).getLeftBottomTitle(), indexListViewBeans.get(position).getLeftBottonUrl());
                    }
                    context.startActivity(intent);
                }
            });
            holder.RightBottomImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread loadThread = new Thread(new LoadThread(indexListViewBeans.get(position).getRightBottomId(), "play"));
                    loadThread.start();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setClass(context, VideoViewPlayingActivity.class);
                    intent.putExtra("url_info", indexListViewBeans.get(position).getRightBottonUrl());
                    intent.putExtra("id_info", indexListViewBeans.get(position).getRightBottomId());
                    intent.putExtra("title_info", indexListViewBeans.get(position).getRightBottomTitle());
                    intent.putExtra("image_info", indexListViewBeans.get(position).getRightBottomImg());
                    intent.putExtra("good_info", indexListViewBeans.get(position).getRightBottomGood());
                    intent.putExtra("collect_info", indexListViewBeans.get(position).getRightBottomCollect());
                    laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(context);
                    laughSQLiteOpenHelper.getWritableDatabase();
                    if (new RecordTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchRecord(indexListViewBeans.get(position).getRightBottomId())) {
                        recordOperate = new RecordOperate(laughSQLiteOpenHelper.getReadableDatabase());
                        recordOperate.insert(indexListViewBeans.get(position).getRightBottomId(), indexListViewBeans.get(position).getRightBottomImg(), indexListViewBeans.get(position).getRightBottomTitle(), indexListViewBeans.get(position).getRightBottonUrl());
                    }
                    context.startActivity(intent);
                }
            });
            holder.Title.setText(indexListViewBeans.get(position).getTitle());
            holder.LeftTopTitle.setText(indexListViewBeans.get(position).getLeftTopTitle());
            holder.LeftTopTimes.setText(indexListViewBeans.get(position).getLeftTopTimes());
            holder.RightTopTitle.setText(indexListViewBeans.get(position).getRightTopTitle());
            holder.RightTopTimes.setText(indexListViewBeans.get(position).getRightTopTimes());
            holder.LeftBottomTitle.setText(indexListViewBeans.get(position).getLeftBottomTitle());
            holder.LeftBottomTimes.setText(indexListViewBeans.get(position).getLeftBottomTimes());
            holder.RightBottomTitle.setText(indexListViewBeans.get(position).getRightBottomTitle());
            holder.RightBottomTimes.setText(indexListViewBeans.get(position).getRightBottomTimes());
            loadBitmap(indexListViewBeans.get(position).getLeftTopImg(), holder.LeftTopImg);
            loadBitmap(indexListViewBeans.get(position).getRightTopImg(), holder.RightTopImg);
            loadBitmap(indexListViewBeans.get(position).getLeftBottomImg(), holder.LeftBottomImg);
            loadBitmap(indexListViewBeans.get(position).getRightBottomImg(), holder.RightBottomImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            L.d("运行到此，播放次数加1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder {
        TextView Title;
        ImageView LeftTopImg;
        TextView LeftTopTitle;
        TextView LeftTopTimes;
        ImageView RightTopImg;
        TextView RightTopTitle;
        TextView RightTopTimes;
        ImageView LeftBottomImg;
        TextView LeftBottomTitle;
        TextView LeftBottomTimes;
        ImageView RightBottomImg;
        TextView RightBottomTitle;
        TextView RightBottomTimes;
    }
}