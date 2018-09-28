package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Utils;

public class ListAudioFragment extends BaseFragment implements AudioAdapter.OnClick {
    private RecyclerView rvAudio;
    private AudioAdapter adapter;
    private ArrayList<AudioEntity> audioEntityList;

    public static ListAudioFragment newInstance() {

        Bundle args = new Bundle();

        ListAudioFragment fragment = new ListAudioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_audio, container, false);
    }

    @Override
    public void initViews() {

        initToolbar();

        audioEntityList = new ArrayList<>();
        audioEntityList.clear();
        audioEntityList.addAll(Utils.getSongFromDevice(getContext().getApplicationContext()));

        adapter = new AudioAdapter(audioEntityList, getContext(), this);

        rvAudio = (RecyclerView) findViewById(R.id.rv_audio);
        rvAudio.setHasFixedSize(true);
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudio.setAdapter(adapter);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.converter);
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
    }

    @Override
    public void onClick(int index) {

        AudioEntity audioEntity = audioEntityList.get(index);

        Bundle bundle = new Bundle();

        bundle.putParcelable(Utils.AUDIO_ENTITY, audioEntity);

        getFragmentManager().beginTransaction()
                .add(R.id.view_container, ConvertFragment.newInstance(bundle))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLongClick(int index) {

    }
}
