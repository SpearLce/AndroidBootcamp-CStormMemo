package com.illidancstormrage.csrich.utils.htmlconvert;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.illidancstormrage.csrich.utils.ScreenUtil;
import com.illidancstormrage.utils.log.LogUtil;

import java.util.Objects;

/**
 * <a href="https://github.com/chinalwb/Android-Rich-text-Editor/blob/master/ARE/are/src/main/java/com/chinalwb/are/android/inner/Html.java#L98">参考Html类</a>
 */
public class CSImageGetter implements CSHtml.ImageGetter {

    private final Context mContext;

    private final TextView mTextView;
    private String currentUri;
    private static final String TAG = "CSImageGetter";

    //2
    public CSImageGetter(Context context, TextView textView) {
        mContext = context;
        mTextView = textView;
    }

    @Override
    public Drawable getDrawable(String source) {
        // content://media/external/images/media/846589
        // 创建一个异步任务来加载图片，防止阻塞 UI 线程
        CSDrawable drawable = new CSDrawable(mContext);

        ImageTarget imageTarget = new ImageTarget(drawable);

        currentUri = source;

        try {
            Uri uri = Uri.parse(source);

            //异步加载
            Glide.with(mContext)
                    .asBitmap()
                    .load(uri)
                    //.fitCenter()
                    .into(imageTarget);

            return drawable;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private class ImageTarget extends CustomTarget<Bitmap> {
        private final CSDrawable areUrlDrawable;

        private static final String TAG = "ImageTarget";

        private ImageTarget(CSDrawable urlDrawable) {
            this.areUrlDrawable = urlDrawable;
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            int sWidth =
                    Objects.requireNonNull(ScreenUtil.getScreenWidthAndHeight(mContext))[0]
                            - mTextView.getPaddingLeft() - mTextView.getPaddingRight();
            resource = ScreenUtil.scaleBitmapToFitWidth(resource, sWidth);
            assert resource != null;
            int bw = resource.getWidth();
            int bh = resource.getHeight();
            Rect rect = new Rect(0, 0, bw, bh);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
            bitmapDrawable.setBounds(rect);
            areUrlDrawable.setBounds(rect);
            areUrlDrawable.setDrawable(bitmapDrawable);

            areUrlDrawable.invalidateSelf();
            mTextView.post(new Runnable() {
                @Override
                public void run() {
                    areUrlDrawable.invalidateSelf();
                    mTextView.invalidate();
                }
            });

            mTextView.invalidate();

        }

        //textView.setText(textView.getText()); //刷新textView区
        //textView.invalidate();

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
            // 清除回调，一般不需要额外处理
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            // super.onLoadFailed(errorDrawable);
            // 图片加载失败，设置一个占位符 Drawable 或 null
            LogUtil.e(TAG, "CSImageGetter - onLoadFailed:图片加载失败");
        }
    }
}

