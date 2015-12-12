package com.github.buchandersenn.android_permission_manager;

import android.support.annotation.NonNull;

import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionDeniedCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionGrantedCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionShowRationaleCallback;

public class PermissionRequest {
    private final @NonNull PermissionManager permissionManager;

    private final @NonNull String[] permissions;
    private final int requestCode;

    protected final OnPermissionGrantedCallback grantedCallback;
    protected final OnPermissionDeniedCallback deniedCallback;
    protected final OnPermissionShowRationaleCallback showRationaleCallback;

    public PermissionRequest(@NonNull PermissionManager permissionManager, @NonNull String[] permissions, int requestCode, OnPermissionGrantedCallback grantedCallback, OnPermissionDeniedCallback deniedCallback, OnPermissionShowRationaleCallback showRationaleCallback) {
        this.permissionManager = permissionManager;
        this.permissions = permissions;
        this.requestCode = requestCode;
        this.grantedCallback = grantedCallback;
        this.deniedCallback = deniedCallback;
        this.showRationaleCallback = showRationaleCallback;
    }

    public void acceptPermissionRationale() {
        permissionManager.requestPermission(this);
    }

    @NonNull
    protected String[] getPermissions() {
        return permissions;
    }

    protected int getRequestCode() {
        return requestCode;
    }

    protected void fireOnPermissionGrantedCallback() {
        if (grantedCallback != null) {
            grantedCallback.onPermissionGranted();
        }
    }

    protected void fireOnPermissionDeniedCallback() {
        if (deniedCallback != null) {
            deniedCallback.onPermissionDenied();
        }
    }

    protected void fireOnPermissionShowRationaleCallback() {
        if (showRationaleCallback != null) {
            showRationaleCallback.onPermissionShowRationale(this);
        }
    }
}
