package com.bluebead38.opencvtesseractocr;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.Serializable;

public class FloatingViewService extends Service implements View.OnClickListener,Serializable {


    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private Button btnCapture;
    private Button btnClose;

    public static String INTENT_CATEGORY_CAPTURE = "capture";

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);


        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);
        btnCapture = mFloatingView.findViewById(R.id.btn_capture);
        btnClose = mFloatingView.findViewById(R.id.btn_close);

        //adding click listener to close button and expanded view
        btnCapture.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        //collapsedView.setOnClickListener(this);
        expandedView.setOnClickListener(this);

        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
//                        collapsedView.setVisibility(View.GONE);
//                        expandedView.setVisibility(View.VISIBLE);

                    case MotionEvent.ACTION_BUTTON_RELEASE:
                        ///startCapture();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                //switching views
//                collapsedView.setVisibility(View.GONE);
//                btnClose.setVisibility(View.GONE);
//                btnCapture.setVisibility(View.GONE);

                startCapture();

                break;

            case R.id.btn_close:
                //closing the widget
                stopSelf();
                break;
        }
    }

//    @Override
//    public int onStartCommand (Intent intent, int flags, int startId) {
//        Bundle bundle = intent.getExtras();
//        CustomData data = (CustomData) bundle.getSerializable("value");
//        if(data != null) {
//            sMediaProjection = data.getsMediaProjection();
//        }
//
//        return Service.START_STICKY_COMPATIBILITY;
//    }

    void startCapture()
    {
        Intent dialogIntent = new Intent(this, ScreenCaptureImageActivity.class);
        dialogIntent.addCategory(INTENT_CATEGORY_CAPTURE);
        startActivity(dialogIntent);

    }
}
