package com.primary.one;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ThreeWaysToShowImageActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_ways_to_show_image);
        findViewById(R.id.iv_direct_tv).setOnClickListener(this);
        findViewById(R.id.iv_surfaceview_tv).setOnClickListener(this);
        findViewById(R.id.iv_custom_tv).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_direct_tv) {
            getSupportFragmentManager().beginTransaction().replace(R.id.image_content_fl, new ImageViewWayFragment()).commitAllowingStateLoss();

        } else if (i == R.id.iv_surfaceview_tv) {

        } else if (i == R.id.iv_custom_tv) {

        }
    }

    public static void launch(Context context){
        context.startActivity(new Intent(context, ThreeWaysToShowImageActivity.class));
    }

}
