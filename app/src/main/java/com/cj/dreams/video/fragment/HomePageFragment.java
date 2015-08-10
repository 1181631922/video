package com.cj.dreams.video.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.LocalRecordActivity;
import com.cj.dreams.video.activity.VideoListActivity;
import com.cj.dreams.video.activity.VideoViewPlayingActivity;
import com.cj.dreams.video.adapter.IndexListViewAdapter;
import com.cj.dreams.video.bean.IndexListViewBean;
import com.cj.dreams.video.bean.IndexUrlBean;
import com.cj.dreams.video.bean.RollScreenBean;
import com.cj.dreams.video.layout.PullToRefreshLayout;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;
import com.cj.dreams.video.util.SP;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomePageFragment extends BaseFragment {
    private PullToRefreshLayout ptrl;
    private ListView listView;
    private IndexListViewAdapter indexListViewAdapter;
    private List<IndexListViewBean> indexListViewBeanList = new ArrayList<IndexListViewBean>();
    private List<IndexListViewBean> indexListViewBeanList_loadmore = new ArrayList<IndexListViewBean>();
    private List<IndexListViewBean> indexListViewBeanList_final = new ArrayList<IndexListViewBean>();
    private List<List<Map<String, Object>>> showListList = new ArrayList<List<Map<String, Object>>>();
    private List<Map<String, Object>> showList = new ArrayList<Map<String, Object>>();
    private List<IndexUrlBean> indexUrlBeanList = new ArrayList<IndexUrlBean>();
    private List<IndexUrlBean> indexUrlBeanList_more = new ArrayList<IndexUrlBean>();
    private String MinId, m3u8, MaxId;
    //    private IndexUrlBean indexUrlBean = new IndexUrlBean(0, null, null, null, null);
    private Handler handler1, handler2;
    private List<Map<String, Object>> moreVideoList = new ArrayList<Map<String, Object>>();

    private RelativeLayout index_view_header;
    private ViewPager view_pager;
    private LayoutInflater inflater;
    private LinearLayout scorll_linearlayout;
    private ImageView image, index_record;
    private View item;
    private MyAdapter adapter;
    private ImageView[] indicator_imgs;// 存放引到图片数组
    private JSONArray jsonArray;
    private List<RollScreenBean> rollScreenBeanList = new ArrayList<RollScreenBean>();
    private LinearLayout index_indicator_background;
    private int currPage;
    private MyAdapter myAdapter;
    private TextView listview_footer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
//        initPagerView();
        Thread loadThread = new Thread(new LoadThread());
        loadThread.start();
    }

    Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    jsonArray = (JSONArray) msg.obj;
                    L.d("handler中的jsonarray", jsonArray.toString());
                    if (jsonArray.length() != 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                RollScreenBean rollScreenBean = new RollScreenBean(null, null, null, null, null, null, null, null, null, null);
                                JSONObject rollScreenBeanItem = jsonArray.getJSONObject(i);
                                rollScreenBean.setIndex(rollScreenBeanItem.getString("index"));
                                rollScreenBean.setRollScreenTitle(rollScreenBeanItem.getString("rollscreen_title"));
                                rollScreenBean.setTitle(rollScreenBeanItem.getString("title"));
                                rollScreenBean.setRollScreenImage(BaseUrl + rollScreenBeanItem.getString("rollscreen_image"));
                                rollScreenBean.setImage(BaseUrl + rollScreenBeanItem.getString("image"));
                                rollScreenBean.setVideoId(rollScreenBeanItem.getString("videoid"));
                                rollScreenBean.setVideoUrl(rollScreenBeanItem.getString("url"));
                                rollScreenBean.setGoodNumber(Integer.parseInt(rollScreenBeanItem.optString("praise_num")) + "");
                                rollScreenBean.setCollectNumber(Integer.parseInt(rollScreenBeanItem.optString("collect_num")) + "");
                                rollScreenBeanList.add(rollScreenBean);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
//                        listView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.item_index_header, null));
                        try {
                            index_view_header = (RelativeLayout) getActivity().findViewById(R.id.index_view_header);
                            index_view_header.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        initPagerView();
                    } else {
//                        scorll_linearlayout.setVisibility(View.INVISIBLE);
                        LayoutInflater.from(getActivity()).inflate(R.layout.item_index_header, null).setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    private void initPagerView() {

        view_pager = (ViewPager) getActivity().findViewById(R.id.index_view_pager);
        List<View> list = new ArrayList<View>();
        inflater = LayoutInflater.from(getActivity());

        /**
         * 创建多个item （每一条viewPager都是一个item） 从服务器获取完数据（如文章标题、url地址） 后，再设置适配器
         */
        for (int i = 0; i < jsonArray.length(); i++) {
            item = inflater.inflate(R.layout.item_index_image, null);
            ((TextView) item.findViewById(R.id.index_scorll_text)).setText(rollScreenBeanList.get(i).getRollScreenTitle());
            ((TextView) item.findViewById(R.id.index_scorll_text)).bringToFront();
            list.add(item);
        }

        // 创建适配器， 把组装完的组件传递进去
        adapter = new MyAdapter(list);
//        listView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.item_index_header, null));
        view_pager.setAdapter(adapter);

        // 绑定动作监听器：如翻页的动画
        view_pager.setOnPageChangeListener(new ScorllListener());

        initIndicator();
        // 开启定时器，每隔2秒自动播放下一张（通过调用线程实现）（与Timer类似，可使用Timer代替）
        ScheduledExecutorService scheduled = Executors
                .newSingleThreadScheduledExecutor();
        // 设置一个线程，该线程用于通知UI线程变换图片
        ViewPagerTask pagerTask = new ViewPagerTask();
        scheduled.scheduleAtFixedRate(pagerTask, 4, 4, TimeUnit.SECONDS);
    }

    private class ViewPagerTask implements Runnable {
        @Override
        public void run() {
            // 改变当前页面的值
            currPage = (currPage + 1) % jsonArray.length();
            // 发送消息给UI线程
            handler4.sendEmptyMessage(0);
        }
    }

    Handler handler4 = new Handler() {
        public void handleMessage(Message msg) {
            // 接收到消息后，更新页面
            view_pager.setCurrentItem(currPage);
        }

    };

    /**
     * 初始化引导图标 动态创建多个小圆点，然后组装到线性布局里
     */
    private void initIndicator() {
        indicator_imgs = new ImageView[jsonArray.length()];
        ImageView imgView;
        View v = getActivity().findViewById(R.id.index_indicator);// 线性水平布局，负责动态调整导航图标

        for (int i = 0; i < jsonArray.length(); i++) {
            imgView = new ImageView(getActivity());
//            LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(20, 20);
            LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(15, 15);
            params_linear.setMargins(10, 15, 10, 15);
//            params_linear.setm
            imgView.setLayoutParams(params_linear);
            indicator_imgs[i] = imgView;
            if (i == 0) { // 初始化第一个为选中状态
                indicator_imgs[i].setBackgroundResource(R.drawable.indicator_focused);
            } else {
                indicator_imgs[i].setBackgroundResource(R.drawable.indicator);
            }
            ((ViewGroup) v).addView(indicator_imgs[i]);
        }

//        listView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.item_index_header, null));

    }

    /**
     * 适配器，负责装配 、销毁 数据 和 组件 。
     */
    private class MyAdapter extends PagerAdapter {

        private List<View> mList;

        private AsyncImageLoader asyncImageLoader;

        public MyAdapter(List<View> list) {
            mList = list;
            asyncImageLoader = new AsyncImageLoader();
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }

        /**
         * Remove a page for the given position. 滑动过后就销毁 ，销毁当前页的前一个的前一个的页！
         * instantiateItem(View container, int position) This method was
         * deprecated in API level . Use instantiateItem(ViewGroup, int)
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView(mList.get(position));

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        /**
         * Create the page for the given position.
         */
        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {

            Drawable cachedImage = asyncImageLoader.loadDrawable(rollScreenBeanList.get(position).getRollScreenImage(), new AsyncImageLoader.ImageCallback() {

                public void imageLoaded(Drawable imageDrawable, String imageUrl) {

                    View view = mList.get(position);
                    image = ((ImageView) view.findViewById(R.id.index_scorll_img));
                    //4.0不兼容
//                    image.setBackground(imageDrawable);
                    image.setBackgroundDrawable(imageDrawable);
                    container.removeView(mList.get(position));
                    container.addView(mList.get(position));
                    // adapter.notifyDataSetChanged();

                }
            });

            View view = mList.get(position);
            image = ((ImageView) view.findViewById(R.id.index_scorll_img));
            //4.0不兼容
//            image.setBackground(cachedImage);
            image.setBackgroundDrawable(cachedImage);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    T.showShort(getActivity(), position + "第几个");
                    Thread oadThread = new Thread(new postThread(rollScreenBeanList.get(position).getVideoId(), "play"));
                    oadThread.start();
                    Intent intent = new Intent(getActivity(), VideoViewPlayingActivity.class);
                    intent.putExtra("url_info", rollScreenBeanList.get(position).getVideoUrl());
                    intent.putExtra("id_info", rollScreenBeanList.get(position).getVideoId());
                    intent.putExtra("title_info", rollScreenBeanList.get(position).getTitle());
                    intent.putExtra("image_info", rollScreenBeanList.get(position).getImage());
                    intent.putExtra("good_info", rollScreenBeanList.get(position).getGoodNumber());
                    intent.putExtra("collect_info", rollScreenBeanList.get(position).getCollectNumber());
                    startActivity(intent);
                }
            });

            container.removeView(mList.get(position));
            container.addView(mList.get(position));
            // adapter.notifyDataSetChanged();

            return mList.get(position);

        }

    }

    /**
     * 动作监听器，可异步加载图片
     */
    private class ScorllListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
            if (state == 0) {
                // new MyAdapter(null).notifyDataSetChanged();
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {

            // 改变所有导航的背景图片为：未选中
            for (int i = 0; i < indicator_imgs.length; i++) {

                indicator_imgs[i].setBackgroundResource(R.drawable.indicator);

            }

            // 改变当前背景图片为：选中
            indicator_imgs[position].setBackgroundResource(R.drawable.indicator_focused);
        }

    }

    /**
     * 异步加载图片
     */
    static class AsyncImageLoader {

        // 软引用，使用内存做临时缓存 （程序退出，或内存不够则清除软引用）
        private HashMap<String, SoftReference<Drawable>> imageCache;

        public AsyncImageLoader() {
            imageCache = new HashMap<String, SoftReference<Drawable>>();
        }

        /**
         * 定义回调接口
         */
        public interface ImageCallback {
            public void imageLoaded(Drawable imageDrawable, String imageUrl);
        }

        /**
         * 创建子线程加载图片 子线程加载完图片交给handler处理（子线程不能更新ui，而handler处在主线程，可以更新ui）
         * handler又交给imageCallback，imageCallback须要自己来实现，在这里可以对回调参数进行处理
         *
         * @param imageUrl      ：须要加载的图片url
         * @param imageCallback ：
         * @return
         */
        public Drawable loadDrawable(final String imageUrl, final ImageCallback imageCallback) {

            // 如果缓存中存在图片 ，则首先使用缓存
            if (imageCache.containsKey(imageUrl)) {
                SoftReference<Drawable> softReference = imageCache.get(imageUrl);
                Drawable drawable = softReference.get();
                if (drawable != null) {
                    imageCallback.imageLoaded(drawable, imageUrl);// 执行回调
                    return drawable;
                }
            }

            /**
             * 在主线程里执行回调，更新视图
             */
            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
                }
            };

            /**
             * 创建子线程访问网络并加载图片 ，把结果交给handler处理
             */
            new Thread() {
                @Override
                public void run() {
                    Drawable drawable = loadImageFromUrl(imageUrl);
                    // 下载完的图片放到缓存里
                    imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                    Message message = handler.obtainMessage(0, drawable);
                    handler.sendMessage(message);
                }
            }.start();

            return null;
        }

        /**
         * 下载图片 （注意HttpClient 和httpUrlConnection的区别）
         */
        public Drawable loadImageFromUrl(String url) {

            try {
                HttpClient client = new DefaultHttpClient();
                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 * 15);
                HttpGet get = new HttpGet(url);
                HttpResponse response;

                response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    Drawable d = Drawable.createFromStream(entity.getContent(), "src");
                    return d;
                } else {
                    return null;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        // 清除缓存
        public void clearCache() {

            if (this.imageCache.size() > 0) {

                this.imageCache.clear();
            }

        }

    }

    class LoadThread implements Runnable {
        @Override
        public void run() {
            loadData("update_home", "0");
        }
    }

    private String loadData(String updatetype, String id) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("devicetype", "android");
        map.put("updatetype", updatetype);
        map.put("periodicalid", id);
        //邯郸团队测试包测试固定值
        if (!(boolean) SP.get(getActivity(), SP.TeamTestType, false)) {
            map.put("testvideo", "1");
        }
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetHomeInfo, map);
            L.d("主页请求下来的数据", backMsg.toString());
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                JSONArray periodical = jsonObject.getJSONArray("periodical");
                L.d(periodical.toString());
                if (id.equals("0")) {
                    JSONArray rollscreen = jsonObject.getJSONArray("rollscreen");
                    L.d("滚屏的json串", rollscreen.toString());
                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = rollscreen;
                    handler3.sendMessage(message);
                }
                indexUrlBeanList_more.clear();
                indexListViewBeanList_loadmore.clear();
                for (int i = 0; i < periodical.length(); i++) {
                    IndexListViewBean indexListViewBean = new IndexListViewBean(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
                    JSONObject periodicalinfo = periodical.getJSONObject(i);
                    if (id.equals("0") && i == 0) {
                        MaxId = periodicalinfo.getString("id");
                        L.d("初次最大的id", MaxId);
                        MinId = periodicalinfo.getString("id");
                        L.d("初次最小的id", MinId);
                    }
                    if (Integer.parseInt(MinId) >= Integer.parseInt(periodicalinfo.getString("id"))) {
                        MinId = periodicalinfo.getString("id");
                        L.d("最小的id", MinId);
                    }
//                    if (Integer.parseInt(MinId) == Integer.parseInt(periodicalinfo.getString("id")) && MinId != MaxId && !id.equals("0")) {
//                        Message message = Message.obtain();
//                        message.what = 8;
//                        handler.sendMessage(message);
//                    }
//                    MinId = periodicalinfo.getString("id");
                    if (Integer.parseInt(MaxId) <= Integer.parseInt(periodicalinfo.getString("id"))) {
                        MaxId = periodicalinfo.getString("id");
                        L.d("最大的id", MaxId);
                    }
                    String name = periodicalinfo.getString("name");
                    indexListViewBean.setTitle(name);
                    JSONArray video = periodicalinfo.getJSONArray("video");
                    for (int j = 0; j < video.length(); j++) {
                        JSONObject videoinfo = video.getJSONObject(j);
                        Map<String, Object> urlmap = new HashMap<String, Object>();
                        urlmap.put("urlmap", (String) videoinfo.getString("url"));
                        showList.add(urlmap);

                        if (j == 0) {
                            indexListViewBean.setLeftTopId(videoinfo.getString("id"));
                            indexListViewBean.setLeftTopUrl(videoinfo.getString("url"));
                            indexListViewBean.setLeftTopTimes(Integer.parseInt(videoinfo.getString("play_number")) + "播放");
                            indexListViewBean.setLeftTopTitle(videoinfo.getString("title"));
                            indexListViewBean.setLeftTopImg(BaseUrl + videoinfo.getString("image"));
                            indexListViewBean.setLeftTopCollect(Integer.parseInt(videoinfo.getString("collect_num")) + "");
                            indexListViewBean.setLeftTopGood(Integer.parseInt(videoinfo.getString("praise_num")) + "");
                        }
                        if (j == 1) {
                            indexListViewBean.setRightTopId(videoinfo.getString("id"));
                            indexListViewBean.setRightTopUrl(videoinfo.getString("url"));
                            indexListViewBean.setRightTopTimes(Integer.parseInt(videoinfo.getString("play_number")) + "播放");
                            indexListViewBean.setRightTopTitle(videoinfo.getString("title"));
                            indexListViewBean.setRightTopImg(BaseUrl + videoinfo.getString("image"));
                            indexListViewBean.setRightTopCollect(Integer.parseInt(videoinfo.getString("collect_num")) + "");
                            indexListViewBean.setRightTopGood(Integer.parseInt(videoinfo.getString("praise_num")) + "");
                        }
                        if (j == 2) {
                            indexListViewBean.setLeftBottomId(videoinfo.getString("id"));
                            indexListViewBean.setLeftBottonUrl(videoinfo.getString("url"));
                            indexListViewBean.setLeftBottomTimes(Integer.parseInt(videoinfo.getString("play_number")) + "播放");
                            indexListViewBean.setLeftBottomTitle(videoinfo.getString("title"));
                            indexListViewBean.setLeftBottomImg(BaseUrl + videoinfo.getString("image"));
                            indexListViewBean.setLeftBottomCollect(Integer.parseInt(videoinfo.getString("collect_num")) + "");
                            indexListViewBean.setLeftBottmoGood(Integer.parseInt(videoinfo.getString("praise_num")) + "");
                        }
                        if (j == 3) {
                            indexListViewBean.setRightBottomId(videoinfo.getString("id"));
                            indexListViewBean.setRightBottonUrl(videoinfo.getString("url"));
                            indexListViewBean.setRightBottomTimes(Integer.parseInt(videoinfo.getString("play_number")) + "播放");
                            indexListViewBean.setRightBottomTitle(videoinfo.getString("title"));
                            indexListViewBean.setRightBottomImg(BaseUrl + videoinfo.getString("image"));
                            indexListViewBean.setRightBottomCollect(Integer.parseInt(videoinfo.getString("collect_num")) + "");
                            indexListViewBean.setRightBottomGood(Integer.parseInt(videoinfo.getString("praise_num")) + "");
                        }
                    }
                    Map<String, Object> idmap = new HashMap<String, Object>();
                    idmap.put("periodicalid", periodicalinfo.getString("id"));
                    moreVideoList.add(idmap);
                    if (id.equals("0")) {
                        indexListViewBeanList.add(indexListViewBean);
                    } else {
                        indexListViewBeanList_loadmore.add(indexListViewBean);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message = Message.obtain();
        message.what = 0;
        message.obj = id;
        if (message.obj.equals("0")) {
            handler.sendMessage(message);
        } else {
            handler1.sendMessage(message);
        }
        return MinId;
    }

    private String refreshData(String updatetype, String id) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("devicetype", "android");
        map.put("updatetype", updatetype);
        map.put("periodicalid", id);
        if (!(boolean) SP.get(getActivity(), SP.TeamTestType, false)) {
            map.put("testvideo", "1");
        }
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetHomeInfo, map);
            L.d(backMsg.toString());
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                JSONArray periodical = jsonObject.getJSONArray("periodical");
                L.d(periodical.toString());
                if (periodical.toString().equals("[]")) {
                    Message message = Message.obtain();
                    message.what = 0;
                    handler2.sendMessage(message);
                } else {
                    for (int i = 0; i < periodical.length(); i++) {
                        IndexListViewBean indexListViewBean = new IndexListViewBean(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
                        IndexUrlBean indexUrlBean = new IndexUrlBean(0, null, null, null, null);
                        JSONObject periodicalinfo = periodical.getJSONObject(i);
                        if (id.equals("0") && i == 0) {
                            MaxId = periodicalinfo.getString("id");
                            L.d("初次最大的id", MaxId);
                            MinId = periodicalinfo.getString("id");
                            L.d("初次最小的id", MinId);
                        }
                        if (Integer.parseInt(MinId) >= Integer.parseInt(periodicalinfo.getString("id"))) {
                            MinId = periodicalinfo.getString("id");
                            L.d("最小的id", MinId);
                        }
//                    MinId = periodicalinfo.getString("id");
                        if (Integer.parseInt(MaxId) <= Integer.parseInt(periodicalinfo.getString("id"))) {
                            MaxId = periodicalinfo.getString("id");
                            L.d("最大的id", MaxId);
                        }
                        String name = periodicalinfo.getString("name");
                        indexListViewBean.setTitle(name);
                        JSONArray video = periodicalinfo.getJSONArray("video");
                        indexUrlBean.setPosition(i);
                        for (int j = 0; j < video.length(); j++) {
                            JSONObject videoinfo = video.getJSONObject(j);
                            Map<String, Object> urlmap = new HashMap<String, Object>();
                            urlmap.put("urlmap", (String) videoinfo.getString("url"));
                            showList.add(urlmap);

                            if (j == 0) {
                                indexListViewBean.setLeftTopId(videoinfo.getString("id"));
                                indexListViewBean.setLeftTopUrl(videoinfo.getString("url"));
                                indexListViewBean.setLeftTopTimes(Integer.parseInt(videoinfo.getString("play_number")) + "播放");
                                indexListViewBean.setLeftTopTitle(videoinfo.getString("title"));
                                indexListViewBean.setLeftTopImg(BaseUrl + videoinfo.getString("image"));
                                indexListViewBean.setLeftTopCollect(Integer.parseInt(videoinfo.getString("collect_num")) + "");
                                indexListViewBean.setLeftTopGood(Integer.parseInt(videoinfo.getString("praise_num")) + "");
                            }
                            if (j == 1) {
                                indexListViewBean.setRightTopId(videoinfo.getString("id"));
                                indexListViewBean.setRightTopUrl(videoinfo.getString("url"));
                                indexListViewBean.setRightTopTimes(Integer.parseInt(videoinfo.getString("play_number")) + "播放");
                                indexListViewBean.setRightTopTitle(videoinfo.getString("title"));
                                indexListViewBean.setRightTopImg(BaseUrl + videoinfo.getString("image"));
                                indexListViewBean.setRightTopCollect(Integer.parseInt(videoinfo.getString("collect_num")) + "");
                                indexListViewBean.setRightTopGood(Integer.parseInt(videoinfo.getString("praise_num")) + "");
                            }
                            if (j == 2) {
                                indexListViewBean.setLeftBottomId(videoinfo.getString("id"));
                                indexListViewBean.setLeftBottonUrl(videoinfo.getString("url"));
                                indexListViewBean.setLeftBottomTimes(Integer.parseInt(videoinfo.getString("play_number")) + "播放");
                                indexListViewBean.setLeftBottomTitle(videoinfo.getString("title"));
                                indexListViewBean.setLeftBottomImg(BaseUrl + videoinfo.getString("image"));
                                indexListViewBean.setLeftBottomCollect(Integer.parseInt(videoinfo.getString("collect_num")) + "");
                                indexListViewBean.setLeftBottmoGood(Integer.parseInt(videoinfo.getString("praise_num")) + "");
                            }
                            if (j == 3) {
                                indexListViewBean.setRightBottomId(videoinfo.getString("id"));
                                indexListViewBean.setRightBottonUrl(videoinfo.getString("url"));
                                indexListViewBean.setRightBottomTimes(Integer.parseInt(videoinfo.getString("play_number")) + "播放");
                                indexListViewBean.setRightBottomTitle(videoinfo.getString("title"));
                                indexListViewBean.setRightBottomImg(BaseUrl + videoinfo.getString("image"));
                                indexListViewBean.setRightBottomCollect(Integer.parseInt(videoinfo.getString("collect_num")) + "");
                                indexListViewBean.setRightBottomGood(Integer.parseInt(videoinfo.getString("praise_num")) + "");
                            }
                        }
                        indexUrlBeanList.clear();
                        indexListViewBeanList.clear();
                        indexUrlBeanList.add(indexUrlBean);
                        indexListViewBeanList.add(indexListViewBean);
                        Message message = Message.obtain();
                        message.what = 0;
                        handler2.sendMessage(message);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return MinId;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (msg.obj.equals("0")) {
                        indexListViewAdapter.update();
                        listView.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.item_listview_footer, null));
                    }
                    break;
                case 1:
                    break;
                case 8:
                    listview_footer.setText("已经全部加载");
                    break;
            }
        }
    };


    private void initView() {
//        index_view_header=(RelativeLayout)getActivity().findViewById(R.id.index_view_header);
        listview_footer = (TextView) getActivity().findViewById(R.id.listview_footer);
        scorll_linearlayout = (LinearLayout) getActivity().findViewById(R.id.scorll_linearlayout);
        index_indicator_background = (LinearLayout) getActivity().findViewById(R.id.index_indicator_background);
        ptrl = ((PullToRefreshLayout) getActivity().findViewById(R.id.refresh_homepage_view));
        ptrl.setOnRefreshListener(new MyListener());
        listView = (ListView) getActivity().findViewById(R.id.homepage_listview);

        indexListViewAdapter = new IndexListViewAdapter(getActivity(), indexListViewBeanList);
        indexListViewAdapter.update();
        listView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.item_index_header, null));
        listView.setAdapter(indexListViewAdapter);

//        listView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.item_index_header, null));
//        listView.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.item_listview_footer, null));
        listView.setFocusable(true);
        listView.setOnItemClickListener(new IndexOnItemClickListener());

        index_record = (ImageView) getActivity().findViewById(R.id.index_record);
        index_record.setOnClickListener(this);
    }

    private class IndexOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //增加header前的准备工作
            Intent intent = new Intent(getActivity(), VideoListActivity.class);
            for (int i = 0; i <= parent.getAdapter().getItemId(position); i++) {
                if (parent.getAdapter().getItemId(position) == i) {
                    Map map = (Map) moreVideoList.get(i);
                    intent.putExtra("periodicalid", (String) map.get("periodicalid"));
                }
            }
            startActivity(intent);
        }
    }

    private class MyListener implements PullToRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Thread refresh = new Thread(new Refresh());
                    refresh.start();
                    handler2 = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case 0:
                                    indexListViewAdapter.update();
                                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                    break;
                            }
                        }
                    };
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessageDelayed(0, 500);
        }

        @Override
        public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Thread lodeMore = new Thread(new LoadMore());
                    lodeMore.start();

                    handler1 = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case 0:
                                    if (!msg.obj.equals("0") && !msg.equals(MaxId)) {
                                        indexUrlBeanList.addAll(indexUrlBeanList_more);
                                        L.d("indexurlbeanlist的长度", indexUrlBeanList.size() + "");
                                        L.d(indexUrlBeanList.toString());
                                        indexListViewBeanList.addAll(indexListViewBeanList_loadmore);
                                        indexListViewAdapter.update();
                                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                                    }
                                    break;
                            }
                        }
                    };
                }
            }.sendEmptyMessageDelayed(0, 500);
        }

    }

    class LoadMore implements Runnable {
        @Override
        public void run() {
            loadData("update_home_2down", MinId);
        }
    }

    class Refresh implements Runnable {
        @Override
        public void run() {
            refreshData("update_home_2top", MaxId);
        }
    }

    class postThread implements Runnable {
        private String id;
        private String type;

        postThread(String id, String type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public void run() {
            postData(id, type);
        }
    }

    private void postData(String videoId, String buttonType) {
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.index_record:
                Intent intent1 = new Intent(getActivity(), LocalRecordActivity.class);
                intent1.putExtra("local_title", "播放记录");
                startActivity(intent1);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
