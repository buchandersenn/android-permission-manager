package com.github.buchandersenn.android_permission_manager.demo.contacts;

import android.Manifest;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.demo.R;

import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.doAll;
import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.setPermissionDeniedViewEnabled;
import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.setPermissionDeniedViewVisibility;
import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.showPermissionGrantedFragment;
import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.showPermissionRationaleFragment;

public class ContactRequestFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback {
    private final PermissionManager permissionManager = PermissionManager.create(this);

    private Button contactsButton;
    private View contactsDeniedView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_request, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        contactsButton  = (Button) view.findViewById(R.id.button_open_contacts);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContacts();
            }
        });
        contactsDeniedView = view.findViewById(R.id.contacts_permission_denied);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

    private void showContacts() {
        permissionManager.with(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
                .onPermissionGranted(showPermissionGrantedFragment(getFragmentManager(), R.id.fragment_container, new ContactResultFragment(), false))
                .onPermissionShowRationale(showPermissionRationaleFragment(getFragmentManager(), R.id.fragment_container, new ContactRationaleFragment(), false))
                .onPermissionDenied(doAll(
                        setPermissionDeniedViewVisibility(contactsDeniedView, View.VISIBLE),
                        setPermissionDeniedViewEnabled(contactsButton, false)))
                .request();
    }
}
