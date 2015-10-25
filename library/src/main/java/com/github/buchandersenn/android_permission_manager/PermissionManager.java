package com.github.buchandersenn.android_permission_manager;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;

import com.github.buchandersenn.android_permission_manager.callbacks.OnPermissionCallback;

import java.util.Arrays;

public abstract class PermissionManager {
    private SparseArray<OnPermissionCallback> callbacks = new SparseArray<>();

    public static PermissionManager create(Activity activity) {
        return new ActivityPermissionManager(activity);
    }

    public static PermissionManager create(Fragment fragment) {
        return new FragmentPermissionManager(fragment);
    }

    public PermissionRequestBuilder with(@NonNull String... permissions) {
        if (permissions.length < 1) {
            throw new IllegalArgumentException("PermissionManager.with(String... permissions) must be called with at least one permission");
        }

        return new PermissionRequestBuilder(this, permissions);
    }

    public boolean handlePermissionResult(int requestCode, @NonNull int[] grantResults) {
        OnPermissionCallback callback = callbacks.get(requestCode);
        if (callback == null) {
            return false;
        }

        callbacks.delete(requestCode);

        if (PermissionUtil.verifyPermissionResults(grantResults)) {
            callback.onPermissionGranted();
        } else {
            callback.onPermissionDenied();
        }

        return true;
    }

    public abstract void requestPermission(int requestCode, String[] permissions);

    protected void execute(int requestCode, String[] permissions, OnPermissionCallback callback) {
        if (checkPermissions(permissions)) {
            callback.onPermissionGranted();
            return;
        }

        // If no request code was supplied by the PermissionRequestBuilder then
        // calculate one...
        if (requestCode == 0) {
            requestCode = Arrays.hashCode(permissions);
        }

        // Register the callback with the PermissionManager before either showing the rationale,
        // and hopefully requesting the permission once the user agrees with the rationale,
        // or just requesting the permission at once. The callback is then used by
        // PermissionManager.handlePermissionResult() once the user replies to the request...
        callbacks.put(requestCode, callback);

        if (shouldShowPermissionRationale(permissions)) {
            callback.onPermissionShowRationale(requestCode, permissions);
        } else {
            requestPermission(requestCode, permissions);
        }
    }

    protected void check(String[] permissions, OnPermissionCallback callback) {
        if (checkPermissions(permissions)) {
            callback.onPermissionGranted();
        } else {
            callback.onPermissionDenied();
        }
    }

    protected abstract boolean checkPermissions(String[] permissions);
    protected abstract boolean shouldShowPermissionRationale(String[] permissions);

    private static class ActivityPermissionManager extends PermissionManager {
        private final @NonNull Activity activity;

        public ActivityPermissionManager(@NonNull Activity activity) {
            this.activity = activity;
        }

        @Override
        public void requestPermission(int requestCode, String[] permissions) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }

        @Override
        protected boolean checkPermissions(String[] permissions) {
            return PermissionUtil.checkPermissions(activity, permissions);
        }

        @Override
        protected boolean shouldShowPermissionRationale(String[] permissions) {
            return PermissionUtil.shouldShowPermissionRationale(activity, permissions);
        }
    }

    private static class FragmentPermissionManager extends PermissionManager {
        private final @NonNull Fragment fragment;

        public FragmentPermissionManager(@NonNull Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void requestPermission(int requestCode, String[] permissions) {
            FragmentCompat.requestPermissions(fragment, permissions, requestCode);
        }

        @Override
        protected boolean checkPermissions(String[] permissions) {
            return PermissionUtil.checkPermissions(fragment.getActivity(), permissions);
        }

        @Override
        protected boolean shouldShowPermissionRationale(String[] permissions) {
            return PermissionUtil.shouldShowPermissionRationale(fragment, permissions);
        }
    }
}
