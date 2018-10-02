package bsoft.com.musiceditor.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;

public class StudioRecorderFragment extends BaseFragment implements AudioAdapter.OnClick {
    private List<AudioEntity> audioEntityList = new ArrayList<>();
    private List<AudioEntity> listAllAudio = new ArrayList<>();
    private RecyclerView rvRecord;
    private AudioAdapter adapter;

    public static StudioRecorderFragment newInstance() {
        Bundle args = new Bundle();
        StudioRecorderFragment fragment = new StudioRecorderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getAction() != null) {

                switch (intent.getAction()) {
                    case Keys.STOP_RECORD:
                        updateList();
                        break;
                }
            }
        }
    };

    @Override
    public void initViews() {

        listAllAudio.clear();
        listAllAudio.addAll(Utils.getAudioConvert(getContext(), Keys.DIR_RECORDER));

        audioEntityList.clear();
        audioEntityList.addAll(listAllAudio);
        adapter = new AudioAdapter(audioEntityList, getContext(), this, true);

        rvRecord = (RecyclerView) findViewById(R.id.rv_audio);
        rvRecord.setHasFixedSize(true);
        rvRecord.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecord.setAdapter(adapter);

        getContext().registerReceiver(receiver, new IntentFilter(Keys.STOP_RECORD));

    }


    public void updateList() {

        listAllAudio.clear();
        listAllAudio.addAll(Utils.getAudioConvert(getContext(), Keys.DIR_RECORDER));

        audioEntityList.clear();
        audioEntityList.addAll(listAllAudio);
        adapter.notifyDataSetChanged();
    }

    public void beginSearch(String s) {
        audioEntityList = Utils.filterAudioEntity(listAllAudio, s);
        adapter.setFilter(audioEntityList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_studio_cutter, container, false);
    }

    @Override
    public void onClick(int index) {

    }

    @Override
    public boolean onLongClick(int index) {
        return false;
    }

    @Override
    public void onOptionClick(int index) {

    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(receiver);
        super.onDestroy();
    }
}
