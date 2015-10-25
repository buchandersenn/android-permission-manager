package com.github.buchandersenn.android_permission_manager.callbacks;

public class PermissionCallbackDelegate implements OnPermissionCallback {
    private final OnPermissionGrantedCallback grantedCallback;
    private final OnPermissionDeniedCallback deniedCallback;
    private final OnPermissionShowRationaleCallback showRationaleCallback;

    public PermissionCallbackDelegate(OnPermissionGrantedCallback grantedCallback, OnPermissionDeniedCallback deniedCallback, OnPermissionShowRationaleCallback showRationaleCallback) {
        this.grantedCallback = grantedCallback;
        this.deniedCallback = deniedCallback;
        this.showRationaleCallback = showRationaleCallback;
    }

    @Override
    public void onPermissionGranted() {
        if (grantedCallback != null) {
            grantedCallback.onPermissionGranted();
        }
    }

    @Override
    public void onPermissionDenied() {
        if (deniedCallback != null) {
            deniedCallback.onPermissionDenied();
        }
    }

    @Override
    public void onPermissionShowRationale(int requestCode, String[] permissions) {
        if (showRationaleCallback != null) {
            showRationaleCallback.onPermissionShowRationale(requestCode, permissions);
        }
    }
}
