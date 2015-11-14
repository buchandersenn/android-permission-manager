# android-permission-manager

Android runtime permissions were introduced in Android 6.0 Marshmallow. They are unquestionably 
a boon for Android users, but can be a pain for developers. 

With runtime permissions, Android app developers can no longer assume that the app has permission
to access the contacts, accounts, camera or any other permissions categorised as 'dangerous.'
The app therefore need to check for permissions each time it wants to perform an operation that
requires a permission, and to ask the user to grant the permission if the app does not already 
posses it.

This is somewhat cumbersome and requires a great deal of boilerplate code. The library can simplify
this, allowing the developer to focus on the actual app functionality instead of Android 
technicalities.

## Example

Take a look at the following example from the RuntimePermissionsBasic sample:

    private void showCameraPreview() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Snackbar.make(mLayout,
                    "Camera permission is available. Starting preview.",
                    Snackbar.LENGTH_SHORT).show();
            startCamera();
        } else {
            // Permission is missing and must be requested.
            requestCameraPermission();
        }
        // END_INCLUDE(startCamera)
    }

    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
    }

... and this is just for checking and requesting the permission. The app still need to deal with the
result of the request and perform the actual intended operation if the request is granted:

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, "Camera permission was granted. Starting preview.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                startCamera();
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, "Camera permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void startCamera() {
        Intent intent = new Intent(this, CameraPreviewActivity.class);
        startActivity(intent);
    }

Phew!

The library reduces the complexity by handling the whole mundane check-permission/show-rationale/
request-permission/perform-operation flow behind the scenes. Using the PermissionManager 
and some of the helper methods included in the library, you can replace all of the above 
with the following few lines of code:

    private final PermissionManager permissionManager = PermissionManager.create(this);

    private void showCameraPreview() {
        permissionManager.with(Manifest.permission.CAMERA)
                .onPermissionGranted(startPermissionGrantedActivity(this, new Intent(this, CameraPreviewActivity.class)))
                .onPermissionDenied(showPermissionDeniedSnackbar(mLayout, "Camera permission request was denied.", "SETTINGS"))
                .onPermissionShowRationale(showPermissionShowRationaleSnackbar(mLayout, "Camera access is required to display the camera preview.", "OK"))
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

# Download

The library is not (yet) available in jCenter, so you'll need to clone the GIT repository and 
include the library in your project manually.

# Usage

## Setup 

A bit of setup is required before using the PermissionManager to handle permission requests. 

Each Activity/Fragment need to implement the appropriate OnRequestPermissionsResultCallback 
from the support library and delegate the onRequestPermissionsResult to a PermissionManager 
instance.

For activities:

    public class MainActivity extends AppCompatActivity 
        implements ActivityCompat.OnRequestPermissionsResultCallback {

        private final PermissionManager permissionManager = PermissionManager.create(this);

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            permissionManager.handlePermissionResult(requestCode, grantResults);
        }

        ...
    }


For fragments:

    public class ContactRationaleFragment extends Fragment 
        implements FragmentCompat.OnRequestPermissionsResultCallback {
        
        private final PermissionManager permissionManager = PermissionManager.create(this);

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            permissionManager.handlePermissionResult(requestCode, grantResults);
        }

        ...
    }

## Performing requests and handling callbacks

A permission request is performed using a syntax inspired by Glide and similar libraries. 

    // Start building a new request using the with() method. 
    // The method takes either a single permission or a list of permissions.
    // Specify multiple permissions in case you need to request both 
    // read and write access to the contacts at the same time, for example.
    permissionManager.with(...)
            // Optionally, specify a request code value for the request
            .usingRequestCode(REQUEST_CODE) 
            
            // Optionally, specify a callback handler for all three callbacks
            .onCallback(new OnPermissionCallback() {...}) 

            // OR specify handlers for each callback separately
            .onPermissionGranted(new OnPermissionGrantedCallback() {...})
            .onPermissionDenied(new OnPermissionDeniedCallback() {...})
            .onPermissionShowRationale(new OnPermissionShowRationaleCallback() {...})
            
            // Finally, perform the request
            .request();

If the app already has the requested permission then the onPermissionGranted callback is invoked
at once. Similarly, if the user has denied the permission and checked the 'never ask again option'
the onPermissionDenied callback is invoked immediately. The onPermissionShowRationale callback is 
invoked if the app should show a rationale before asking the user to grant the permission. 
If neither of these conditions are met, then the permissionManager requests the permission and the 
onPermissionGranted/onPermissionDenied callbacks called once the user has answered.

Alternatively, it is also possible to just check for the permission 'silently':

    permissionManager.with(...)
            .onPermissionGranted(new OnPermissionGrantedCallback() {...})
            .onPermissionDenied(new OnPermissionDeniedCallback() {...})
            .check();

The check() method will not ask the user for permission if it is not available, thus the 
onPermissionShowRationale callback is irrelevant. It will always invoke either the
onPermissionGranted or the onPermissionDenied callback at once.

## Callback interfaces

The callbacks are simple single methods interfaces - with the exception of the aggregate 
OnPermissionCallback interface:

    public interface OnPermissionGrantedCallback {
        void onPermissionGranted();
    }

    public interface OnPermissionDeniedCallback {
        void onPermissionDenied();
    }

    public interface OnPermissionShowRationaleCallback {
        void onPermissionShowRationale(PermissionRequest permissionRequest);
    }

    public interface OnPermissionCallback extends 
        OnPermissionGrantedCallback, 
        OnPermissionDeniedCallback, 
        OnPermissionShowRationaleCallback {
    }

When the onPermissionShowRationale callback is called, the app is expected to show some kind of 
rationale to the user, perhaps in the form of a Snackbar. The rationale should include 
a way for the user to indicate acceptance of the rationale, typically in the form of a
"OK" og "GOT IT" button. 

The PermissionRequest instance provided by the onPermissionShowRationale callback contains a 
single public method. This method lets the permission manager know that the user has accepted the 
rationale. If the rationale is accepted then the permission manager automatically tries to request 
the permission again. 
           
    void onPermissionShowRationale(PermissionRequest permissionRequest) {
        ...
        
        okButton.OnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionRequest.acceptPermissionRationale();
            }
        });
    }

When the user answers the permission prompt, the onPermissionGranted or onPermissionDenied 
callbacks are called, just as if the rationale had not been shown.

## Common callbacks handlers

Some callbacks handlers are common across a wide range of apps: launching a new activity 
when a permission is granted, using a snackbar to show the permission rationale or
showing a fragment if the permission request is denied.

To facilitate these common callback types the library contains a collection of common callback
implementations in the class PermissionCallbacks. Each callback handler is wrapped in an 
appropriately named factory method. By static importing these methods the code can 
be streamlined further, as shown in the initial example:

    import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.showPermissionDeniedSnackbar;
    import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.showPermissionShowRationaleSnackbar;
    import static com.github.buchandersenn.android_permission_manager.callbacks.PermissionCallbacks.startPermissionGrantedActivity;

    ...
    
    permissionManager.with(Manifest.permission.CAMERA)
            .onPermissionGranted(startPermissionGrantedActivity(this, new Intent(this, CameraPreviewActivity.class)))
            .onPermissionDenied(showPermissionDeniedSnackbar(mLayout, "Camera permission request was denied.", "SETTINGS"))
            .onPermissionShowRationale(showPermissionShowRationaleSnackbar(mLayout, "Camera access is required to display the camera preview.", "OK"))
            .request();

It is even possible to chain callback handlers together using the doAll() callback handler 
as a wrapper. Here's an example from the sample app:

    permissionManager.with(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
            .onPermissionGranted(showPermissionGrantedFragment(getFragmentManager(), R.id.fragment_container, new ContactResultFragment(), false))
            .onPermissionShowRationale(showPermissionRationaleFragment(getFragmentManager(), R.id.fragment_container, new ContactRationaleFragment(), false))
            .onPermissionDenied(doAll(
                    setPermissionDeniedViewVisibility(contactsDeniedView, View.VISIBLE),
                    setPermissionDeniedViewEnabled(contactsButton, false)))
            .request();

You are free to use the same technique to bundle your own callbacks in a similar 
MyFavoriteCallbacks class, or to contact me if you think some important common callback handlers 
are missing from the library.

# Author

Nicolai Buch-Andersen<br/>
Google+ <https://google.com/+NicolaiBuchAndersen><br/>
Email: <nicolai.buch.andersen@gmail.com><br/>

# License

    Copyright 2015 Nicolai Buch-Andersen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.