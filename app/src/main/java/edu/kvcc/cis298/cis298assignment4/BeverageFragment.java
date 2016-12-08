package edu.kvcc.cis298.cis298assignment4;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.RatingCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.UUID;

/**
 * Created by David Barnes on 11/3/2015.
 */
public class BeverageFragment extends Fragment {

    //String key that will be used to send data between fragments
    private static final String ARG_BEVERAGE_ID = "crime_id";

    // >Request codes.
    private static final int REQUEST_CONTACT = 0;

    //private class level vars for the model properties
    private EditText mId;
    private EditText mName;
    private EditText mPack;
    private EditText mPrice;
    private CheckBox mActive;

    private Button mContact;
    private Button mSendDetails;

    private String mContactName = "";
    private String mContactEmail = "";

    //Private var for storing the beverage that will be displayed with this fragment
    private Beverage mBeverage;

    //Public method to get a properly formatted version of this fragment
    public static BeverageFragment newInstance(String id) {
        //Make a bungle for fragment args
        Bundle args = new Bundle();
        //Put the args using the key defined above
        args.putString(ARG_BEVERAGE_ID, id);

        //Make the new fragment, attach the args, and return the fragment
        BeverageFragment fragment = new BeverageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //When created, get the beverage id from the fragment args.
//        UUID beverageId = (UUID) getArguments().getSerializable(ARG_BEVERAGE_ID);
//        //use the id to get the beverage from the singleton
//        mBeverage = BeverageCollection.get(getActivity()).getBeverage(beverageId);

        String beverageId = (String) getArguments().getSerializable(ARG_BEVERAGE_ID);
        //use the id to get the beverage from the singleton
        mBeverage = BeverageCollection.get(getActivity()).getBeverageByString(beverageId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Use the inflater to get the view from the layout
        View view = inflater.inflate(R.layout.fragment_beverage, container, false);

        //Get handles to the widget controls in the view
        mId = (EditText) view.findViewById(R.id.beverage_id);
        mName = (EditText) view.findViewById(R.id.beverage_name);
        mPack = (EditText) view.findViewById(R.id.beverage_pack);
        mPrice = (EditText) view.findViewById(R.id.beverage_price);
        mActive = (CheckBox) view.findViewById(R.id.beverage_active);

        //Set the widgets to the properties of the beverage
        mId.setText(mBeverage.getId());
        mId.setEnabled(false);
        mName.setText(mBeverage.getName());
        mPack.setText(mBeverage.getPack());
        mPrice.setText(Double.toString(mBeverage.getPrice()));
        mActive.setChecked(mBeverage.isActive());

        //Text changed listenter for the id. It will not be used since the id will be always be disabled.
        //It can be used later if we want to be able to edit the id.
        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the name. Updates the model as the name is changed
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the Pack. Updates the model as the text is changed
        mPack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setPack(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Text listener for the price. Updates the model as the text is typed.
        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the count of characters is greater than 0, we will update the model with the
                //parsed number that is input.
                if (count > 0) {
                    mBeverage.setPrice(Double.parseDouble(s.toString()));
                //else there is no text in the box and therefore can't be parsed. Just set the price to zero.
                } else {
                    mBeverage.setPrice(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Set a checked changed listener on the checkbox
        mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeverage.setActive(isChecked);
            }
        });


        // >Create an intent to get the contact.
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mContact = (Button) view.findViewById(R.id.beverage_contact);
        mContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });


        mSendDetails = (Button) view.findViewById(R.id.beverage_send);
        mSendDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // >
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                // >Add the data to the intent.
                i.putExtra(Intent.EXTRA_EMAIL, new String[] {mContactEmail});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.beverage_report_subject));
                i.putExtra(Intent.EXTRA_TEXT, createBeverageReport());

                // >Make the app prompt what to use.
                i = Intent.createChooser(i, getString(R.string.choose_app));

                startActivity(i);
            }
        });
        mSendDetails.setEnabled(false);

        // >Deactivate the button if there is not a default contacts app.
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mContact.setEnabled(false);
            // >Let a report be sent.
            mSendDetails.setEnabled(true);
        }

        // >Allow a report to be sent if the contact was chosen.
        if (mContactName != "") {
            mContact.setText(mContactName);
            mSendDetails.setEnabled(true);
        }

        //Lastly return the view with all of this stuff attached and set on it.
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // >Don't do anything if the result is not ok.
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CONTACT && data != null) {

            Uri contactUri = data.getData();

            Cursor cursor;  // Cursor object
            String mime;    // MIME type
            int dataIdx;    // Index of DATA1 column
            int mimeIdx;    // Index of MIMETYPE column
            int nameIdx;    // Index of DISPLAY_NAME column

            // Get the name
            cursor = getActivity().getContentResolver().query(contactUri,
                    new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                    null, null, null);
            if (cursor.moveToFirst()) {
                nameIdx = cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME);
                mContactName = cursor.getString(nameIdx);
                mContact.setText(mContactName);
                mSendDetails.setEnabled(true);

                // Set up the projection
                String[] projection = {
                        ContactsContract.Data.DISPLAY_NAME,
                        ContactsContract.Contacts.Data.DATA1,
                        ContactsContract.Contacts.Data.MIMETYPE };

                // Query ContactsContract.Data
                cursor = getActivity().getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI, projection,
                        ContactsContract.Data.DISPLAY_NAME + " = ?",
                        new String[] { mContactName },
                        null);

                if (cursor.moveToFirst()) {
                    // Get the indexes of the MIME type and data
                    mimeIdx = cursor.getColumnIndex(
                            ContactsContract.Contacts.Data.MIMETYPE);
                    dataIdx = cursor.getColumnIndex(
                            ContactsContract.Contacts.Data.DATA1);
                    // Match the data to the MIME type, store in variables
                    do {
                        mime = cursor.getString(mimeIdx);
                        if (ContactsContract.CommonDataKinds.Email
                                .CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
                            mContactEmail = cursor.getString(dataIdx);
                        }
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }

        }
    }

    // >Create a report.
    private String createBeverageReport() {
        return getString(R.string.beverage_report,
                mContactName,
                mBeverage.getId(),
                mBeverage.getName(),
                mBeverage.getPack(),
                mBeverage.getPrice(),
                getString((mBeverage.isActive() ? R.string.beverage_is_active : R.string.beverage_not_active))
        );
    }


}
