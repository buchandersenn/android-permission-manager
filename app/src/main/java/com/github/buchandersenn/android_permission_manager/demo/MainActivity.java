package com.github.buchandersenn.android_permission_manager.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionDeniedCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionGrantedCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionShowRationaleCallback;
import com.github.buchandersenn.android_permission_manager.demo.camera.CameraPreviewActivity;

import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.showPermissionDeniedSnackbar;
import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.showPermissionShowRationaleSnackbar;
import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.startActivityFromIntent;

/**
 *
 * TODO : Update description
 *
 * Launcher Activity that demonstrates the use of runtime permissions for Android M.
 * This Activity requests permissions to access the camera
 * ({@link android.Manifest.permission#CAMERA})
 * when the 'Show Camera Preview' button is clicked to start  {@link CameraPreviewActivity} once
 * the permission has been granted.
 * <p>
 * First, the status of the Camera permission is checked using {@link
 * ActivityCompat#checkSelfPermission(Context, String)}
 * If it has not been granted ({@link PackageManager#PERMISSION_GRANTED}), it is requested by
 * calling
 * {@link ActivityCompat#requestPermissions(Activity, String[], int)}. The result of the request is
 * returned to the
 * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}, which starts
 * {@link
 * CameraPreviewActivity} if the permission has been granted.
 * <p>
 * Note that there is no need to check the API level, the support library
 * already takes care of this. Similar helper methods for permissions are also available in
 * ({@link ActivityCompat},
 * {@link android.support.v4.content.ContextCompat} and {@link android.support.v4.app.Fragment}).
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private View mLayout;

    private final PermissionManager permissionManager = PermissionManager.create(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.main_layout);

        // Register a listener for the 'Show Camera Preview' button.
        Button b = (Button) findViewById(com.github.buchandersenn.android_permission_manager.demo.R.id.button_open_camera);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraPreview();
            }
        });
    }

    private void showCameraPreview() {
        permissionManager.with(Manifest.permission.CAMERA)
                .usingRequestCode(PERMISSION_REQUEST_CAMERA)
                .onCallback(new CameraPermissionCallback())
                .execute();

        permissionManager.with(Manifest.permission.CAMERA)
                .onPermissionGranted(new OnPermissionGrantedCallback() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(MainActivity.this, CameraPreviewActivity.class);
                        startActivity(intent);
                    }
                })
                .onPermissionDenied(new OnPermissionDeniedCallback() {
                    @Override
                    public void onPermissionDenied() {
                        Snackbar.make(mLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .onPermissionShowRationale(new OnPermissionShowRationaleCallback() {
                    @Override
                    public void onPermissionShowRationale(final int requestCode, final String[] permissions) {
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Request the permission if the user agree with the rationale...
                                permissionManager.requestPermission(requestCode, permissions);
                            }
                        };

                        Snackbar.make(mLayout, "Camera access is required to display the camera preview.", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", clickListener)
                                .show();
                    }
                })
                .execute();

        permissionManager.with(Manifest.permission.CAMERA)
                .onPermissionGranted(startActivityFromIntent(this, new Intent(MainActivity.this, CameraPreviewActivity.class)))
                .onPermissionDenied(showPermissionDeniedSnackbar(mLayout, "Camera permission request was denied.", "SETTINGS"))
                .onPermissionShowRationale(showPermissionShowRationaleSnackbar(permissionManager, mLayout, "Camera access is required to display the camera preview.", "OK"))
                .execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //boolean handled = permissionManager.handlePermissionResult(requestCode, permissions, grantResults);
        //if (!handled) {
        //    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //}

        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

    private class CameraPermissionCallback implements OnPermissionCallback {
        @Override
        public void onPermissionGranted() {
            Intent intent = new Intent(MainActivity.this, CameraPreviewActivity.class);
            startActivity(intent);
        }

        @Override
        public void onPermissionDenied() {
            Snackbar.make(mLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionShowRationale(final int requestCode, final String[] permissions) {
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Request the permission if the user agree with the rationale...
                    permissionManager.requestPermission(requestCode, permissions);
                }
            };

            Snackbar.make(mLayout, "Camera access is required to display the camera preview.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", clickListener)
                    .show();
        }
    }
}
