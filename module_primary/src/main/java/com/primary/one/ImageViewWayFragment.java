package com.primary.one;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * <p>Project: AVDemo2</p>
 * <p>Description: ImageViewWayFragment</p>
 * <p>Copyright (c) 2018 www.duowan.com Inc. All rights reserved.</p>
 * <p>Company: YY Inc.</p>
 *
 * @author: Aragon.Wu
 * @date: 2018-09-28
 * @vserion: 1.0
 */
public class ImageViewWayFragment extends Fragment {

    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView = new ImageView(getActivity());
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ameri);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    @Override
    public void onDestroyView() {
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        super.onDestroyView();
    }
}
