package bsoft.com.musiceditor.fragment;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bsoft.com.musiceditor.BuildConfig;
import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.activity.MainActivity;
import bsoft.com.musiceditor.adapter.AudioActionAdapter;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Flog;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;

import static bsoft.com.musiceditor.utils.Utils.deleteAudio;
import static bsoft.com.musiceditor.utils.Utils.getFileExtension;

public class StudioCutterFragment extends BaseFragment implements AudioActionAdapter.OnClick {
    private List<AudioEntity> audioEntities = new ArrayList<>();
    private List<AudioEntity> listAllAudio = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private AudioActionAdapter adapter;
    private RecyclerView rvAudio;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private EditText edtRename;
    private String CHECK_CURRENT_FRAGMENT;
    private int indexOption;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getAction() != null) {

                switch (intent.getAction()) {

                    case Keys.UPDATE_LIST_STUDIO:

                        //updateList();

                        break;
                }
            }
        }
    };

    public static StudioCutterFragment newInstance(Bundle bundle) {
        StudioCutterFragment fragment = new StudioCutterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews() {

        CHECK_CURRENT_FRAGMENT = getArguments().getString(Keys.CHECK_STUDIO_FRAGMENT);

        listAllAudio.clear();
        listAllAudio.addAll(Utils.getAudioConvert(getContext(), CHECK_CURRENT_FRAGMENT));
        audioEntities.clear();
        audioEntities.addAll(listAllAudio);

        Flog.e("xxx", listAllAudio.size() + " " + CHECK_CURRENT_FRAGMENT);

        Collections.reverse(audioEntities);

        adapter = new AudioActionAdapter(audioEntities, this, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvAudio = (RecyclerView) findViewById(R.id.rv_audio);
        rvAudio.setHasFixedSize(true);
        rvAudio.setLayoutManager(linearLayoutManager);
        rvAudio.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvAudio.getContext(),
                linearLayoutManager.getOrientation());

        rvAudio.addItemDecoration(dividerItemDecoration);

        getContext().registerReceiver(receiver, new IntentFilter(Keys.UPDATE_LIST_STUDIO));

    }

    public void beginSearch(String s) {
        audioEntities = Utils.filterAudioEntity(listAllAudio, s);
        Collections.reverse(audioEntities);
        adapter.setFilter(audioEntities);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_studio_cutter, container, false);
    }

//    @Override
//    public void onClick(int index) {
//        indexOption = index;
//        PlaybackFragment playbackFragment = new PlaybackFragment().newInstance(audioEntities.get(indexOption));
//        FragmentTransaction transaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
//        playbackFragment.show(transaction, "dialog_playback");
//    }

    StudioFragment studioFragment;

//    @Override
//    public boolean onLongClick(int index) {
//        //deleteRecord();
////        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.view_container);
////        if (fragment instanceof StudioFragment) {
////            studioFragment = (StudioFragment) fragment;
////
////        }
//        createAction();
//        return true;
//
//    }

    MainActivity context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (MainActivity) context;
    }

    private ActionMode actionMode = null;
    public boolean isActionMode = false;
    public int countItemSelected = 0;
    public boolean isSelectAll = false;


    public void createAction() {
        context.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                isActionMode = true;
                for (AudioEntity audioEntity : audioEntities) {
                    audioEntity.setCheck(false);
                }
                adapter.notifyDataSetChanged();
                actionMode = mode;
                actionMode.setTitle("0");
                context.getMenuInflater().inflate(R.menu.setting_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_delete:
                        actionModeDelete(mode);
                        break;
                    case R.id.item_check_all:
                        selectItem();
                        break;
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                isActionMode = false;
                isSelectAll = false;
                countItemSelected = 0;
                if (mListChecked != null) {
                    mListChecked.clear();
                }
                for (AudioEntity audioEntity : audioEntities) {
                    audioEntity.setCheck(false);
                }
                adapter.notifyDataSetChanged();
                mode.finish();
            }
        });
    }

    public void prepareSelection(View view, int i) {
        if (((CheckBox) view).isChecked()) {
            if (!mListChecked.contains(audioEntities.get(i))) {
                mListChecked.add(audioEntities.get(i));
                countItemSelected = countItemSelected + 1;
                updateCountItemSelected();
            }
        } else {
            if (mListChecked.contains(audioEntities.get(i))) {
                mListChecked.remove(audioEntities.get(i));
                countItemSelected = countItemSelected - 1;
                updateCountItemSelected();
            }
        }
    }

    private void selectItem() {
        if (!isSelectAll) {
            countItemSelected = audioEntities.size();
            isSelectAll = true;
            for (AudioEntity record : audioEntities) {
                record.setCheck(true);
            }
            adapter.notifyDataSetChanged();
            actionMode.setTitle(countItemSelected + "");
            mListChecked.clear();
            mListChecked.addAll(audioEntities);
        } else {
            isSelectAll = false;
            for (AudioEntity audioEntity : audioEntities) {
                audioEntity.setCheck(false);
            }
            mListChecked.clear();
            countItemSelected = 0;
            actionMode.setTitle(countItemSelected + " ");
            actionMode.finish();
        }
    }

    private List<AudioEntity> mListChecked = new ArrayList<>();

    private void actionModeDelete(final ActionMode mode) {
        if (mListChecked.size() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.delete_this_record));
            builder.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {

                settingDeleteRecord();
                isActionMode = false;
                isSelectAll = false;
                adapter.notifyDataSetChanged();
                mode.finish();
            });
            builder.setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void settingDeleteRecord() {
        List<AudioEntity> audios = new ArrayList<>();
        for (AudioEntity audioEntity : audioEntities) {
            if (audioEntity.getPath().toLowerCase().equals(audioEntity.getPath().toLowerCase())) {
                audios.add(audioEntities.get(indexOption));
            }
        }


        if (mListChecked.size() != 0) {

            for (AudioEntity audioEntity : mListChecked) {
                File file = new File(audioEntity.getPath());
                file.delete();
            
                Utils.deleteAudio(getContext(), audioEntity.getPath());

                countItemSelected = countItemSelected - 1;

            }

            updateList();

            updateCountItemSelected();

            adapter.notifyDataSetChanged();
            context.sendBroadcast(new Intent(Keys.UPDATE_DELETE_RECORD));
        }
    }

    public void updateCountItemSelected() {
        if (countItemSelected == 0)
            actionMode.setTitle("0");
        else
            actionMode.setTitle(countItemSelected + "");
    }

    @Override
    public void onClick(int index, boolean isAction) {
        indexOption = index;
        PlaybackFragment playbackFragment = new PlaybackFragment().newInstance(audioEntities.get(indexOption));
        FragmentTransaction transaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
        playbackFragment.show(transaction, "dialog_playback");
    }

    @Override
    public boolean onLongClick(int index, boolean isAction) {
        createAction();
        return true;
    }

    @Override
    public void onOptionClick(int index) {

        indexOption = index;

        showBottomSheet(indexOption);

    }

    @Override
    public void onListChecked(List<AudioEntity> listChecked) {

    }

    private void showBottomSheet(int index) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_option_bottom, null);

        TextView tvTitle = view.findViewById(R.id.btn_title);

        tvTitle.setText(audioEntities.get(index).getNameAudio());

        view.findViewById(R.id.btn_share).setOnClickListener(v -> shareRecord());
        view.findViewById(R.id.btn_delete).setOnClickListener(v -> deleteRecord());
        view.findViewById(R.id.btn_detail).setOnClickListener(v -> detailAudio());
        view.findViewById(R.id.btn_rename).setOnClickListener(v -> renameAudio());
        view.findViewById(R.id.btn_open_file).setOnClickListener(v -> openFileRecord());

        bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void openFileRecord() {
        Uri uri;

        AudioEntity audioEntity = audioEntities.get(indexOption);

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(audioEntity.getPath()));
        } else {
            uri = Uri.fromFile(new File(audioEntity.getPath()));
        }

        intent.setDataAndType((uri), "audio/*");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);

        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
    }

    private void renameAudio() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_rename, null);

        createDialog(view);

        edtRename = view.findViewById(R.id.edtRename);

        view.findViewById(R.id.btnYes).setOnClickListener(v -> {

            if (edtRename.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.file_name_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            String tailFile;

            File currentFile, newFile;

            AudioEntity audioEntity = audioEntities.get(indexOption);

            tailFile = "." + getFileExtension(audioEntity.getPath());

            currentFile = new File(audioEntity.getPath());

            newFile = new File(audioEntity.getPath().replace(audioEntity.getNameAudio(), "") + edtRename.getText().toString().trim() + tailFile);

            if (newFile.exists()) {
                Toast.makeText(getContext(), getString(R.string.name_file_exist), Toast.LENGTH_SHORT).show();

            } else {

                rename(currentFile, newFile);
                renameContentProvider(edtRename.getText().toString().trim(), tailFile, audioEntity);
                updateList();

                Log.e("xxx", "Cccccccccccc");

                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.btnNo).setOnClickListener(v -> alertDialog.dismiss());

        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }

    }


    private boolean rename(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }

    private void renameContentProvider(String newName, String tailFile, AudioEntity audioEntity) {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " = ?", new String[]{audioEntity.getPath()}, null);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, newName);
        values.put(MediaStore.Audio.Media.DATA, audioEntity.getPath().replace(new File(audioEntity.getPath()).getName(), "") + newName + tailFile);

        File f = new File(audioEntity.getPath());

        try {
            int result = contentResolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, MediaStore.Audio.Media.DATA + " = ?", new String[]{f.getAbsolutePath()});

        } catch (Exception e) {

            Toast.makeText(getContext(), getString(R.string.fail_rename), Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }

    }

    private void createDialog(View view) {
        builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void detailAudio() {
        AudioEntity audioEntity = audioEntities.get(indexOption);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_detail, null);

        createDialog(view);

        TextView tvTitle, tvFilePath, tvDuration, tvDateTime, tvSize;

        view.findViewById(R.id.btn_yes_detail).setOnClickListener(v -> dismissDetail());

        tvSize = view.findViewById(R.id.tvSize);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvFilePath = view.findViewById(R.id.tvFilePath);
        tvDuration = view.findViewById(R.id.tvDuaration);
        tvDateTime = view.findViewById(R.id.tvDateTime);

        tvTitle.setText(getResources().getString(R.string.title_audio) + ": " + audioEntity.getNameAudio());
        tvSize.setText(getString(R.string.size) + ": " + Utils.getStringSizeLengthFile(audioEntity.getSize()));
        tvDateTime.setText(getString(R.string.date_time) + ": " + Utils.convertDate(String.valueOf(audioEntity.getDateModifier()), "dd/MM/yyyy hh:mm:ss"));
        tvFilePath.setText(getResources().getString(R.string.path) + ": " + audioEntity.getPath());
        tvDuration.setText(getResources().getString(R.string.duration) + ": " + Utils.convertMillisecond(Long.parseLong(audioEntity.getDuration())));

        bottomSheetDialog.dismiss();

    }

    @Override
    public void onDetach() {
        try {
            getContext().unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDetach();
    }

    private void dismissDetail() {
        alertDialog.dismiss();
    }

    private void updateList() {

        listAllAudio.clear();
        listAllAudio.addAll(Utils.getAudioConvert(getContext(), CHECK_CURRENT_FRAGMENT));

        audioEntities.clear();
        audioEntities.addAll(listAllAudio);

        Collections.reverse(audioEntities);

        adapter.notifyDataSetChanged();

        Utils.closeKeyboard(getActivity());

        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
    }

    private void deleteRecord() {
        AudioEntity audioEntity = audioEntities.get(indexOption);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.delete_this_record));
        builder.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
            File f = new File(audioEntity.getPath());
            f.delete();
            deleteAudio(getContext(), audioEntity.getPath());
            updateList();
        });
        builder.setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shareRecord() {
        Uri uri;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(audioEntities.get(indexOption).getPath()));
        } else {

            uri = Uri.fromFile(new File(audioEntities.get(indexOption).getPath()));
        }

        intent.setType("audio/*");

        intent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(intent, "Share audio to.."));

        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
    }
}
