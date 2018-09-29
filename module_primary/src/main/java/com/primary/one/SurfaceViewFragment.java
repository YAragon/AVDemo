package com.primary.one;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>Project: AVDemo2</p>
 * <p>Description: SurfaceViewFragment</p>
 * <p>Copyright (c) 2018 www.duowan.com Inc. All rights reserved.</p>
 * <p>Company: YY Inc.</p>
 *
 * @author: Aragon.Wu
 * @date: 2018-09-29
 * @vserion: 1.0
 */
public class SurfaceViewFragment extends Fragment {

    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SurfaceView surfaceView = new SurfaceView(getActivity());
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                if (surfaceHolder == null) {
                    return;
                }

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);

                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.surfaceview_img);

                Canvas canvas = surfaceHolder.lockCanvas();  // 先锁定当前surfaceView的画布
                canvas.drawBitmap(bitmap, 0, 0, paint); //执行绘制操作
                surfaceHolder.unlockCanvasAndPost(canvas); // 解除锁定并显示在界面上
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (null != bitmap && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        });

        return surfaceView;

    }

}
