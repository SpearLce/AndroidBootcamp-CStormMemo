package com.illidancstormrage.csrich.utils.htmlconvert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.illidancstormrage.csrich.R;


/**
 *
 */
public class CSDrawable extends BitmapDrawable {
    protected Drawable defaultDrawable;

    private Drawable mDrawable;
    protected int w;
    protected int h;

    private final Context mContext;


    @SuppressLint("UseCompatLoadingForDrawables")
    @SuppressWarnings("deprecation")
    public CSDrawable(Context context) {

        //super(context.getResources(), String.valueOf(R.drawable.icon_text_picture));

        this.mContext = context;
        //获取默认Drawable - icon_text_picture
        defaultDrawable = context.getResources().getDrawable(R.drawable.icon_text_picture);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.icon_text_picture, options);
        this.w = options.outWidth;
        this.h = options.outHeight;
        defaultDrawable.setBounds(0, 0, w, h);

        Rect rect = new Rect(0, 0, w, h);
        this.setBounds(rect);
    }

    @Override
    public void draw(Canvas canvas) {
        Drawable drawable = mDrawable == null ? defaultDrawable : mDrawable;
        boolean isLoading = (mDrawable == null);
        if (drawable != null) {
            drawable.draw(canvas);
            //在正在加载的时候 - 显示图片前显示Loading...字符
            loading(canvas, isLoading);
        }
    }


    public void loading(Canvas canvas, Boolean isLoading) {
        if (isLoading) {
            Paint p = new Paint();
            //p.setColor(Color.WHITE);
            p.setColor(Color.RED);
            p.setTextSize(30);
            String loading = "Loading... 0%";
            Rect bounds = new Rect();
            p.getTextBounds(loading, 0, loading.length(), bounds);

            float loadingWidth = bounds.width();
            float x = 0.0f;
            if (loadingWidth < this.w) {
                x = (this.w - loadingWidth) / 2;
            }

            float loadingHeight = bounds.height();
            float y = 0.0f;
            if (loadingHeight < this.h) {
                y = (float) this.h / 2;
            }
            canvas.drawText(loading, 0, loading.length(), x, y, p);
        }
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        //this.invalidateSelf();
    }
}
