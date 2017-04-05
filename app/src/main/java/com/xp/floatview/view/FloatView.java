package com.xp.floatview.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.xp.floatview.R;

/**
 * Created by xp on 2017/4/5.
 */

public class FloatView extends FrameLayout {

    private Context mContext;
    private WindowManager wm;
    // edge margin 24px
    private static final int EDGE_MARGIN = 24;
    private float xInView;
    private float yInView;
    private float xInScreen;
    private float yInScreen;
    private float mStartX;
    private float mStartY;
    private WindowManager.LayoutParams wmParams;
    private OnClickListener mOnClickListner;

    public FloatView(Context context) {
        this(context, null);
    }

    public FloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFloatView(context);
    }

    private void initFloatView(Context context) {
        this.mContext = context;
        // inflate floatview layout
        LayoutInflater.from(this.mContext).inflate(R.layout.window_floatview, this);
        // request window service
        this.wm = (WindowManager) this.getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);


    }

    /**
     * set click listener
     *
     * @param listener
     */
    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListner = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect frame = new Rect();
        this.getWindowVisibleDisplayFrame(frame);
        int statusBar = frame.top;
        Log.d("FloatView", "statusBar height: " + statusBar);
        int screenWidth = this.wm.getDefaultDisplay().getWidth();
        int screenHeight = this.wm.getDefaultDisplay().getHeight();
        this.xInScreen = event.getRawX();
        this.yInScreen = event.getRawY() - (float) statusBar;
        Log.i("onTouchEvent", "x: " + this.xInScreen + ", y: " + this.yInScreen);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.xInView = event.getX();
                this.yInView = event.getY();
                this.mStartX = this.xInScreen;
                this.mStartY = this.yInScreen;
                Log.i("ACTION_DOWN", "xInView: " + this.xInView + ", mTouchStartY: " + this.yInView);
                break;
            case MotionEvent.ACTION_UP:
                if ((double) Math.abs(this.xInScreen - this.mStartX) < 5.0D && (double) Math.abs(this.yInScreen - this.mStartY) < 5.0D) {
                    if (this.mOnClickListner != null) {
                        this.mOnClickListner.onClick(this);
                        Log.i("FloatView", "click floating window");
                    }
                } else {
                    // 自动回弹吸附
                    if (this.xInScreen < (float) (screenWidth / 2)) {
                        this.xInScreen = 0.0F;
                    } else {
                        this.xInScreen = (float) screenWidth;
                    }
                    updateViewLayout();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                this.xInScreen = event.getRawX();
                this.yInScreen = event.getRawY() - (float) statusBar;
                Log.i("ACTION_MOVE", "xInScreen: " + this.xInScreen + ", yInScreen: " + this.yInScreen + ", xInView: " + this.xInView + ", yInView: " + this.yInView);
                updateViewLayout();
        }
        return true;
    }

    private void updateViewLayout() {
        wmParams.x = (int) (this.xInScreen - this.xInView);
        wmParams.y = (int) (this.yInScreen - this.yInView);
        wm.updateViewLayout(this, this.wmParams);
        setLayoutTransition(new LayoutTransition());
    }

    public void setWmParams(WindowManager.LayoutParams wmParams) {
        this.wmParams = wmParams;
    }
}
