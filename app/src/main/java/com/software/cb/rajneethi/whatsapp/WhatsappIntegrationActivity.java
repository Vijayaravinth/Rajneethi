package com.software.cb.rajneethi.whatsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.software.cb.rajneethi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;

public class WhatsappIntegrationActivity extends AppCompatActivity {

    private boolean isMultiPartMessage;
    private Whatsapp whatsapp;
    private AutoCompleteTextView mContacts;
    private EditText mTextMessage;
    private ImageView mImageView;
    private Button mSelectImage;
    private Button mSendMessage;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private Toolbar mToolbar;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @BindString(R.string.whatsapp_send_message)
    String sendWhastappMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_integration);
        ButterKnife.bind(this);
        initialize();

    }

    private void initialize(){
        whatsapp = new Whatsapp();
        setWhatsappContacts();

        mToolbar = (Toolbar) findViewById(R.id.custom_whatsapp_toolbar);
        mToolbar.setTitle(sendWhastappMessage);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this,android.R.color.white));



       mTextMessage = (EditText) findViewById(R.id.whatsapp_text_message);

       mImageView = (ImageView) findViewById(R.id.whatsapp_selected_image);

       mSelectImage = (Button) findViewById(R.id.whatsapp_image_message);
       mSelectImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent();
               intent.setType("image/*");
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(intent, "Select A Picture"), PICK_IMAGE_REQUEST);

           }
       });

        mSendMessage = (Button) findViewById(R.id.whatsapp_send_message);
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =   mContacts.getText().toString();
                String phoneNumber = getPhoneNumber(name);
                String message = mTextMessage.getText().toString();

                sendTextMessage(phoneNumber,message);
            }
        });


    }



    private void sendTextMessage(String phoneNumber,String message){
         phoneNumber = phoneNumber.replace("+","");
         String type = "text/plain";

        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.putExtra("jid", phoneNumber + "@s.whatsapp.net"); //phone number without "+" prefix
            if(isMultiPartMessage){
                type = "*/*";
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            }

            sendIntent.setType(type);
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch(Exception e) {
            Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();
            isMultiPartMessage = true;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        private void setWhatsappContacts(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Cursor cursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,new String[]{ContactsContract.RawContacts.CONTACT_ID,
                            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY},
                    ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                    new String[] { "com.whatsapp" },
                    null
            );

            List<String> whatsappContacts = new ArrayList<>();
            int contactNameColumn = cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY);

            while (cursor.moveToNext()){
                String contact = cursor.getString(contactNameColumn);
                whatsappContacts.add(contact);
            }

            String[] contacts = whatsappContacts.toArray(new String[whatsappContacts.size()]);
            whatsapp.setContacts(contacts);

            mContacts = (AutoCompleteTextView) findViewById(R.id.whatsapp_contact);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contacts);
            mContacts.setAdapter(adapter);

        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setWhatsappContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, you cannot select a contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getPhoneNumber(String name) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if(ret==null)
            ret = "Unsaved";
        return ret;
    }

}