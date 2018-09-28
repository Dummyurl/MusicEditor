package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bsoft.com.musiceditor.R;

public class RecorderStudioFragment extends BaseFragment {

    public static RecorderStudioFragment newInstance() {
        
        Bundle args = new Bundle();
        
        RecorderStudioFragment fragment = new RecorderStudioFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void initViews() {
        
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_studio_cutter,container,false);
    }
}