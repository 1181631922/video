package com.cj.dreams.video.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.util.List;

public class FeedBackActivity extends BaseCustomActivity {
    private ListView mListView;
    private FeedbackAgent mAgent;
    private Conversation mComversation;
    private Context mContext;
    private ReplyAdapter adapter;
    private List<Reply> mReplyList;
    private Button sendBtn;
    private EditText inputEdit;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final String TAG = FeedBackActivity.class.getName();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.base_custom_title_bar);
        title = "用户反馈";

        mContext = this;
        initView();
        mAgent = new FeedbackAgent(this);
        mComversation = mAgent.getDefaultConversation();
        adapter = new ReplyAdapter();
        mListView.setAdapter(adapter);
        sync();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.fb_reply_list);
        sendBtn = (Button) findViewById(R.id.fb_send_btn);
        inputEdit = (EditText) findViewById(R.id.fb_send_content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fb_reply_refresh);
        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = inputEdit.getText().toString();
                inputEdit.getEditableText().clear();
                if (!TextUtils.isEmpty(content)) {
                    mComversation.addUserReply(content);//添加到会话列表
                    mHandler.sendMessage(new Message());
                    sync();
                }

            }
        });

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });
    }

    // 数据同步
    private void sync() {

        mComversation.sync(new SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {

            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (replyList == null || replyList.size() < 1) {
                    return;
                }
                mHandler.sendMessage(new Message());
            }
        });
    }

    // adapter
    class ReplyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mComversation.getReplyList().size();
        }

        @Override
        public Object getItem(int arg0) {
            return mComversation.getReplyList().get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.fb_custom_item, null);
                holder = new ViewHolder();
                holder.reply_item = (TextView) convertView.findViewById(R.id.fb_reply_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            Reply reply = mComversation.getReplyList().get(position);
            String content;
            if (Reply.TYPE_DEV_REPLY.endsWith(reply.type)) {
                holder.reply_item.setGravity(Gravity.RIGHT);
                content = reply.content + ": 开发者";
            } else {
                content = "用  户 :" + reply.content;
            }
            holder.reply_item.setText(content);
            return convertView;
        }


        class ViewHolder {
            TextView reply_item;
        }
    }

}
