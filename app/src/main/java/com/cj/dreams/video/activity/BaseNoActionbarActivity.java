package com.cj.dreams.video.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.cj.dreams.video.ABaseActivity;

public class BaseNoActionbarActivity extends ABaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
