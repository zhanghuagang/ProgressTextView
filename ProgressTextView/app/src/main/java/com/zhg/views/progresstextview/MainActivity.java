package com.zhg.views.progresstextview;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ProgressTextView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (ProgressTextView) findViewById(R.id.id_changeTextColorView);

    }

    @SuppressLint("NewApi")
    public void startLeftChange(View view)
    {
        mView.setDirection(0);
        ObjectAnimator.ofFloat(mView, "range", 0, 1).setDuration(2000).start();
    }

    @SuppressLint("NewApi")
    public void startRightChange(View view)
    {
        mView.setDirection(1);
        ObjectAnimator.ofFloat(mView, "range", 0, 1).setDuration(2000).start();
    }
}
