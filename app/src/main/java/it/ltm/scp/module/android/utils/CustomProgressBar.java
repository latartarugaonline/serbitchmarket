package it.ltm.scp.module.android.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import it.ltm.scp.module.android.R;

/**
 * Created by HW64 on 15/09/2016.
 */
public class CustomProgressBar extends ProgressBar {



    private int[] mColors = new int[]{R.color.g_blue, R.color.g_red, R.color.g_yellow, R.color.g_green};
    private int mIndexColor = 0;
    private int mMaxIndex = 4;
    private boolean init = false;
    private Runnable mColoringRunnable = new Runnable() {
        @Override
        public void run() {
            if(isShown()){
                if(mIndexColor == mMaxIndex)
                    mIndexColor = 0;
                getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), mColors[mIndexColor]), PorterDuff.Mode.SRC_IN);
                mIndexColor ++;
                tint();
            }
        }
    };

    public CustomProgressBar(Context context){
        super(context);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility == VISIBLE){
            tint();
        } else {
            stopTint();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopTint();
        super.onDetachedFromWindow();
    }

    private void tint(){
        if(!init){
            getIndeterminateDrawable().setColorFilter(getContext().getColor(mColors[0]), PorterDuff.Mode.SRC_IN);
            mIndexColor ++;
            init = true;
        }
        postDelayed(mColoringRunnable, 1450);//prev 1300
    }

    private void stopTint(){
        removeCallbacks(mColoringRunnable);
    }


}
