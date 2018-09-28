package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.FormatAdapter;
import bsoft.com.musiceditor.adapter.QualityAdapter;

public class RecordFragment extends BaseFragment implements QualityAdapter.OnClick, FormatAdapter.OnClick {
    private RecyclerView rvQuality, rvFormat;
    private QualityAdapter qualityAdapter;
    private FormatAdapter formatAdapter;
    private TextView tvQuality, tvFormat;

    @Override
    public void initViews() {

        initToolbar();

        qualityAdapter = new QualityAdapter(this, false, getContext());
        formatAdapter = new FormatAdapter(this, true, getContext());

        tvQuality = (TextView) findViewById(R.id.tv_quality);
        tvFormat = (TextView) findViewById(R.id.tv_format);

        rvQuality = (RecyclerView) findViewById(R.id.rv_quality);
        rvQuality.setHasFixedSize(true);
        rvQuality.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvQuality.setAdapter(qualityAdapter);

        rvFormat = (RecyclerView) findViewById(R.id.rv_format);
        rvFormat.setHasFixedSize(true);
        rvFormat.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFormat.setAdapter(formatAdapter);

        tvQuality.setText(getString(R.string.quality) + ":");
        tvFormat.setText(getString(R.string.format) + ":");

    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
    }

    public static RecordFragment newInstance() {
        Bundle args = new Bundle();
        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recorder, container, false);
    }

    @Override
    public void onClick(int index) {

    }

    @Override
    public void onItemClick(int index) {

    }
}
