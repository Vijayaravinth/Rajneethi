package com.software.cb.rajneethi.utility;

import android.Manifest;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.google.gson.JsonObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.software.cb.rajneethi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

/**
 * Created by DELL on 10-01-2018.
 */

public class UtilActivity extends ActivityManagePermission implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    ProgressDialog pDialog;
    public SpannableString s;

    int year, month, day;
    private BlockingDeque queue = new LinkedBlockingDeque();
    ConnectionFactory factory = new ConnectionFactory();


    Date startedTime, endedTime;


    android.location.Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private String location_string;
    private static LocationManager locationManager;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 0 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 3000;

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

/*    public void setupConnectionFactory() {
        String uri = "";
        try {
            factory.setAutomaticRecoveryEnabled(false);
            factory.setUri(uri);
        } catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }*/

    void publishMessage(String message) {
        Log.w(TAG, "pubish message is working");
        try {
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void publishToAMQP() {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        while (true) {
                            String message = queue.takeFirst().toString();
                            try {
                                ch.basicPublish("amq.fanout", "chat", null, message.getBytes());
                                Log.w(TAG, "[s] " + message);
                                ch.waitForConfirmsOrDie();
                            } catch (Exception e) {
                                Log.w(TAG, "[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.w(TAG, "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }

    public void subscribe(final Handler handler) {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        channel.basicQos(1);
                        AMQP.Queue.DeclareOk q = channel.queueDeclare();
                        channel.queueBind(q.getQueue(), "amq.fanout", "chat");
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(q.getQueue(), true, consumer);

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            String message = new String(delivery.getBody());
                            Log.w(TAG, "[r] " + message);
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", message);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        Log.w(TAG, "Connection broken: " + e1.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.start();
    }

    Thread publishThread, subscribeThread;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (publishThread != null) {
            publishThread.interrupt();
        }

        if (subscribeThread != null) {
            subscribeThread.interrupt();
        }
    }

    public void setup_toolbar_with_back() {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.w(TAG, "Util Location :" + location.getLatitude() + ":" + location.getLongitude());

        JSONObject message = new JSONObject();
        try {
            message.put("location", location.getLatitude() + "," + location.getLongitude());
            message.put("date", get_current_date());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        publishMessage(message.toString());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //make dir
    public void makeDir() {
        File newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
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


    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    LocationSettingsRequest.Builder locationSettingsRequest;

    public void mEnableGps() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(UtilActivity.this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        mLocationSetting();
    }

    public void mLocationSetting() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);

        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        mResult();

    }

    PendingResult<LocationSettingsResult> pendingResult;

    public static final int REQUEST_LOCATION = 001;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String TAG = "Util";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_LOCATION) {
            try {
                final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            } catch (Exception e) {

            }
            switch (requestCode) {
                case REQUEST_LOCATION:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
                            Log.w(TAG, "Gps enabled");
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            Log.w(TAG, "Gps not enabled");
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }


    public void mResult() {
        pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();


                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

                            status.startResolutionForResult(UtilActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.


                        break;
                }
            }

        });
    }

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


    public String getCurrentateAndTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.US);
        Date date = new Date();
        return formatter.format(date);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
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

    public void getLocation() {
        try {

            Log.w(TAG, "inside get location called ");
            if (locationManager == null) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                Log.w(TAG, "GPS Enabled");
                canGetLocation = true;
                isGPSEnabled = true;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        //if (latitude == 0L && longitude != 0L) {


                        Log.w(TAG, "Get location working");
                        //  publishMessage(latitude + "," + longitude);

                      /*  LatLng myLocation = new LatLng(latitude, longitude);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                myLocation).zoom(18).build();
                        map.clear();

                        map.addMarker(new MarkerOptions().position(myLocation)
                                .title("My Location"));
                        //   map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
                        //   map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

                        Log.w(TAG, " Latitude " + latitude + " Longitude " + longitude);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
