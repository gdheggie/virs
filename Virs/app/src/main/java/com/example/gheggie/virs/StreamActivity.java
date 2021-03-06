package com.example.gheggie.virs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WZCamera;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

import static com.example.gheggie.virs.VirsUtils.currentPoet;

public class StreamActivity extends AppCompatActivity implements WZStatusCallback
        , View.OnClickListener {

    // The GoCoder SDK camera view
    private WZCameraView goCoderCameraView;

    private WZCamera activeCamera;

    // The broadcast configuration settings
    private WZBroadcastConfig goCoderBroadcastConfig;

    // Properties needed for Android 6+ permissions handling
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;

    // The GoCoder SDK broadcaster
    private WZBroadcast goCoderBroadcaster;

    private boolean mPermissionsGranted = true;

    private Button broadcastButton;

    private String[] mRequiredPermissions = new String[] {
            android.Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private GestureDetectorCompat mAutoFocusDetector = null;

    private TextView liveText;
    private TextView timerText;
    private ImageButton closeButton;
    private int time = -3;
    private Thread t;
    private Stream stream = new Stream();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        // Initialize the GoCoder SDK
        WowzaGoCoder goCoder = WowzaGoCoder.init(getApplicationContext(), "GOSK-5B44-0103-99B1-5193-2922");

        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WZError goCoderInitError = WowzaGoCoder.getLastError();
            Toast.makeText(this,
                    "GoCoder SDK error: " + goCoderInitError.getErrorDescription(),
                    Toast.LENGTH_LONG).show();
        }

        // Create a broadcaster instance
        goCoderBroadcaster = new WZBroadcast();

        // Associate the WZCameraView defined in the U/I layout with the corresponding class member
        goCoderCameraView = (WZCameraView) findViewById(R.id.stream_view);

        // Create an audio device instance for capturing and broadcasting audio
        WZAudioDevice goCoderAudioDevice = new WZAudioDevice();

        // Create a configuration instance for the broadcaster
        goCoderBroadcastConfig = new WZBroadcastConfig(WZMediaConfig.FRAME_SIZE_1280x720);

        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Cloud account
        goCoderBroadcastConfig.setHostAddress("192.168.0.188");
        goCoderBroadcastConfig.setPortNumber(1935);
        goCoderBroadcastConfig.setApplicationName("live");
        goCoderBroadcastConfig.setStreamName("myStream");
        goCoderBroadcastConfig.setUsername("heggie");
        goCoderBroadcastConfig.setPassword("Grandma92");

        String webAddress = goCoderBroadcastConfig.getHostAddress();
        String appName = goCoderBroadcastConfig.getApplicationName();
        String streamName = goCoderBroadcastConfig.getStreamName();

        stream.setUserIcon(currentPoet.getUserIcon());
        stream.setAddress("rtsp://"+webAddress+":1935/"+appName+"/"+streamName);


        // Designate the camera preview as the video source
        goCoderBroadcastConfig.setVideoBroadcaster(goCoderCameraView);

        // Designate the audio device as the audio broadcaster
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice);

        broadcastButton = (Button) findViewById(R.id.broadcast_button);
        broadcastButton.setOnClickListener(this);
        ImageButton switchCam = (ImageButton) findViewById(R.id.switch_view);

        if (goCoderCameraView != null) {
            activeCamera = goCoderCameraView.getCamera();
            activeCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);
        }

        switchCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goCoderCameraView != null) {
                    activeCamera = goCoderCameraView.switchCamera();
                }
            }
        });
        timerText = (TextView)findViewById(R.id.stream_timer);

        closeButton = (ImageButton)findViewById(R.id.close_live);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        liveText = (TextView)findViewById(R.id.live_text);

        hideLive();
    }

    private void timerTextValue(){
        t = new Thread() {
            @Override
            public void run() {
                while(!isInterrupted()) {
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time++;
                                timerText.setText(String.valueOf(time));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        t.start();
    }

    // enable full screen mode
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    // check to see if the necessary permissions
    @Override
    protected void onResume() {
        super.onResume();

        // If running on Android 6 (Marshmallow) or above, check to see if the necessary permissions
        // have been granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionsGranted = hasPermissions(this, mRequiredPermissions);
            if (!mPermissionsGranted) {
                ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            mPermissionsGranted = true;
        }

        // Start the camera preview display
        if (mPermissionsGranted && goCoderCameraView != null) {
            if (goCoderCameraView.isPreviewPaused()) {
                goCoderCameraView.onResume();
            }
            else {
                goCoderCameraView.startPreview();
            }

            if (mAutoFocusDetector == null) {
                mAutoFocusDetector = new GestureDetectorCompat(this, new FocusListener(this, goCoderCameraView));
            }

            if (goCoderCameraView != null) {
                activeCamera = goCoderCameraView.getCamera();
                activeCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);
            }
        }
    }

    private void showLive(){
        broadcastButton.setText(R.string.end);
        liveText.setVisibility(View.VISIBLE);
        closeButton.setVisibility(View.GONE);
        timerText.setVisibility(View.VISIBLE);
        timerTextValue();
    }

    private void hideLive(){
        broadcastButton.setText(R.string.start_stream);
        liveText.setVisibility(View.GONE);
        timerText.setVisibility(View.GONE);
        closeButton.setVisibility(View.VISIBLE);
    }

     // interpret the results of the permissions request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mPermissionsGranted = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // Check the result of each permission granted
                for(int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionsGranted = false;
                    }
                }
            }
        }
    }

    // check the status of a permissions request
    private boolean hasPermissions(StreamActivity streamActivity, String[] permissions) {
        for(String permission : permissions)
            if (streamActivity.checkCallingOrSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        return true;
    }

    // The callback invoked upon changes to the state of the steaming broadcast
    @Override
    public void onWZStatus(WZStatus wzStatus) {
        // A successful status transition has been reported by the GoCoder SDK
        final StringBuffer statusMessage = new StringBuffer("Stream status: ");

        switch (wzStatus.getState()) {
            case WZState.STARTING:
                statusMessage.append("Broadcast initialization");
                break;
            case WZState.RUNNING:
                statusMessage.append("Stream is active");
                break;
            case WZState.IDLE:
                statusMessage.append("Stream has stopped");
                break;
            default:
                return;
        }

        // Display the status message using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StreamActivity.this, statusMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // The callback invoked when an error occurs during a broadcast
    @Override
    public void onWZError(final WZStatus wzStatus) {
        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StreamActivity.this,
                        "Streaming error: " + wzStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // return if the user hasn't granted the app the necessary permissions
        if (!mPermissionsGranted) { return;}

        // Ensure the minimum set of configuration settings have been specified necessary to
        // initiate a broadcast streaming session
        WZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();

        if (configValidationError != null) {
            Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
        } else if (goCoderBroadcaster.getStatus().isRunning()) {
            // Stop the broadcast that is currently running
            hideLive();
            goCoderBroadcaster.endBroadcast(this);
            t.interrupt();
            deleteStream();
        } else {
            showLive();
            goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, this);
            addStream();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mAutoFocusDetector != null)
            mAutoFocusDetector.onTouchEvent(event);

        return super.onTouchEvent(event);

    }

    private void addStream(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Streams").child(currentPoet.getUserId()).setValue(stream);
    }

    private void deleteStream(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Streams").child(currentPoet.getUserId()).removeValue();
    }
}
