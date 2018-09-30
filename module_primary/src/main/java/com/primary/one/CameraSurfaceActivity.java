package com.primary.one;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import java.io.IOException;

/**
 * <p>Project: AVDemo2</p>
 * <p>Description: CameraSurfaceActivity</p>
 * <p>Copyright (c) 2018 www.duowan.com Inc. All rights reserved.</p>
 * <p>Company: YY Inc.</p>
 *
 * @author: Aragon.Wu
 * @date: 2018-09-30
 * @vserion: 1.0
 */
public class CameraSurfaceActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private TextureView mTextureView;
    private Camera      camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_surface);
        mSurfaceView = findViewById(R.id.surface_view);
//        mTextureView = findViewById(R.id.texture_view);

        surfacePreview();

    }

    private void surfacePreview() {
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    // 打开摄像头并将展示方向旋转90度
                    camera = Camera.open();
                    camera.setDisplayOrientation(90);

                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.release();
            }
        });
    }

    private void texturePreview() {
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    // 打开摄像头并将展示方向旋转90度
                    camera = Camera.open();
                    camera.setDisplayOrientation(90);

                    camera.setPreviewTexture(surface);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                camera.release();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, CameraSurfaceActivity.class));
    }

}
