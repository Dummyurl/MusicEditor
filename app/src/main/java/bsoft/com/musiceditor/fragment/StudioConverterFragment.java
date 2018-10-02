package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;

public class StudioConverterFragment extends BaseFragment implements AudioAdapter.OnClick {

    private List<AudioEntity> audioEntities = new ArrayList<>();
    private List<AudioEntity> listAllAudio = new ArrayList<>();
    private AudioAdapter adapter;
    private RecyclerView rvAudio;


    public static StudioConverterFragment newInstance() {
        Bundle args = new Bundle();
        StudioConverterFragment fragment = new StudioConverterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void beginSearch(String s) {
        audioEntities = Utils.filterAudioEnity(listAllAudio, s);
        adapter.setFilter(audioEntities);
    }

    @Override
    public void initViews() {

        listAllAudio.clear();
        listAllAudio.addAll(Utils.getAudioConvert(getContext(), Keys.DIR_CONVERTER));

        audioEntities.clear();
        audioEntities.addAll(listAllAudio);
        adapter = new AudioAdapter(audioEntities, getContext(), this, true);

        rvAudio = (RecyclerView) findViewById(R.id.rv_audio);
        rvAudio.setHasFixedSize(true);
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudio.setAdapter(adapter);

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
}
