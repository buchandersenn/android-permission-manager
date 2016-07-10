package com.github.buchandersenn.android_permission_manager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {
    /**
     * Check if that all given permissions are granted.
     */
    static boolean checkPermissions(Context context, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if at least one of the given permissions should show a permission rationale.
     */
    static boolean shouldShowPermissionRationale(Activity activity, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if at least one of the given permissions should show a permission rationale.
     */
    static boolean shouldShowPermissionRationale(Fragment fragment, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if at least one of the given permissions should show a permission rationale.
     */
    static boolean shouldShowPermissionRationale(android.support.v4.app.Fragment fragment, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    static boolean verifyPermissionResults(@NonNull int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
