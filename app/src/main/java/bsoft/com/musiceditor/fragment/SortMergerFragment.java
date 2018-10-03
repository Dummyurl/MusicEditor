package bsoft.com.musiceditor.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.SimpleItemTouchHelperCallback;
import bsoft.com.musiceditor.adapter.SortAdapter;
import bsoft.com.musiceditor.listener.IListSongChanged;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Flog;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;

import static bsoft.com.musiceditor.utils.Utils.FORMAT_MP3;
import static bsoft.com.musiceditor.utils.Utils.deleteAudio;

public class SortMergerFragment extends BaseFragment implements IListSongChanged, SortAdapter.OnStartDragListener {
    private List<AudioEntity> audioEntities = new ArrayList<>();
    private RecyclerView rvAudio;
    private SortAdapter audioAdapter;
    private Toolbar toolbar;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper itemTouchHelper;
    private FFmpeg ffmpeg;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private EditText edtNameFile;
    private long duration;
    private ProgressDialog progressDialog;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
    private String path;
    private boolean isCancelSaveFile = false;
    private boolean isSuccess = false;

    public static SortMergerFragment newInstance(Bundle bundle) {
        SortMergerFragment fragment = new SortMergerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews() {

        ffmpeg = FFmpeg.getInstance(getContext());

        duration = getArguments().getLong(Keys.DURATION);
        audioEntities.clear();
        audioEntities.addAll(getArguments().getParcelableArrayList(Keys.LIST_SONG));
        audioAdapter = new SortAdapter(audioEntities, getContext(), this, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvAudio = (RecyclerView) findViewById(R.id.rv_audio);
        rvAudio.setHasFixedSize(true);
        rvAudio.setLayoutManager(linearLayoutManager);
        rvAudio.setAdapter(audioAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvAudio.getContext(),
                linearLayoutManager.getOrientation());

        rvAudio.addItemDecoration(dividerItemDecoration);

        callback = new SimpleItemTouchHelperCallback(audioAdapter);

        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvAudio);

        initToolbar();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.sort);
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
        toolbar.inflateMenu(R.menu.menu_merger);
        toolbar.getMenu().findItem(R.id.item_ads).setOnMenuItemClickListener(item -> dialogSelectLocalSaveFile());
    }

    public static void appendVideoLog(String text) {
        if (!Keys.TEMP_DIRECTORY.exists()) {
            Keys.TEMP_DIRECTORY.mkdirs();
        }
        File logFile = new File(Keys.TEMP_DIRECTORY, "video.txt");
        //Log.d("FFMPEG", "File append " + text);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }


    private void initDialogProgress() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(getString(R.string.decoding) + "...");
        progressDialog.setProgress(0);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> cancelMerger());
        progressDialog.show();
    }

    private void cancelMerger() {
        isCancelSaveFile = true;
        ffmpeg.killRunningProcesses();
        new File(path).delete();
    }

    private boolean mergerAudio() {

        String nameFile = edtNameFile.getText().toString().trim();
        String title = nameFile + FORMAT_MP3;

        if (!nameFile.isEmpty()) {

            path = Environment.getExternalStorageDirectory().getAbsolutePath() + Keys.DIR_APP + Keys.DIR_MERGER + "/";

            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
            }

            path = path + title;

            if (new File(path).exists()) {
                Toast.makeText(getContext(), getString(R.string.name_file_exist), Toast.LENGTH_SHORT).show();

            } else {

                isCancelSaveFile = false;

                initDialogProgress();

                // delete last list path file
                new File(Keys.TEMP_DIRECTORY, "video.txt").delete();

                // add list path file to file txt
                for (AudioEntity audioEntity : audioEntities) {
                    appendVideoLog(String.format("file '%s'", audioEntity.getPath()));
                }

                File listFile = new File(Keys.TEMP_DIRECTORY, "video.txt");

                String command[] = new String[]{"-f", "concat", "-safe", "0", "-i", listFile.getAbsolutePath(), "-c", "copy", path};

                execFFmpegBinary(command, path, title);

            }
        } else {
            Toast.makeText(getContext(), getString(R.string.file_name_empty), Toast.LENGTH_SHORT).show();
        }

        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        return true;

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
                Flog.e("LOADDDDDDDDDDDĐ Update media " + result);

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sort, container, false);
    }

    @Override
    public void onNoteListChanged(List<AudioEntity> audioEntities) {

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    private void createDialog(View view) {
        builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean dialogSelectLocalSaveFile() {
        if (audioEntities.size() >= 2) {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_save_file, null);

            createDialog(view);

            view.findViewById(R.id.btn_local_ok).setOnClickListener(v -> mergerAudio());
            view.findViewById(R.id.btn_local_cancel).setOnClickListener(v -> alertDialog.dismiss());

            edtNameFile = view.findViewById(R.id.edt_name_file);
            edtNameFile.setText("AM_" + simpleDateFormat.format(System.currentTimeMillis()));

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.error));
            builder.setMessage(getString(R.string.you_need_to_have));
            builder.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                dialog.dismiss();
                getFragmentManager().popBackStack();
            });
            builder.setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        return true;
    }

    private void execFFmpegBinary(final String[] command, String path, String title) {
        Flog.e("xxx", "cccccccccccccc");
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    isSuccess = false;
                    Flog.e("xxx", "FAILED with output: " + s);
                }

                @Override
                public void onSuccess(String s) {
                    isSuccess = true;
                    Flog.e("xxx", "Success " + s);

                }

                @Override
                public void onProgress(String s) {
                    Flog.e("xxx", s);

                    int durationFile = (int) Utils.getProgress(s, duration / 1000);
                    Flog.e("xxx", durationFile + "___" + duration / 1000);
                    float percent = durationFile / (float) (duration / 1000);
                    if (progressDialog != null) {
                        Flog.e("xxx", "cxxxxxxxxxxxxxxxxx " + percent);
                        progressDialog.setProgress((int) (percent * 100));
                    }
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    Flog.e("xxx", "Success finish ");
                    if (isCancelSaveFile) {
                        return;

                    } else {

                        if (isSuccess) {
                            isSuccess = false;

                            addFileToContentProvider(getContext(), path, title);

                            Toast.makeText(getContext(), getString(R.string.create_file) + ": " + path, Toast.LENGTH_SHORT).show();

                            progressDialog.setProgress(100);

                            new Handler().postDelayed(() -> progressDialog.dismiss(), 500);

                            for (Fragment fragment : getFragmentManager().getFragments()) {
                                if (fragment != null) {
                                    getFragmentManager().beginTransaction().remove(fragment).commit();
                                }
                            }

                            getContext().sendBroadcast(new Intent(Keys.CLEAR_LIST_AUDIO_MERGER));

                        } else {

                            Toast.makeText(getContext(), getString(R.string.alert_title_failure), Toast.LENGTH_SHORT).show();

                            getFragmentManager().popBackStack();

                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            Flog.e("xxx", "ccccxxxxxxxxxxxxxxxxxxxxxcccccccccc");
        }
    }
}
