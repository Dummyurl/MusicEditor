package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.model.AudioEntity;

public class StudioRecorderFragment extends BaseFragment {
    private List<AudioEntity> audioEntityList = new ArrayList<>();
    public static StudioRecorderFragment newInstance() {

        Bundle args = new Bundle();

        StudioRecorderFragment fragment = new StudioRecorderFragment();
        fragment.setArguments(args);
        return fragment;
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
