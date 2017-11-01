package com.example.gheggie.virsux;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.wowza.gocoder.sdk.api.devices.WZCamera;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.geometry.WZSize;

class FocusListener extends GestureDetector.SimpleOnGestureListener {

    private Context mContext = null;
    private WZCameraView mCameraView = null;

    private FocusListener(Context context) {
        super();
        mContext = context;
    }

    FocusListener(Context context, WZCameraView cameraView) {
        this(context);
        mCameraView = cameraView;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    // Focus camera on double tap
    @Override
    public boolean onDoubleTap(MotionEvent event) {
        if (mCameraView != null) {
            WZCamera userCamera = mCameraView.getCamera();

            if (userCamera != null && userCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS)) {
                if (userCamera.getFocusMode() != WZCamera.FOCUS_MODE_CONTINUOUS) {
                    userCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);
                } else {
                    userCamera.setFocusMode(WZCamera.FOCUS_MODE_OFF);
                }
            }
        }

        return true;
    }

    // focus on single tap at location of tap
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        if (mCameraView != null) {
            WZCamera userCamera = mCameraView.getCamera();

            if (userCamera != null && userCamera.hasCapability(WZCamera.FOCUS_MODE_AUTO)) {

                DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
                WZSize screenSize = mCameraView.getScreenSize();
                WZSize frameSize = mCameraView.getFrameSize();

                int screenLeft = Math.round((float) (displayMetrics.widthPixels - screenSize.width) / 2f);
                int screenTop = Math.round((float) (displayMetrics.heightPixels - screenSize.height) / 2f);

                float screenX = event.getX() - screenLeft;
                float screenY = event.getY() - screenTop;

                if (screenX < 0 || screenX > screenSize.width ||
                        screenY < 0 || screenY > screenSize.getHeight()) {
                    return true;
                }

                float trueX = (screenX / (float)screenSize.width) * (float)frameSize.getWidth();
                float trueY = (screenY / (float)screenSize.height) * (float)frameSize.getHeight();

                userCamera.setFocusPoint(trueX, trueY, 25);
            }
        }

        return true;
    }
}
