package com.software.cb.rajneethi.qc;

import android.content.DialogInterface;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.ListenToPeopleActivity;
import com.software.cb.rajneethi.adapter.QCAudioFilesAdapter;
import com.software.cb.rajneethi.models.AudioFileDetails;
import com.software.cb.rajneethi.models.QCAudioDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.checkInternet;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by DELL on 04-04-2018.
 */

public class QCUserAudioActivity extends UtilActivity implements Handler.Callback {


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.qc)
    String title;

    @BindView(R.id.audioRecyclerview)
    RecyclerView recyclerView;

    ArrayList<QCAudioDetails> list = new ArrayList<>();
    QCAudioFilesAdapter adapter;
    String dir;
    File file;

    int downloadPos = 0, playPos = 0;

    private String TAG = "QCUserAudio";


    private static final String S3_BUCKETNAME = "rn-assets";
    private BasicAWSCredentials s3Credentials;
    private AmazonS3Client client;
    private TransferUtility transferUtility;
    private TransferObserver transferObserver;

    @BindString(R.string.fileMissing)
    String fileNotFound;

    @BindString(R.string.noInternet)
    String noInternet;

    @BindString(R.string.survey_report)
    String dialogTitle;
    @BindString(R.string.validate)
    String valid;
    @BindString(R.string.invalidate)
    String invalid;
    @BindString(R.string.skip)
    String skip;
    @BindString(R.string.dialogmsg)
    String dialogMsg;

    private Handler mHandler = new Handler();
    Runnable updateRunnable;
    ThreadPoolExecutor executor;


    private SharedPreferenceManager sharedPreferenceManager;

    @BindView(R.id.audioLayout)
    CardView audioLayout;
    @BindView(R.id.txtAudioFilename)
    TextView txtAudioFileName;

    @BindView(R.id.imgPlay)
    ImageView play;


    @BindView(R.id.imgNext)
    ImageView imgNext;
    @BindView(R.id.imgPrevious)
    ImageView imgPrevious;

    @BindView(R.id.seekBar)
    SeekBar seekBar;
    private int oldPosition = -1, seekbarPosition;

    String name;
    MediaPlayer player;
    AlertDialog dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qc_user_audio);
        ButterKnife.bind(this);




        if (getIntent().getExtras() != null) {
            name = getIntent().getExtras().getString("name");
            setup_toolbar_with_back(toolbar,name);
        }


        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );

        sharedPreferenceManager = new SharedPreferenceManager(this);


        set_grid_layout_manager(recyclerView, this, 2);

        setAdapter();

        forwardMusic();
    }

    public void hideAudioLayout() {
        if (audioLayout.isShown()) {
            audioLayout.setVisibility(View.GONE);
        }
        releasePlayer();
    }


    private void releasePlayer() {
        if (player != null) {
            player.pause();
            player.reset();
            player.release();
            player = null;
        }
    }

    private void changeAudio() {
        playPos++;
        playAudio(playPos, false);
    }

    @OnClick(R.id.imgPrevious)
    public void gotoPreviousAudio() {
        QCAudioDetails details = list.get(playPos);
        details.setPlaying(false);
        details.setPaused(false);
        adapter.notifyDataSetChanged();
        if (playPos != 0) {
            playPos--;
            playAudio(playPos, false);
        }
    }

    @OnClick(R.id.imgNext)
    public void gotoNextAudio() {
        QCAudioDetails details = list.get(playPos);
        details.setPlaying(false);
        details.setPaused(false);
        adapter.notifyDataSetChanged();
        if (playPos != list.size() - 1) {
            playPos++;
            playAudio(playPos, false);
        }
    }


    public void showAlert() {
        // String[] singleChoiceItems = getResources().getStringArray(R.array.dialog_single_choice_array);

        try {
            if (dialog != null) {
                dialog.dismiss();
            }

            dialog = new AlertDialog.Builder(this)
                    .setTitle(dialogTitle)
                    .setMessage(dialogMsg)
                    .setPositiveButton(valid, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Log.w(TAG,"valid clicked");
                        }
                    })
                    .setNegativeButton(invalid, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.w(TAG,"invalid clicked");
                        }
                    })
                    .setNeutralButton(skip, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.w(TAG,"skip clicked");
                        }
                    })
                    .show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }, 15000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void playAudioFromAdapter(int pos) {
        if (oldPosition != -1) {
            QCAudioDetails details = list.get(oldPosition);
            details.setPlaying(false);
            details.setPaused(false);
            adapter.notifyDataSetChanged();
        }

        oldPosition = pos;


        playAudio(pos, true);
    }

    public void playAudio(int pos, boolean playFromAdapter) {

        if (pos < list.size()) {
            QCAudioDetails details = list.get(pos);

            if (pos == 0) {
                imgPrevious.setVisibility(GONE);
            } else {
                imgPrevious.setVisibility(View.VISIBLE);
            }

            if (pos == list.size() - 1) {
                imgNext.setVisibility(GONE);
            } else {
                imgNext.setVisibility(View.VISIBLE);
            }

            if (checkFileExist(details.getAudioFileName())) {
                details.setPlaying(true);
                adapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(pos);

                if (!audioLayout.isShown()) {
                    audioLayout.setVisibility(View.VISIBLE);
                }

                //  oldPosition = pos;

                txtAudioFileName.setText(details.getAudioFileName());
                seekbarPosition = 0;
                seekBar.setProgress(seekbarPosition);

                playSong(getApplicationContext().getCacheDir() + "/" + details.getAudioFileName(), playFromAdapter);
            } else {
                changeAudio();
            }
        }

    }

    private void playSong(String songPath, final boolean isPlayFromAdapter) {

        Log.w(TAG, "Song path : " + songPath);
        try {

            releasePlayer();
            player = new MediaPlayer();

            player.setDataSource(songPath);
            player.prepare();
            player.start();
            int duration = player.getDuration();
            Log.w("Duartion ", duration + "");
            duration = duration / 1000;
            Log.w("Duartion ", (duration / 60) + "");
            seekBar.setMax(duration);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    showAlert();

                }
            }, 15000);

            hidePlayButton();

            this.runOnUiThread(updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (player != null) {
                        seekbarPosition = player.getCurrentPosition() / 1000;
                        seekBar.setProgress(seekbarPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });

            player.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.w(TAG, "buffering start");
                            //   show_dialog(SuccessStoriesAudioActivity.this);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.w(TAG, "buffering end");
                            //  dismiss_dialog();
                            break;
                    }
                    return false;
                }
            });


            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    hidePauseButton();
                    QCAudioDetails details = list.get(playPos);
                    details.setPlaying(false);
                    details.setPaused(false);

                    if (oldPosition != -1) {
                        QCAudioDetails details1 = list.get(oldPosition);
                        details1.setPlaying(false);
                        details1.setPaused(false);

                        oldPosition = -1;
                    }
                    adapter.notifyDataSetChanged();

                    showAlert();


                    if (!isPlayFromAdapter) {
                        playPos++;
                    }
                    playAudio(playPos, false);

                   /* if (audioLayout.isShown()) {
                        audioLayout.setVisibility(GONE);
                    }*/
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void forwardMusic() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (player != null) {

                    if (b) {
                        player.seekTo(i * 1000);

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void hidePlayButton() {
        play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_circle_outline_white_24dp));
        //  play.setVisibility(View.INVISIBLE);
        // pause.setVisibility(View.VISIBLE);
    }

    private void hidePauseButton() {
        //play.setVisibility(View.VISIBLE);
        //pause.setVisibility(View.INVISIBLE);
        play.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_play_circle_outline_white_24dp));

    }

    @OnClick(R.id.imgPlay)
    public void play_audio() {

        hidePlayButton();
        if (!player.isPlaying()) {
            if (player != null) {
                player.start();
            }
        } else {
            pauseAudio();
            QCAudioDetails details = list.get(playPos);
            details.setPlaying(true);
            details.setPaused(true);
            adapter.notifyDataSetChanged();
        }
    }

    public void pauseAudio() {
        Log.w(TAG, "pause audio called");
        if (player != null) {
            player.pause();
            hidePauseButton();
        }
    }


    int res = 0;

    @Override
    protected void onResume() {
        super.onResume();
        downloadAudioOneByOne(downloadPos);
    }

    private boolean checkFileExist(String fileName) {
        File downloadFile = new File(getApplicationContext().getCacheDir(), fileName);

        if (downloadFile.exists()) {
            Log.w(TAG, "file  exist");
            return true;

        } else {
            return false;
        }
    }

    private void downloadAudioOneByOne(int pos) {

        if (downloadPos < list.size()) {
            QCAudioDetails details = list.get(pos);
            //File downloadFile = new File(getApplicationContext().getCacheDir(), details.getAudioFileName());

            if (checkFileExist(details.getAudioFileName())) {
                Log.w(TAG, "file  exist");
                callDownloadMethod();

            } else {
                if (checkInternet.isConnected()) {
                    details.setDownloading(true);
                    adapter.notifyDataSetChanged();
                    executor.execute(new downloadThread(pos, new Handler(this), details.getAudioFileName()));
                } else {
                    Toastmsg(QCUserAudioActivity.this, noInternet);
                }
                Log.w(TAG, "file not exist");
            }
        }
    }

    private void callDownloadMethod() {
        downloadPos++;
        downloadAudioOneByOne(downloadPos);
    }

    @Override
    public boolean handleMessage(Message message) {
        Log.w(TAG, "message : " + message.obj);
        switch (message.obj.toString()) {
            case "1":
                QCAudioDetails details = list.get(message.what);
                details.setDownloading(false);
                details.setDownloaded(true);
                adapter.notifyDataSetChanged();
                callDownloadMethod();
                break;
            case "2":
                QCAudioDetails details1 = list.get(message.what);
                details1.setDownloading(false);
                details1.setDownloaded(false);
                adapter.notifyDataSetChanged();
                callDownloadMethod();
                break;
        }
        return true;
    }


    private int downloadAudio(String fileName, final int pos, final Handler handler) {


        res = 0;
        dir = getCacheDir() + "/" + fileName;
        file = new File(getApplicationContext().getCacheDir(), fileName);

     /*
        transferObserver = transferUtility.download(S3_BUCKETNAME, Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.AUDIO_PATH + "/" + fileName, file);
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                switch (state) {
                    case IN_PROGRESS:
                        Log.w(TAG, "progress");
                        break;
                    case COMPLETED:
                        Log.w(TAG, "completed");
                        res = 1;
                        sendMessage(pos, res + "", handler);

                        break;
                    case FAILED:

                        res = 2;
                        sendMessage(pos, res + "", handler);
                        Log.w(TAG, "Failed");
                        break;

                }
            }*/
/*
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                Log.w(TAG, "Bytes current : " + bytesCurrent + " total :" + bytesTotal);

                try {
                    int percentage = (int) ((bytesCurrent * 100) / bytesTotal);
                    updateProgressbar(pos, percentage);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(int id, Exception ex) {

                res = 2;
                sendMessage(pos, res + "", handler);
            }
        });*/


        return res;
    }


    private void updateProgressbar(int pos, int progress) {

        Log.w(TAG, "pos : " + pos + " progress : " + progress);

        adapter.refresh(pos, progress);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qc_audio_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.audioStart:
                releasePlayer();
                playAudio(playPos, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void setAdapter() {
        adapter = new QCAudioFilesAdapter(this, list, QCUserAudioActivity.this);
        recyclerView.setAdapter(adapter);
    }

    void sendMessage(int what, String msg, Handler handler) {

        Log.w("Donwload", "Send message is working");
        Message message = handler.obtainMessage(what, msg);
        message.sendToTarget();
    }


    private class downloadThread implements Runnable {

        int threadNo;
        Handler handler;
        String fileName;
        public static final String TAG = "Download Thread";

        downloadThread(int threadNo, Handler handler, String fileName) {
            this.threadNo = threadNo;
            this.handler = handler;
            this.fileName = fileName;
        }


        @Override
        public void run() {

          //  downloadAudio(fileName, threadNo, handler);
            Log.w(TAG, "Send message before");
            //sendMessage(threadNo, res + "");

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(updateRunnable);
        }
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAudio();
    }
}
