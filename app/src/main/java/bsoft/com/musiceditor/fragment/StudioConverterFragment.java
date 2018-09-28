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
import bsoft.com.musiceditor.utils.Utils;

public class StudioConverterFragment extends BaseFragment implements AudioAdapter.OnClick {

    private List<AudioEntity> audioEntities = new ArrayList<>();
    private AudioAdapter adapter;
    private RecyclerView rvAudio;


    public static StudioConverterFragment newInstance() {
        Bundle args = new Bundle();
        StudioConverterFragment fragment = new StudioConverterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initViews() {

        audioEntities.clear();
        audioEntities.addAll(Utils.getAudioConvert(getContext()));
        Log.e("xxx", " sizzzzzz" + audioEntities.size());
        adapter = new AudioAdapter(audioEntities, getContext(), this);

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
    public void onLongClick(int index) {

    }
}
