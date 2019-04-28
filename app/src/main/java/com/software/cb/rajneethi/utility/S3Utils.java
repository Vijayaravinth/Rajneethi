package com.software.cb.rajneethi.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.software.cb.rajneethi.BuildConfig;
import com.software.cb.rajneethi.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;

import java.io.File;

public class S3Utils {

    private static final String S3_BUCKETNAME = "rn-assets";
    private static final String S3_CONSTITUENCIES_FOLDER = "constituencies";
    private static final String S3_RICHASSETS_FOLDER = "richassets";
    private static final String S3_CONSTITUENCY_DESTINATION_FOLDER = "offline/data/";
    private static volatile S3Utils instance;
    private BasicAWSCredentials s3Credentials;
    private AmazonS3Client client;
    private String appFilePath;
    private Context context;
    private TransferUtility transferUtility;

    private String TAG = "S3Utils";

    private SharedPreferenceManager sharedPreferenceManager ;

    private S3Utils(Context context) {
        this.context = context;
        sharedPreferenceManager = new SharedPreferenceManager(context);

    }

    public static S3Utils getInstance(Context context) {
        if (instance == null) {
            synchronized (S3Utils.class) {
                if (instance == null) {
                    instance = new S3Utils(context);
                }
            }
        }
        return instance;
    }


    //uplosd export db_file
    public void upload_export_db(final File filetoBeUploaded, String path, final IOSDialog dialog) {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(context)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

        Log.w(TAG,"path "+ path);
        Log.w(TAG,"file name :"+ filetoBeUploaded.getName());

        TransferObserver uploadObserver =
                transferUtility.upload(S3_BUCKETNAME,
                        path+"/"+filetoBeUploaded.getName(),
                        filetoBeUploaded);


        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    dialog.dismiss();
                    Toast.makeText(context,context.getResources().getString(R.string.successfullyUploaded),Toast.LENGTH_LONG).show();
                    Log.w(TAG, " task completed");
                }else if(TransferState.FAILED == state){
                    dialog.dismiss();
                    Toast.makeText(context,context.getResources().getString(R.string.Error),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });
    }


}
