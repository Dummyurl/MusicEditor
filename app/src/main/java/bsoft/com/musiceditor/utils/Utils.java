package bsoft.com.musiceditor.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bsoft.com.musiceditor.model.AudioEntity;

/**
 * Created by Windows 10 Gamer on 01/03/2018.
 */

public class Utils {
    public static final String SAVE_PATH = "SavePath";
    public static final String PATH = "path";
    public static final String TREE_URI = "treeUri";
    public static final String FORMAT_MP3 = ".mp3";
    public static final String FORMAT_M4A = ".m4a";
    public static final String FORMAT_OGG = ".ogg";
    public static final String FORMAT_WAV = ".wav";
    public static final String FORMAT_AAC = ".aac";
    public static final String FORMAT_FLAC = ".flac";
    public static final String FORMAT_AVI = ".avi";
    public static final String FORMAT_FLV = ".flv";
    public static final String PREFERENCE_URI = "copy_utils";
    public static final String AUDIO_ENTITY = "audio_entity";
    public static final String BITRATE_WAV ="bitrate_wav" ;
    public static final String BITRATE_MP3 ="bitrate_mp3" ;

    private static Pattern pattern = Pattern.compile("time=([\\d\\w:]+)");

    public static void addAudio(String path, String title,Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DURATION, Utils.getMediaDuration(path));
        values.put(MediaStore.Audio.Media.TITLE, title);
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.SIZE, new File(path).length());
        values.put(MediaStore.Audio.Media.ARTIST, "<unknow>");
        values.put(MediaStore.Audio.Media.DATA, new File(path).getAbsolutePath());
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);

        Uri newUri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));

        if (newUri == null) {
            Log.e("xxx", "update contentresolver failllllllllllllllllll");
        } else {
            Log.e("xxx", "update contenresolver");
        }
    }

    public static List<AudioEntity> filterAudioEntity(List<AudioEntity> recordList, String query) {
        String s = Utils.unAccent(query.toLowerCase());
        List<AudioEntity> filteredModelList = new ArrayList<>();

        for (AudioEntity record : recordList) {
            String text = Utils.unAccent(record.getNameAudio().toLowerCase());
            if (text.contains(s)) {
                filteredModelList.add(record);
            }
        }
        return filteredModelList;
    }

    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;


        if (size < sizeMo)
            return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGo)
            return df.format(size / sizeMo) + " Mb";
        else if (size < sizeTerra)
            return df.format(size / sizeGo) + " Gb";

        return "";
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    public static void buildAppRate(Context context, final OnClickButtonListener listener) {
//        AppRate.with(context)
//                .setInstallDays(0) // default 10, 0 means install day.
//                .setLaunchTimes(1) // default 10
//                .setRemindInterval(99) // default 1
//                .setDebug(false) // default false
//                .setOnClickButtonListener(listener)
//                .monitor();
//    }

    public static void deleteAudio(Context context, String path) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{path});
    }

    public static long getDurationFile(String pathStr, Context context) {
        // Uri uri = Uri.parse(pathStr);
        long duration;
        MediaPlayer mp = MediaPlayer.create(context, Uri.parse(pathStr));
        if (mp == null) {
            duration = 0;
        } else {
            duration = mp.getDuration();
        }

        //long timeInMillisec = Long.parseLong(time );
        //long millSecond = Integer.parseInt(timeInMillisec);
        return duration;
    }

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

    public static String readSizeFile(double path) {
        String valueFile = null;

        double Filesize = path / 1024;

        if (Filesize >= 1000) {
            BigDecimal rowOff = new BigDecimal(Filesize / 1024).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            valueFile = rowOff + " Mb";
        } else {
            BigDecimal rowOff = new BigDecimal(Filesize).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            valueFile = rowOff + " Kb";
        }
        return valueFile;
    }

    public static long getProgress(String message, long totalDuration) {
        if (message.contains("speed")) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String tempTime = String.valueOf(matcher.group(1));
                // FLog.d("getProgress: tempTime " + tempTime);
                String[] arrayTime = tempTime.split(":");
                long currentTime =
                        TimeUnit.HOURS.toSeconds(Long.parseLong(arrayTime[0]))
                                + TimeUnit.MINUTES.toSeconds(Long.parseLong(arrayTime[1]))
                                + Long.parseLong(arrayTime[2]);

                int percent = (int) (100 * currentTime / totalDuration);

                //  FLog.d("currentTime -> " + currentTime + "s % -> " + percent);

                return currentTime;
            }
        }
        return 0;
    }

    public static List<AudioEntity> getSongFromDevice(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        List<AudioEntity> mListSong = new ArrayList<>();
        String[] m_data = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATE_MODIFIED
        };

        Cursor c = context.getContentResolver().query(uri, m_data, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (c != null && c.moveToNext()) {
            do {
                String name, album, artist, path, id, audioType, dateModifier;
                String duration;
                int albumId, artistId;

                id = c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID));
                name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                duration = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION));

                albumId = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                dateModifier = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                String imagePath = ContentUris.withAppendedId(ART_CONTENT_URI, albumId).toString();

                AudioEntity audio = new AudioEntity(id, name, artist, album, String.valueOf(duration), path, albumId, imagePath, dateModifier);

                try {

                    if (duration != null && Long.parseLong(duration) > 0) {
                        mListSong.add(audio);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

            } while (c.moveToNext());
        }

        c.close();
        return mListSong;
    }

    public static List<AudioEntity> getAudioConvert(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        List<AudioEntity> mListSong = new ArrayList<>();
        String[] m_data = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATE_MODIFIED
        };

        Cursor c = context.getContentResolver().query(uri, m_data, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (c != null && c.moveToNext()) {
            do {
                String name, album, artist, path, id, audioType, dateModifier;
                String duration;
                int albumId, artistId;

                id = c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID));
                name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                duration = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION));

                albumId = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                dateModifier = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                String imagePath = ContentUris.withAppendedId(ART_CONTENT_URI, albumId).toString();

                AudioEntity audio = new AudioEntity(id, name, artist, album, String.valueOf(duration), path, albumId, imagePath, dateModifier);

                try {

                    if (duration != null && Long.parseLong(duration) > 0 && path.contains("/Converter")) {
                        mListSong.add(audio);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

            } while (c.moveToNext());
        }

        c.close();
        return mListSong;
    }


    public static int getMediaDuration(String filePath) {
        MediaMetadataRetriever metaInfo = new MediaMetadataRetriever();
        int duration = -1;
        try {
            metaInfo.setDataSource(filePath);
            duration = Integer.valueOf(metaInfo.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            metaInfo.release();
            return -1;
        } finally {
            metaInfo.release();
        }
        return duration;
    }

    public static String convertMillisecond(long millisecond) {
        long sec = (millisecond / 1000) % 60;
        long min = (millisecond / (60 * 1000)) % 60;
        long hour = millisecond / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }

        return time;
    }

    public static List<AudioEntity> filterAudioEnity(List<AudioEntity> audioEnities, String query) {
        String s = Utils.unAccent(query.toLowerCase());
        List<AudioEntity> filteredModelList = new ArrayList<>();

        for (AudioEntity audio : audioEnities) {
            String text = Utils.unAccent(audio.getNameAudio().toLowerCase());
            if (text.contains(s)) {
                filteredModelList.add(audio);
            }
        }
        return filteredModelList;
    }


    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }

    public static void closeKeyboard(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
