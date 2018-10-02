package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.model.AudioEntity;

public class StudioMergerFragment extends BaseFragment {
    private List<AudioEntity> audioEntityList = new ArrayList<>();
    private List<AudioEntity> listAllAudio = new ArrayList<>();
    private RecyclerView rvRecord;
    private AudioAdapter adapter;

    public static StudioMergerFragment newInstance() {
        Bundle args = new Bundle();
        StudioMergerFragment fragment = new StudioMergerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void beginSearch(String s) {

    }

    @Override
    public void initViews() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_studio_cutter, container, false);
    }
}
