package bsoft.com.musiceditor.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.FormatAdapter;
import bsoft.com.musiceditor.adapter.QualityAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Flog;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.SharedPrefs;
import bsoft.com.musiceditor.utils.Utils;

public class ConvertFragment extends BaseFragment implements FormatAdapter.OnClick, QualityAdapter.OnClick, SeekBar.OnSeekBarChangeListener {
    private RecyclerView rvBitrate, rvFormat;
    private FormatAdapter formatAdapter;
    private QualityAdapter bitrateAdapter;
    private TextView tvTitleInputFormat, tvInputFormat, tvStartTime, tvEndTime, tvOutputFormat, tvBitrate, tvArtist, tvSong;
    private AudioEntity audioEntity;
    private ImageView ivThumb, ivPlay;
    private SeekBar seekBar;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    private String bitrate[] = {"128k", "160k", "192k", "256k", "320k"};
    private String bitrateSelected = "128k";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
    private String formatSelected = ".mp3";
    private String format[] = {".mp3", ".wav", ".m4a", ".aac"};
    private FFmpeg ffmpeg;
    private boolean isSuccessConvert = false;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (mediaPlayer == null) {
                return;

            } else {
                tvStartTime.setText(Utils.convertMillisecond(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }
    };

    public static ConvertFragment newInstance(Bundle bundle) {
        ConvertFragment fragment = new ConvertFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews() {

        if (getArguments() == null) {
            getFragmentManager().popBackStack();

        } else {
            audioEntity = getArguments().getParcelable(Utils.AUDIO_ENTITY);
        }

        ffmpeg = FFmpeg.getInstance(getContext());
        formatAdapter = new FormatAdapter(this, false, getContext());
        bitrateAdapter = new QualityAdapter(this, true, getContext());

        rvFormat = (RecyclerView) findViewById(R.id.rv_format);
        rvFormat.setHasFixedSize(true);
        rvFormat.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFormat.setAdapter(formatAdapter);

        rvBitrate = (RecyclerView) findViewById(R.id.rv_bitrate);
        rvBitrate.setHasFixedSize(true);
        rvBitrate.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvBitrate.setAdapter(bitrateAdapter);

        tvTitleInputFormat = (TextView) findViewById(R.id.tv_title_input_format);
        tvInputFormat = (TextView) findViewById(R.id.tv_input_format);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        tvOutputFormat = (TextView) findViewById(R.id.tv_output_format);
        tvBitrate = (TextView) findViewById(R.id.tv_bitrate);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivThumb = (ImageView) findViewById(R.id.iv_thumb_music);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvSong = (TextView) findViewById(R.id.tv_song);
        seekBar = (SeekBar) findViewById(R.id.seekbar);

        tvTitleInputFormat.setText(getString(R.string.input_format) + ":");
        tvOutputFormat.setText(getString(R.string.output_format) + ":");
        tvBitrate.setText(getString(R.string.bitrate) + ":");

        tvSong.setText(audioEntity.getNameAudio());
        tvArtist.setText(audioEntity.getNameArtist());
        tvStartTime.setText("00:00");
        tvEndTime.setText(Utils.convertMillisecond(Long.parseLong(audioEntity.getDuration())));

        Glide.with(getContext()).load(audioEntity.getPathImage()).into(ivThumb);

        initToolbar();
        initActions();
        loadFFMpegBinary();

    }

    private void execFFmpegBinary(final String[] command, String path, String title) {
        Log.e("xxx", "cccccccccccccc");
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    isSuccessConvert = false;
                    Flog.e("xxx", "FAILED with output: " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Flog.e("xxx", "Success " + s);
                    isSuccessConvert = true;

                }

                @Override
                public void onProgress(String s) {
                    Log.e("xxx", s);

                    int durationFile = (int) Utils.getProgress(s, Long.parseLong(audioEntity.getDuration()) / 1000);
                    Log.e("xxx", durationFile + "___" + Long.parseLong(audioEntity.getDuration()) / 1000);
                    float percent = durationFile / (Float.parseFloat(audioEntity.getDuration()) / 1000);
                    if (progressDialog != null) {
                        Log.e("xxx", "cxxxxxxxxxxxxxxxxx " + percent);
                        progressDialog.setProgress((int) (percent * 100));
                    }
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    Flog.e("xxx", "Success finish ");
                    if (isSuccessConvert) {

                        addFileToContentProvider(getContext(), path, title);

                        progressDialog.setProgress(100);

                        new Handler().postDelayed(() -> {
                            if (progressDialog == null) {
                                return;
                            } else {
                                progressDialog.dismiss();
                            }
                        }, 500);

                        Toast.makeText(getContext(), getString(R.string.create_file) + ": " + path, Toast.LENGTH_SHORT).show();

                    } else {
                        if (progressDialog == null) {
                            return;
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            Log.e("xxx", "ccccxxxxxxxxxxxxxxxxxxxxxcccccccccc");
        }
    }

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private EditText edtNameFile;

    private void createDialog(View view) {
        builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void dialogSelectLocalSaveFile() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_save_file, null);

        createDialog(view);

        view.findViewById(R.id.btn_local_ok).setOnClickListener(v -> convertAudio());
        view.findViewById(R.id.btn_local_cancel).setOnClickListener(v -> cancelConvertAudio());

        edtNameFile = view.findViewById(R.id.edt_name_file);
        edtNameFile.setText("AC_" + simpleDateFormat.format(System.currentTimeMillis()));

        playAudio();

    }

    private void cancelConvertAudio() {
        if (ffmpeg.isFFmpegCommandRunning()) {
            ffmpeg.killRunningProcesses();
        }

        new File(path).delete();

    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        stopPlaying();
        super.onDestroy();
    }

    private void initActions() {
        seekBar.setOnSeekBarChangeListener(this);
        ivPlay.setOnClickListener(v -> playAudio());
        findViewById(R.id.view_seekbar).setOnClickListener(view -> {});
        findViewById(R.id.iv_convert).setOnClickListener(v -> dialogSelectLocalSaveFile());
    }

    private ProgressDialog progressDialog;

    private void initDialogProgress() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(getString(R.string.decoding) + "...");
        progressDialog.setProgress(0);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        progressDialog.show();
    }

    private boolean addFileToContentProvider(Context context, String path, String title) {
        File f = new File(path);
        if (f.exists()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
            values.put(MediaStore.Audio.Media.TITLE, title);
            values.put(MediaStore.Audio.Media.ARTIST, "<Unknow>");
            values.put(MediaStore.Audio.Media.ALBUM, "<Unknow>");
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
            values.put(MediaStore.Audio.Media.DATA, path);
            values.put(MediaStore.Audio.Media.DURATION, Utils.getMediaDuration(path));
            values.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis());

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver
                    .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " = ?", new String[]{path}, null);


            if (cursor != null && cursor.getCount() > 0) {
                int result = contentResolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, MediaStore.Audio.Media.DATA + " = ?", new String[]{path});
                Flog.d("LOADDDDDDDDDDDĐ Update media " + result);

                cursor.close();
            } else {
                Flog.e("LOADDDDDDDDDDDĐ Add media");
                Uri newUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                Flog.e(newUri + "");

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
            }
            return true;
        } else {
            return false;
        }
    }

    private String path;

    private void convertAudio() {

        alertDialog.dismiss();

        String titleFile, outPathFile;

        titleFile = edtNameFile.getText().toString().trim();

        if (titleFile.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.file_name_empty), Toast.LENGTH_SHORT).show();
            return;
        } else {

            path = Environment.getExternalStorageDirectory().getAbsolutePath() + Keys.DIR_APP + Keys.DIR_CONVERTER + "/";

            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            path = path + titleFile + formatSelected;

        }

        String command[] = new String[]{"-i", audioEntity.getPath(), "-b:a", bitrateSelected, "-metadata", "title=" + titleFile, "-metadata", "artist=" + "<Unknow>", "-metadata", "album=" + "<Unknow>", path};

        initDialogProgress();

        execFFmpegBinary(command, path, titleFile);
    }


    private void playAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            ivPlay.setImageResource(R.drawable.ic_pause_black_24dp);

            try {
                mediaPlayer.setDataSource(audioEntity.getPath());
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
                mediaPlayer.setOnCompletionListener(mp -> stopPlaying());

                seekBar.setMax(mediaPlayer.getDuration());

                updateTimePlay();

            } catch (IOException e) {

                Toast.makeText(getContext(), getString(R.string.can_not_play_this_file), Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        } else {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();

                ivPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);

            } else {

                mediaPlayer.start();

                ivPlay.setImageResource(R.drawable.ic_pause_black_24dp);

            }
        }
    }

    private void updateTimePlay() {
        if (handler == null) {
            handler = new Handler();
        }

        handler.postDelayed(runnable, 500);
    }

    private void stopPlaying() {
        if (mediaPlayer == null) {
            return;

        } else {

            handler.removeCallbacksAndMessages(null);

            seekBar.setProgress(0);

            tvStartTime.setText("00:00");

            ivPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(getString(R.string.converter));
        simpleDateFormat.format(System.currentTimeMillis());
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_convert, container, false);
    }


    @Override
    public void onClick(int index) {
        formatAdapter.setIndexSelected(index);
        formatSelected = format[index];
    }

    @Override
    public void onItemClick(int index) {
        bitrateAdapter.setSelectBitrate(index, true);
        bitrateSelected = bitrate[index];
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaPlayer == null) {
            return;
        } else {
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }
}
