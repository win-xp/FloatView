package com.xp.floatview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.xp.floatview.view.FloatView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import static android.webkit.WebSettings.PluginState.ON;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mShowBtn, mCloseBtn;
    private WindowManager mWindowMgr;
    private WindowManager.LayoutParams wmParams;
    private FloatView mFloatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWindowMgr = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        initView();

    }

    private void initView() {
        mShowBtn = (Button) findViewById(R.id.show_btn);
        mCloseBtn = (Button) findViewById(R.id.close_btn);

        mShowBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_btn:
                checkPemission();
                break;
            case R.id.close_btn:
                if (null != mFloatView) {
                    mWindowMgr.removeView(mFloatView);
                    mFloatView = null;
                }
                break;
            default:
                // 销毁悬浮窗
                if (null != mFloatView) {
                    mWindowMgr.removeView(mFloatView);
                    mFloatView = null;
                }
                Intent intent = new Intent("com.xp.floatview.mainpage");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    private void checkPemission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                //有悬浮窗权限开启
                showFloatingWindow();
                onBackPressed();
            } else {
                //没有悬浮窗权限,去开启悬浮窗权限
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivityForResult(intent, 0x01);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            showFloatingWindow();
            onBackPressed();
        }
    }

    public void showFloatingWindow() {
        mFloatView = new FloatView(this);
        mFloatView.setOnClickListener(this);
        int screenWidth = mWindowMgr.getDefaultDisplay().getWidth();
        int screenHeight = mWindowMgr.getDefaultDisplay().getHeight();
        wmParams = new WindowManager.LayoutParams();
        String phoneModel = android.os.Build.MODEL.toLowerCase();
        if (phoneModel.startsWith("mi") || phoneModel.startsWith("mx")
                || phoneModel.startsWith("m1")) {
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // keep 24px to edge
        wmParams.x = 24;
        wmParams.y = screenHeight / 2;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatView.setWmParams(wmParams);
        mWindowMgr.addView(mFloatView, wmParams);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 0x01) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();
                //有悬浮窗权限开启
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFloatingWindow();
                        onBackPressed();
                    }
                }, 2000);
            }
        }
    }
}
