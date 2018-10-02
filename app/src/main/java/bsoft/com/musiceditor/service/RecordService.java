package bsoft.com.musiceditor.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.naman14.androidlame.AndroidLame;
import com.naman14.androidlame.LameBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.activity.MainActivity;
import bsoft.com.musiceditor.fragment.RecorderFragment;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.MyApplication;
import bsoft.com.musiceditor.utils.SharedPrefs;
import bsoft.com.musiceditor.utils.Utils;
import ca.uol.aig.fftpack.RealDoubleFFT;
import static bsoft.com.musiceditor.fragment.RecorderFragment.sFormat;
import static bsoft.com.musiceditor.fragment.RecorderFragment.sQuality;
import static bsoft.com.musiceditor.utils.Keys.STOP_SERVICE;
import static bsoft.com.musiceditor.utils.Utils.addAudio;

public class RecordService extends Service {
    private static final int NOTIFICATION_ID_CUSTOM_BIG = 9;
    private static final String _FORMAT = "Format";
    private static final String CHANEL_ID = "1";
    private static final String NOTIFICATION_CHANNEL_ID = "audio_record_chanel_id";
    private static final String RECORD_NOTIFICATION = "audio_record";
    private static final String CHANNEL_DESCRIPTION = "audio_record_description";
    public String FORMAT = ".mp3";
    public boolean isRecord = false;
    public boolean isRecording = false;
    String treePath;
    NotificationManager notificationManager;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private long time = 0;
    private String valueFile = null;
    private Message message;
    private String mFileName;
    private String mFilePath;
    private int minBuffer;
    private int inSamplerate = 44100;
    private long limitedTime = 0;
    private AudioRecord audioRecord;
    private AndroidLame androidLame;
    private FileOutputStream outputStream;
    private int bitRate = 128;
    private boolean isHideIcoinNotification;
    private RealDoubleFFT transformer;
    private String mFileNomedia;

    public static double getFolderSize(File f) {
        double size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int checkRecord = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        int checkWriteExternal = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (checkRecord == 0 && checkWriteExternal == 0) {
            switch (intent.getAction()) {
                case Keys.START_SERVICE:
                    if (!isRecord) {
                        isRecord = true;
                        startRecord();

                    } else {
                        isRecord = false;
                        statusStop();
                    }
                    break;

                case STOP_SERVICE:
                    statusStop();
                    break;

                default:
                    break;
            }
        } else {
            Toast.makeText(this, getString(R.string.msg_need_permission), Toast.LENGTH_SHORT).show();
        }

        return START_NOT_STICKY;
    }

    private void statusStop() {
        isRecording = !isRecording;
        stopSelf();
    }

    private void startRecord() {
        if (!isRecording) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isRecording = true;
                    mStartingTimeMillis = System.currentTimeMillis();
                    setFormat();
                    setOutBitrate();
                    setFileNameAndPath();
                    startRecording();
                }
            }).start();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.startRecord), Toast.LENGTH_SHORT).show();
            updateTime();
            createNotification();
        }
    }

    private void createNotification() {

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            NotificationChannel notificationChannel;
            if (isHideIcoinNotification) {
                notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, RECORD_NOTIFICATION, NotificationManager.IMPORTANCE_LOW);
            } else {
                notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, RECORD_NOTIFICATION, NotificationManager.IMPORTANCE_LOW);
            }

            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification1);

        Intent stopRecord = new Intent(getApplicationContext(), RecordService.class);
        stopRecord.setAction(STOP_SERVICE);
        PendingIntent pStop = PendingIntent.getService(getApplicationContext(), 0, stopRecord, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imgStopRecord, pStop);
        remoteViews.setTextViewText(R.id.tvTitle2, getString(R.string.recording) + "...");

        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext()
                , 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(getApplicationContext());

        notificationCompatBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Notification notification = notificationCompatBuilder.build();

        startForeground(NOTIFICATION_ID_CUSTOM_BIG, notification);
    }

    private void setFormat() {
//        FORMAT = SharedPrefs.getInstance().get(_FORMAT, String.class, "");
//        if (FORMAT.equals("")) {
//            FORMAT = Utils.FORMAT_MP3;
//        }
        FORMAT = sFormat;
    }

    private void setOutBitrate() {
        switch (FORMAT) {
            case Utils.FORMAT_MP3:
                bitRate = SharedPrefs.getInstance().get(Utils.BITRATE_MP3, Integer.class);
                break;
            case Utils.FORMAT_WAV:
                bitRate = SharedPrefs.getInstance().get(Utils.BITRATE_WAV, Integer.class);
                break;

            default:
                break;
        }
        if (bitRate == 0) {
            bitRate = 128;
        }
    }

    private void readSizeFile() {
        File f = new File(String.valueOf(Uri.parse(mFilePath)));
        double Filesize = getFolderSize(f) / 1024;

        if (Filesize >= 1000) {
            BigDecimal rowOff = new BigDecimal(Filesize / 1024).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            valueFile = rowOff + " Mb";
        } else {
            BigDecimal rowOff = new BigDecimal(Filesize).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            valueFile = rowOff + " Kb";
        }
    }

    private void updateTime() {
        if (RecorderFragment.mHandler == null) {
            RecorderFragment.mHandler = new Handler();
        }

        RecorderFragment.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (limitedTime != 0 && time == limitedTime) {

                    isRecording = !isRecording;

                    sendBroadcast(new Intent().setAction(Keys.STOP_RECORD));

                    RecorderFragment.mHandler.removeCallbacksAndMessages(null);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.stopRecord), Toast.LENGTH_SHORT).show();
                    stopForeground(true);
                    stopSelf();

                } else {
                    readSizeFile();

                    message = new Message();
                    Bundle b = new Bundle();
                    b.putLong(Keys.TIME, time);
                    b.putString(Keys.FILE_SIZE, valueFile);
                    message.setData(b);

                    RecorderFragment.mHandler.sendMessage(message);
                    RecorderFragment.mHandler.postDelayed(this, 1000);
                    time = time + 1000;
                }
            }
        }, 100);
    }


    public void setFileNameAndPath() {

        long timeAddRecord = System.currentTimeMillis();

        Date date = new Date(timeAddRecord);


        String lasmod = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(date);

        File f;
        do {
            String filePath = SharedPrefs.getInstance().get("LocalSaveFile", String.class, null);

            mFileName = lasmod + "_" + bitRate + "kbs" + FORMAT;

            if (filePath == null) {
                mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                mFileNomedia = mFilePath + "/" + Keys.DIR_APP + Keys.DIR_RECORDER;
                mFilePath += Keys.DIR_APP + "/" + Keys.DIR_RECORDER + "/" + mFileName;

            } else {
                mFileNomedia = filePath;
                mFilePath = filePath + mFileName;
            }

            f = new File(mFilePath);

        } while (f.exists() && !f.isDirectory());
    }

    private FileOutputStream createFileOutputStreamFromDocumentTree() throws FileNotFoundException {
        //  String rootTreePath = MyApplication.getUriTree();
        treePath = SharedPrefs.getInstance().get(Utils.TREE_URI, String.class, null);
//         Log.d("lynah",treePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && treePath != null) {
            try {
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, Uri.parse(treePath));
                DocumentFile mediaFile = pickedDir.createFile("audio/*", new File(mFilePath).getName());

                if (mediaFile != null) {
                    FileOutputStream outputStream = (FileOutputStream) MyApplication.getAppContext().getContentResolver().openOutputStream(mediaFile.getUri());
                    if (outputStream == null) {
                        throw new Exception("outputStream null: Create new file");
                    } else {
                        return outputStream;
                    }
                } else {
                    throw new Exception("mediaFile null: Create new file");
                }
            } catch (Exception e) {
                e.printStackTrace();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Keys.DIR_APP + Keys.DIR_RECORDER;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(path, new File(mFilePath).getName());
                mFilePath = file.getAbsolutePath();
                return new FileOutputStream(mFilePath);
            }
        } else {
            return new FileOutputStream(new File(mFilePath));
        }
    }

    private void startRecording() {

        minBuffer = AudioRecord.getMinBufferSize(inSamplerate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, inSamplerate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBuffer * 2);

        short[] buffer = new short[inSamplerate * 2 * 5];

        byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

        try {
            outputStream = createFileOutputStreamFromDocumentTree();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        androidLame = new LameBuilder()
                .setInSampleRate(inSamplerate)
                .setOutChannels(1)
                .setOutBitrate(bitRate)
                .setOutSampleRate(sQuality)
                .setAbrMeanBitrate(bitRate)
                .build();

        audioRecord.startRecording();

        int bytesRead = 0;

        int blockSize = 1024;
        double[] toTransform = new double[blockSize];

        if (transformer == null) {
            transformer = new RealDoubleFFT(blockSize);
        }
        while (isRecording) {
            bytesRead = audioRecord.read(buffer, 0, minBuffer);

            for (int i = 0; i < blockSize && i < bytesRead; i++) {
                toTransform[i] = (double) buffer[i] / 32768.0d; // signed 16 bit
            }
            transformer.ft(toTransform);

            if (RecorderFragment.mHandler != null) {
                RecorderFragment.mHandler.sendMessage(RecorderFragment.mHandler.obtainMessage(1, toTransform));
            }

            if (bytesRead > 0) {

                int bytesEncoded = androidLame.encode(buffer, buffer, bytesRead, mp3buffer);

                if (bytesEncoded > 0) {
                    try {
                        outputStream.write(mp3buffer, 0, bytesEncoded);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        RecorderFragment.mHandler.removeCallbacksAndMessages(null);

        int outputMp3buf = androidLame.flush(mp3buffer);

        if (outputMp3buf > 0) {
            try {

                outputStream.write(mp3buffer, 0, outputMp3buf);
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);

        audioRecord.stop();
        audioRecord.release();

        androidLame.close();

        File file = new File(mFileNomedia, ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        sendBroadcast(new Intent().setAction(Keys.STOP_RECORD));
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.stopRecord), Toast.LENGTH_SHORT).show();

//        if (time - 100 < 1000) {
//            String urilFile = SharedPrefs.getInstance().get(Utils.TREE_URI, String.class, null);
//            if (urilFile != null && !urilFile.equals("")) {
//
//                DocumentFile fileuri = DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(urilFile));
//                DocumentFile file = fileuri.findFile(new File(mFilePath).getName());
//                if (file != null) {
//                    file.delete();
//                } else {
//                    File f = new File(mFilePath);
//                    f.delete();
//                }
//
//            } else {
//                File file = new File(mFilePath);
//                file.delete();
//            }
//
//            //Toast.makeText(getApplicationContext(), getString(R.string.file_is_too_small), Toast.LENGTH_SHORT).show();
//        } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && treePath != null) {
            //mDataHandler.addRecord(mFileName, mFilePath, time - 100, valueFile, treePath);
            addAudio(mFilePath, mFileName, getApplicationContext());
            //Log.d("newp", treePath + " " + mFilePath);
        } else {
            //  mDataHandler.addRecord(mFileName, mFilePath, time - 100, valueFile, Utils.EMPTY);
            addAudio(mFilePath, mFileName, getApplicationContext());
        }


        //sendBroadcast(new Intent().setAction(Utils.UPDATE_LIST));

        stopForeground(true);
        stopSelf();

        super.onDestroy();
    }
}
