package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bsoft.com.musiceditor.R;

public class CutterStudioFragment extends BaseFragment {

    public static CutterStudioFragment newInstance() {
        
        Bundle args = new Bundle();
        
        CutterStudioFragment fragment = new CutterStudioFragment();
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
