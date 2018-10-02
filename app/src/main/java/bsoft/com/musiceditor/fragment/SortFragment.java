package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.adapter.SimpleItemTouchHelperCallback;
import bsoft.com.musiceditor.adapter.SortAdapter;
import bsoft.com.musiceditor.listener.IListSongChanged;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Flog;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;

public class SortFragment extends BaseFragment implements IListSongChanged, SortAdapter.OnStartDragListener {
    private List<AudioEntity> audioEntities;
    private RecyclerView rvAudio;
    private SortAdapter audioAdapter;
    private Toolbar toolbar;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper itemTouchHelper;

    public static SortFragment newInstance(Bundle bundle) {
        SortFragment fragment = new SortFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews() {

        ffmpeg = FFmpeg.getInstance(getContext());

        audioEntities = getArguments().getParcelableArrayList(Keys.LIST_SONG);
        audioAdapter = new SortAdapter(audioEntities, getContext(), this, this);

        rvAudio = (RecyclerView) findViewById(R.id.rv_audio);
        rvAudio.setHasFixedSize(true);
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudio.setAdapter(audioAdapter);

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
        toolbar.getMenu().findItem(R.id.item_ads).setOnMenuItemClickListener(item -> mergerAudio());

    }

    private boolean mergerAudio() {
        String command[] = new String[]{};
        return true;
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

    private FFmpeg ffmpeg;

    private void execFFmpegBinary(final String[] command, String path, String title) {
        Log.e("xxx", "cccccccccccccc");
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {

                    Flog.e("xxx", "FAILED with output: " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Flog.e("xxx", "Success " + s);

                }

                @Override
                public void onProgress(String s) {
                    Log.e("xxx", s);

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    Flog.e("xxx", "Success finish ");

                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            Log.e("xxx", "ccccxxxxxxxxxxxxxxxxxxxxxcccccccccc");
        }
    }
}
