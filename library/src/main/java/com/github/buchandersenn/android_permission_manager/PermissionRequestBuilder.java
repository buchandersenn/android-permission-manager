package com.github.buchandersenn.android_permission_manager;

import android.support.annotation.NonNull;

import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionDeniedCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionGrantedCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionShowRationaleCallback;
import com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbackDelegate;

public class PermissionRequestBuilder {
    private final @NonNull PermissionManager manager;
    private final @NonNull String[] permissions;
    private int requestCode;

    private OnPermissionGrantedCallback grantedCallback;
    private OnPermissionDeniedCallback deniedCallback;
    private OnPermissionShowRationaleCallback showRationaleCallback;

    PermissionRequestBuilder(@NonNull PermissionManager manager, @NonNull String[] permissions) {
        this.manager = manager;
        this.permissions = permissions;
    }

    public PermissionRequestBuilder usingRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public PermissionRequestBuilder onCallback(OnPermissionCallback callback) {
        this.grantedCallback = callback;
        this.deniedCallback = callback;
        this.showRationaleCallback = callback;
        return this;
    }

    public PermissionRequestBuilder onPermissionGranted(OnPermissionGrantedCallback callback) {
        this.grantedCallback = callback;
        return this;
    }

    public PermissionRequestBuilder onPermissionDenied(OnPermissionDeniedCallback callback) {
        this.deniedCallback = callback;
        return this;
    }

    public PermissionRequestBuilder onPermissionShowRationale(OnPermissionShowRationaleCallback callback) {
        this.showRationaleCallback = callback;
        return this;
    }

    public void execute() {
        PermissionCallbackDelegate callback = new PermissionCallbackDelegate(grantedCallback, deniedCallback, showRationaleCallback);
        manager.execute(requestCode, permissions, callback);
    }

    public void check() {
        PermissionCallbackDelegate callback = new PermissionCallbackDelegate(grantedCallback, deniedCallback, showRationaleCallback);
        manager.check(permissions, callback);
    }
}