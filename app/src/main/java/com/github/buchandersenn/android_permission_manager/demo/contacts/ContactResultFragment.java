package com.github.buchandersenn.android_permission_manager.demo.contacts;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.buchandersenn.android_permission_manager.demo.R;

public class ContactResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Projection for the content provider query includes the id and primary name of a contact.
     */
    private static final String[] PROJECTION = {ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
    /**
     * Sort order for the query. Sorted by primary name in ascending order.
     */
    private static final String ORDER = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC";

    private TextView resultView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_result, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        resultView = (TextView) view.findViewById(R.id.contact_result);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            final int totalCount = cursor.getCount();
            if (totalCount > 0) {
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                resultView.setText(getResources().getString(R.string.contacts_result, totalCount, name));
            } else {
                resultView.setText(R.string.contacts_empty);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        resultView.setText(R.string.contacts_empty);
    }
}
