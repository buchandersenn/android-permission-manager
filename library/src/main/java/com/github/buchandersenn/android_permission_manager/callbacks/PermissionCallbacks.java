package com.github.buchandersenn.android_permission_manager.callbacks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

public class PermissionCallbacks {

    public static  OnPermissionGrantedCallback startActivityFromIntent(final Context context, final Intent activityIntent) {
        return new OnPermissionGrantedCallback() {
            @Override
            public void onPermissionGranted() {
                context.startActivity(activityIntent);
            }
        };
    }

    public static OnPermissionDeniedCallback showPermissionDeniedSnackbar(@NonNull final View view, final CharSequence text, final CharSequence buttonText) {
        return new OnPermissionDeniedCallback() {
            @Override
            public void onPermissionDenied() {
                Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
                        .setAction(buttonText, new ShowSettingsButtonClickListener())
                        .show();

            }
        };
    }

    public static OnPermissionDeniedCallback showPermissionDeniedSnackbar(@NonNull final View view, @StringRes final int textResId, @StringRes final int buttonTextResId) {
        return new OnPermissionDeniedCallback() {
            @Override
            public void onPermissionDenied() {
                Snackbar.make(view, textResId, Snackbar.LENGTH_INDEFINITE)
                        .setAction(buttonTextResId, new ShowSettingsButtonClickListener())
                        .show();

            }
        };
    }

    public static OnPermissionShowRationaleCallback showPermissionShowRationaleSnackbar(@NonNull final PermissionManager permissionManager, @NonNull final View view, final CharSequence text, final CharSequence buttonText) {
        return new OnPermissionShowRationaleCallback() {
            @Override
            public void onPermissionShowRationale(final int requestCode, final String[] permissions) {
                Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
                        .setAction(buttonText, new ShowRationaleButtonClickListener(permissionManager, requestCode, permissions))
                        .show();
            }
        };
    }

    public static OnPermissionShowRationaleCallback showPermissionShowRationaleSnackbar(@NonNull final PermissionManager permissionManager, @NonNull final View view, @StringRes final int textResId, @StringRes final int buttonTextResId) {
        return new OnPermissionShowRationaleCallback() {
            @Override
            public void onPermissionShowRationale(final int requestCode, final String[] permissions) {
                Snackbar.make(view, textResId, Snackbar.LENGTH_INDEFINITE)
                        .setAction(buttonTextResId, new ShowRationaleButtonClickListener(permissionManager, requestCode, permissions))
                        .show();
            }
        };
    }

    private static class ShowSettingsButtonClickListener implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
    }

    private static class ShowRationaleButtonClickListener implements View.OnClickListener {
        private final PermissionManager permissionManager;
        private final int requestCode;
        private final String[] permissions;

        public ShowRationaleButtonClickListener(PermissionManager permissionManager, int requestCode, String[] permissions) {
            this.permissionManager = permissionManager;
            this.requestCode = requestCode;
            this.permissions = permissions;
        }

        @Override
        public void onClick(View v) {
            permissionManager.requestPermission(requestCode, permissions);
        }
    }
}
