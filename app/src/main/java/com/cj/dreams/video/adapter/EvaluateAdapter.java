package com.cj.dreams.video.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.bean.EvaluateUserBean;
import com.cj.dreams.video.bean.LocalRecordBean;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.util.EncryptUtil;
import com.cj.dreams.video.util.L;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanyafeng on 2015/7/15/0015.
 */
public class EvaluateAdapter extends ABaseAdapter {
    private Context context;
    private List<EvaluateUserBean> evaluateUserBeanList;
    private SQLiteDatabase db = null;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;

    public EvaluateAdapter(Context context, List<EvaluateUserBean> evaluateUserBeanList) {
        this.context = context;
        this.evaluateUserBeanList = evaluateUserBeanList;
        this.laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(context);
        laughSQLiteOpenHelper.getWritableDatabase();
        db = context.openOrCreateDatabase("laughvideo.db", Context.MODE_PRIVATE, null);
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return evaluateUserBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return evaluateUserBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_fragment_evaluate, null);
            holder = new ViewHolder();
            view.setTag(holder);
            holder.evaluateusericon = (ImageView) view.findViewById(R.id.evaluate_icon);
            holder.evaluateuser = (TextView) view.findViewById(R.id.evaluate_name);
            holder.evaluatetime = (TextView) view.findViewById(R.id.evaluate_time);
            holder.evaluatedetail = (TextView) view.findViewById(R.id.evaluate_detail);
            holder.evaluategoodtimes = (TextView) view.findViewById(R.id.evaluate_good_times);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.evaluateusericon.setImageBitmap(getUsericon(evaluateUserBeanList.get(position).getEvaluateUser()));
        holder.evaluateuser.setText(getUserName(evaluateUserBeanList.get(position).getEvaluateUser()));
        holder.evaluatetime.setText(evaluateUserBeanList.get(position).getEvaluateTime());
        holder.evaluatedetail.setText(evaluateUserBeanList.get(position).getEvaluateDetail());
        holder.evaluategoodtimes.setText(evaluateUserBeanList.get(position).getEvaluateGoodTimes());

        return view;
    }

    private String getUserName(String userId) {
        String sql = "SELECT user_name FROM t_user WHERE user_id = ?";
        String args[] = new String[]{userId};
        String username = "匿名";
        Cursor cursor = db.rawQuery(sql, args);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            username = cursor.getString(0);
        }
        L.d("得到的用户的姓名", username);
        return username;
    }

    private Bitmap getUsericon(String userId) {
        String sql = "SELECT user_image FROM t_user WHERE user_id = ?";
        String args[] = new String[]{userId};
        String usericon = "";
        Cursor cursor = db.rawQuery(sql, args);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            usericon = cursor.getString(0);
        }
        L.d("得到的用户的头像", usericon);
        return stringToBitmap(usericon);
    }


    private Bitmap stringToBitmap(String stringUrl) {
        byte[] encode = stringUrl.getBytes();
        byte[] bytes = Base64.decode(encode, 0, encode.length, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
        return bitmap;
    }


    static class ViewHolder {
        ImageView evaluateusericon;
        TextView evaluateuser;
        TextView evaluatetime;
        TextView evaluatedetail;
        TextView evaluategoodtimes;
    }
}
