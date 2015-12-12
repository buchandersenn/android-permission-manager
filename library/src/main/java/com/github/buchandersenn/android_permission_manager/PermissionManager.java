package com.github.buchandersenn.android_permission_manager;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;

public abstract class PermissionManager {
    private static final int MAX_REQUEST_CODE = 255;

    private final Object requestCodeLock = new Object();
    private final SparseArray<PermissionRequest> requests = new SparseArray<>();

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
        PermissionRequest request = requests.get(requestCode);
        unregisterCallbacks(requestCode);

        // If no request callbacks could be found then return false...
        if (request == null) {
            return false;
        }

        // Else execute the appropriate callback...
        if (PermissionUtil.verifyPermissionResults(grantResults)) {
            request.fireOnPermissionGrantedCallback();
        } else {
            request.fireOnPermissionDeniedCallback();
        }

        return true;
    }

    protected void check(PermissionRequest permissionRequest) {
        if (checkPermissions(permissionRequest.getPermissions())) {
            permissionRequest.fireOnPermissionGrantedCallback();
        } else {
            permissionRequest.fireOnPermissionDeniedCallback();
        }
    }

    protected void request(PermissionRequest permissionRequest) {
        if (checkPermissions(permissionRequest.getPermissions())) {
            permissionRequest.fireOnPermissionGrantedCallback();
            return;
        }

        if (shouldShowPermissionRationale(permissionRequest.getPermissions())) {
            permissionRequest.fireOnPermissionShowRationaleCallback();
        } else {
            requestPermission(permissionRequest);
        }
    }

    protected void requestPermission(PermissionRequest permissionRequest) {
        int requestCode = registerCallbacks(permissionRequest);
        requestPermission(requestCode, permissionRequest.getPermissions());
    }

    protected abstract void requestPermission(int requestCode, String[] permissions);
    protected abstract boolean checkPermissions(String[] permissions);
    protected abstract boolean shouldShowPermissionRationale(String[] permissions);

    private void unregisterCallbacks(int requestCode) {
        requests.delete(requestCode);
    }

    private int registerCallbacks(PermissionRequest permissionRequest) {
        // Register the request with the PermissionManager before requesting the permission(s).
        // The requests map is used by PermissionManager.handlePermissionResult() to act
        // on the permission result, once the user replies to the request.
        // If the activity/fragment is destroyed before the result arrives
        // then the requests map is lost and the library will instead try to
        // restore the callbacks from one of the static callback maps...
        int requestCode;
        synchronized (requestCodeLock) {
            // If no request code was supplied by the PermissionRequestBuilder then
            // calculate one...
            int userSuppliedRequestCode = permissionRequest.getRequestCode();
            if (userSuppliedRequestCode == -1) {
                requestCode = calculateRequestCode();
                requests.put(requestCode, permissionRequest);
            } else if (requests.get(userSuppliedRequestCode) == null) {
                requestCode = userSuppliedRequestCode;
                requests.put(requestCode, permissionRequest);
            } else {
                throw new IllegalStateException("The requestCode " + userSuppliedRequestCode + " is already in use");
            }
        }

        return requestCode;
    }

    /**
     * The requestCode must be between 0 and 255. This method calculates a new request code by
     * the simple method of looping through all the possible codes and returning the first one
     * that is not in use.
     * @return an unused request code
     */
    private int calculateRequestCode() {
        for (int i = 0; i < MAX_REQUEST_CODE; i++) {
            if (requests.get(i) == null) {
                return i;
            }
        }

        throw new IllegalStateException("Unable to calculate request code. Try setting a request code manually by calling PermissionRequestBuilder#usingRequestCode(int)");
    }

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
