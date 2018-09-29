package com.high.bigger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.primary.one.AudioRecordTrackActivity;
import com.primary.one.ThreeWaysToShowImageActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.primary_one).setOnClickListener(this);
        findViewById(R.id.primary_two).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.primary_one:
                ThreeWaysToShowImageActivity.launch(this);
                break;

            case R.id.primary_two:
                AudioRecordTrackActivity.launch(this);
                break;

        }
    }
}
