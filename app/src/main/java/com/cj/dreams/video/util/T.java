package com.cj.dreams.video.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.dreams.video.R;

/**
 * Created by fanyafeng on 2015/7/2.
 */
public class T {

    private T() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    private static int GRAVITY = Gravity.CENTER;

    public static void showLong(Context context, String message) {
        if (isShow) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public static void showShort(Context context, String message) {
        if (isShow) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showLong(Context context, int textId) {
        if (isShow) {
            Toast.makeText(context, textId, Toast.LENGTH_LONG).show();
        }
    }

    public static void showShort(Context context, int textId) {
        if (isShow) {
            Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
        }
    }

    public static void show(Context context, String text, int duration) {
        if (isShow) {
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(GRAVITY, 80, 80);
            toast.show();
        }
    }

    public static void show(Context context, int textId, int duration) {
        if (isShow) {
            Toast toast = Toast.makeText(context, textId, duration);
            toast.setGravity(GRAVITY, 80, 80);
            toast.show();
        }
    }

    public static void showSuccess(Context context, int textId) {
        if (isShow) {
            showIconToast(context, textId, R.drawable.success, R.color.blue);
        }
    }

    public static void showFailure(Context context, int textId) {
        if (isShow) {
            showIconToast(context, textId, R.drawable.failure, R.color.red);
        }
    }

    public static void showIconToast(Context context, int textId, int iconId, int colorId) {
        if (isShow) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.toast, null);
            ((TextView) layout).setText(textId);
            ((TextView) layout).setTextColor(context.getResources().getColor(colorId));
            ((TextView) layout).setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }

}
