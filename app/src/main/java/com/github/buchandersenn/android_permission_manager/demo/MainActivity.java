package com.github.buchandersenn.android_permission_manager.demo;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.PermissionRequest;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionCallback;
import com.github.buchandersenn.android_permission_manager.demo.camera.CameraPreviewActivity;
import com.github.buchandersenn.android_permission_manager.demo.contacts.ContactRequestFragment;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private View mLayout;

    private final PermissionManager permissionManager = PermissionManager.create(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.main_layout);

        // Register a listener for the 'Show Camera Preview' button...
        Button b = (Button) findViewById(com.github.buchandersenn.android_permission_manager.demo.R.id.button_open_camera);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraPreview();
            }
        });

        // Setup the contact fragment...
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new ContactRequestFragment());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_reset) {
            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

    private void showCameraPreview() {
        // Handling the callbacks using the helper methods in the library...
        // permissionManager.with(Manifest.permission.CAMERA)
        //         .onPermissionGranted(startPermissionGrantedActivity(this, new Intent(this, CameraPreviewActivity.class)))
        //         .onPermissionDenied(showPermissionDeniedSnackbar(mLayout, "Camera permission request was denied.", "SETTINGS"))
        //         .onPermissionShowRationale(showPermissionShowRationaleSnackbar(mLayout, "Camera access is required to display the camera preview.", "OK"))
        //         .request();

        // Handling all three callbacks using a single custom handler...
        permissionManager.with(Manifest.permission.CAMERA)
                .usingRequestCode(PERMISSION_REQUEST_CAMERA)
                .onCallback(new CameraPermissionCallback())
                .request();
    }

    private class CameraPermissionCallback implements OnPermissionCallback {
        @Override
        public void onPermissionGranted() {
            Intent intent = new Intent(MainActivity.this, CameraPreviewActivity.class);
            startActivity(intent);
        }

        @Override
        public void onPermissionDenied(boolean neverAskAgain) {
            Snackbar.make(mLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionShowRationale(final PermissionRequest permissionRequest) {
            View.OnClickListener okClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    permissionRequest.acceptPermissionRationale();
                }
            };

            Snackbar.make(mLayout, "Camera access is required to display the camera preview.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", okClickListener)
                    .show();
        }
    }
}
