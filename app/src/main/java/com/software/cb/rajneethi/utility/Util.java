package com.software.cb.rajneethi.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

/**
 * Created by Vijay on 14-10-2016.
 */

public class Util extends ActivityManagePermission {

    ProgressDialog pDialog;
    public SpannableString s;

    int year, month, day;

    public void setup_toolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.foot);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar_text_style();
        // toolbar.setTitle(s);
    }

    public void setup_toolbar(Toolbar toolbar,String title) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(title);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.foot);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar_text_style();
        // toolbar.setTitle(s);
    }

    public void setup_toolbar_with_back(Toolbar toolbar, String title){
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.foot);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.img_back));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar_text_style();
        // toolbar.setTitle(s);

        getSupportActionBar().setTitle(title);
    }

    //make dir
    public void makeDir() {
        File newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
    }

    public boolean checkIsANumber(String number) {

        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void createTextFile(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/");
            //  File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Log.w(TAG, "saved");
            //  Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public void reWriteTextFile(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/");
            //  File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            StringBuilder text = new StringBuilder();

            if (gpxfile.exists()) {

                BufferedReader br = new BufferedReader(new FileReader(gpxfile));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();


                sBody = text + "\n" + sBody;
                write(gpxfile, sBody);

            } else {
                write(gpxfile, sBody);
            }
            Log.w(TAG, "saved");
            //  Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(File file, String sBody) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    IOSDialog dialog;

    /*Show progress dialog*/
    public IOSDialog show_dialog(Context context, boolean is_cancallable) {
        dialog = new IOSDialog.Builder(context)
                .setTitle("Loading")
                .setTitleColorRes(R.color.icons)
                .setCancelable(is_cancallable)
                .setSpinnerColor(ContextCompat.getColor(context, R.color.icons))
                .build();
        return dialog;
    }


    public void askPermission(String[] permission) {
        askCompactPermissions(permission, new PermissionResult() {
            @Override
            public void permissionGranted() {

            }

            @Override
            public void permissionDenied() {

            }

            @Override
            public void permissionForeverDenied() {

            }
        });
    }

    public boolean checkPermissionGranted(String permission, Context context) {
        return isPermissionGranted(context, permission);
    }

    public String getExtensionType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    //check permissions
    public void askAllPermission() {
        askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_READ_PHONE_STATE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_SEND_SMS, PermissionUtils.Manifest_READ_CONTACTS, PermissionUtils.Manifest_RECORD_AUDIO}, new PermissionResult() {
            @Override
            public void permissionGranted() {
                //permission granted
                //replace with your action
            }

            @Override
            public void permissionDenied() {
                //permission denied
                //replace with your action
            }

            @Override
            public void permissionForeverDenied() {
                // user has check 'never ask again'
                // you need to open setting manually
                //  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                //  Uri uri = Uri.fromParts("package", getPackageName(), null);
                //   intent.setData(uri);
                //  startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            }
        });
    }






    private String TAG = "Util";





    //check external storage permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean check_external_storage_permission(Context context) {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //File write logic here
            return true;
        } else {
            return false;
        }
    }

    public void set_Linearlayout_manager(RecyclerView recyclerView, Context context) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    //set horizontal layout manager
    public void set_horizontal_layout_manager(RecyclerView recyclerView, Context context) {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    public void set_grid_layout_manager(RecyclerView recyclerView, Context context, int count) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, count));
    }

    //get file type
    public String getFileType(String path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }

    //get mime type
    public String getMimeType(Uri uri) {
        String type;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            type = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        type = type.substring(type.indexOf("/") + 1, type.length());
        return type;
    }

    //Convert bitmap into string
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    public void alert_for_no_internet() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.noInternet))
                .setMessage(getResources().getString(R.string.makeSureInternet))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                // .setIcon(R.mipmap.ic_person_add_black_24dp)
                .show();
    }

   /* //set toolbar text style
    public void toolbar_text_style() {
        s = new SpannableString(getResources().getString(R.string.gallery_activity));
        Typeface type = Typeface.createFromAsset(getAssets(), "GeosansLight.ttf");
        s.setSpan(new CustomTypefaceSpan(type), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }*/

    public void Toastmsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void Snackbar_msg(View v, String msg) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show();
    }


    public void show_dialog(Context context) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(getResources().getString(R.string.pdialogmsg));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public String get_current_date() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuilder string = new StringBuilder()
                // Month is 0 based so add 1
                .append(year).append("-")
                .append(month + 1).append("-")
                .append(day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return string.toString();

    }


    public String get_current_time() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        return sdf.format(d);
    }

    public void dismiss_dialog() {
        pDialog.dismiss();
    }

    public int generate_random_number() {
        Random rand = new Random();
        return rand.nextInt(20);
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert(final Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.gpsSettings));

        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.gpsmsg));

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /*check gps enabled or not*/
    public boolean is_enabled_gps(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        // getting GPS status
        assert locationManager != null;
        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
