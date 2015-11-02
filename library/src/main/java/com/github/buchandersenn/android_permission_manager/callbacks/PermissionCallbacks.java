package com.github.buchandersenn.android_permission_manager.callbacks;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

public class PermissionCallbacks {

    public static OnPermissionGrantedCallback all(final OnPermissionGrantedCallback... grantedCallbacks) {
        return new OnPermissionGrantedCallback() {
            @Override
            public void onPermissionGranted() {
                for (OnPermissionGrantedCallback callback : grantedCallbacks) {
                    callback.onPermissionGranted();
                }
            }
        };
    }

    public static OnPermissionGrantedCallback setPermissionGrantedViewVisibility(@NonNull final View view, final int visibility) {
        return new OnPermissionGrantedCallback() {
            @Override
            public void onPermissionGranted() {
                view.setVisibility(visibility);
            }
        };
    }

    public static OnPermissionGrantedCallback setPermissionGrantedViewEnabled(@NonNull final View view, final boolean enabled) {
        return new OnPermissionGrantedCallback() {
            @Override
            public void onPermissionGranted() {
                view.setEnabled(enabled);
            }
        };
    }

    public static  OnPermissionGrantedCallback startPermissionGrantedActivity(final Context context, final Intent activityIntent) {
        return new OnPermissionGrantedCallback() {
            @Override
            public void onPermissionGranted() {
                context.startActivity(activityIntent);
            }
        };
    }

    public static  OnPermissionGrantedCallback showPermissionGrantedFragment(final FragmentManager fragmentManager, @IdRes final int viewId, final Fragment fragment, final boolean addToBackStack) {
        return new OnPermissionGrantedCallback() {
            @Override
            public void onPermissionGranted() {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(viewId, fragment);
                if (addToBackStack) {
                    fragmentTransaction.addToBackStack(null);
                }
                fragmentTransaction.commit();
            }
        };
    }

    public static OnPermissionDeniedCallback all(final OnPermissionDeniedCallback... deniedCallbacks) {
        return new OnPermissionDeniedCallback() {
            @Override
            public void onPermissionDenied() {
                for (OnPermissionDeniedCallback callback : deniedCallbacks) {
                    callback.onPermissionDenied();
                }
            }
        };
    }

    public static OnPermissionDeniedCallback setPermissionDeniedViewVisibility(@NonNull final View view, final int visibility) {
        return new OnPermissionDeniedCallback() {
            @Override
            public void onPermissionDenied() {
                view.setVisibility(visibility);
            }
        };
    }

    public static OnPermissionDeniedCallback setPermissionDeniedViewEnabled(@NonNull final View view, final boolean enabled) {
        return new OnPermissionDeniedCallback() {
            @Override
            public void onPermissionDenied() {
                view.setEnabled(enabled);
            }
        };
    }

    public static OnPermissionDeniedCallback showPermissionDeniedSnackbar(@NonNull final View view, final CharSequence text, final CharSequence buttonText) {
        return new OnPermissionDeniedCallback() {
            @Override
            public void onPermissionDenied() {
                Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
                        .setAction(buttonText, new SettingsButtonClickListener())
                        .show();
            }
        };
    }

    public static OnPermissionDeniedCallback showPermissionDeniedSnackbar(@NonNull final View view, @StringRes final int textResId, @StringRes final int buttonTextResId) {
        return new OnPermissionDeniedCallback() {
            @Override
            public void onPermissionDenied() {
                Snackbar.make(view, textResId, Snackbar.LENGTH_INDEFINITE)
                        .setAction(buttonTextResId, new SettingsButtonClickListener())
                        .show();
            }
        };
    }

    public static  OnPermissionDeniedCallback showPermissionDeniedFragment(final FragmentManager fragmentManager, @IdRes final int viewId, final Fragment fragment, final boolean addToBackStack) {
        return new OnPermissionDeniedCallback() {
            @Override
            public void onPermissionDenied() {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(viewId, fragment);
                if (addToBackStack) {
                    fragmentTransaction.addToBackStack(null);
                }
                fragmentTransaction.commit();
            }
        };
    }

    public static OnPermissionShowRationaleCallback all(final OnPermissionShowRationaleCallback... showRationaleCallbacks) {
        return new OnPermissionShowRationaleCallback() {
            @Override
            public void onPermissionShowRationale(int requestCode, String[] permissions) {
                for (OnPermissionShowRationaleCallback callback : showRationaleCallbacks) {
                    callback.onPermissionShowRationale(requestCode, permissions);
                }
            }
        };
    }

    public static OnPermissionShowRationaleCallback setPermissionShowRationaleViewVisibility(@NonNull final View view, final int visibility) {
        return new OnPermissionShowRationaleCallback() {
            @Override
            public void onPermissionShowRationale(int requestCode, String[] permissions) {
                view.setVisibility(visibility);
            }
        };
    }

    public static OnPermissionShowRationaleCallback setPermissionShowRationaleViewEnabled(@NonNull final View view, final boolean enabled) {
        return new OnPermissionShowRationaleCallback() {
            @Override
            public void onPermissionShowRationale(int requestCode, String[] permissions) {
                view.setEnabled(enabled);
            }
        };
    }

    public static OnPermissionShowRationaleCallback showPermissionShowRationaleSnackbar(@NonNull final PermissionManager permissionManager, @NonNull final View view, final CharSequence text, final CharSequence buttonText) {
        return new OnPermissionShowRationaleCallback() {
            @Override
            public void onPermissionShowRationale(final int requestCode, final String[] permissions) {
                Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
                        .setAction(buttonText, new RationaleButtonClickListener(permissionManager, requestCode, permissions))
                        .show();
            }
        };
    }

    public static OnPermissionShowRationaleCallback showPermissionShowRationaleSnackbar(@NonNull final PermissionManager permissionManager, @NonNull final View view, @StringRes final int textResId, @StringRes final int buttonTextResId) {
        return new OnPermissionShowRationaleCallback() {
            @Override
            public void onPermissionShowRationale(final int requestCode, final String[] permissions) {
                Snackbar.make(view, textResId, Snackbar.LENGTH_INDEFINITE)
                        .setAction(buttonTextResId, new RationaleButtonClickListener(permissionManager, requestCode, permissions))
                        .show();
            }
        };
    }

    public static  OnPermissionShowRationaleCallback showPermissionRationaleFragment(final FragmentManager fragmentManager, @IdRes final int viewId, final Fragment fragment, final boolean addToBackStack) {
        return new OnPermissionShowRationaleCallback() {
            @Override
            public void onPermissionShowRationale(int requestCode, String[] permissions) {
                Bundle arguments = fragment.getArguments();
                arguments.putInt("requestCode", requestCode);
                arguments.putStringArray("permissions", permissions);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(viewId, fragment);
                if (addToBackStack) {
                    fragmentTransaction.addToBackStack(null);
                }
                fragmentTransaction.commit();
            }
        };
    }

    private static class SettingsButtonClickListener implements  View.OnClickListener {
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

    private static class RationaleButtonClickListener implements View.OnClickListener {
        private final PermissionManager permissionManager;
        private final int requestCode;
        private final String[] permissions;

        public RationaleButtonClickListener(PermissionManager permissionManager, int requestCode, String[] permissions) {
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
