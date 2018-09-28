package bsoft.com.musiceditor.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.FormatAdapter;
import bsoft.com.musiceditor.adapter.QualityAdapter;
import bsoft.com.musiceditor.custom.visualizer.IVisualizerView;
import bsoft.com.musiceditor.custom.visualizer.VisualizerManager;
import bsoft.com.musiceditor.service.RecordService;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;

import static bsoft.com.musiceditor.utils.Keys.START_SERVICE;
import static bsoft.com.musiceditor.utils.Keys.STOP_SERVICE;

public class RecordFragment extends BaseFragment implements QualityAdapter.OnClick, FormatAdapter.OnClick {
    private RecyclerView rvQuality, rvFormat;
    private QualityAdapter qualityAdapter;
    private FormatAdapter formatAdapter;
    private TextView tvQuality, tvFormat, tvTime;
    private String monoSelected = "44100";
    private ImageView ivRecord, ivSaveRecord;
    private int quality[] = new int[]{11000, 16000, 22050, 44100};
    public static Handler mHandler;

    private IVisualizerView iVisualizerView;

    @Override
    public void initViews() {

        initToolbar();

        iVisualizerView = (IVisualizerView) findViewById(R.id.visualizer_fullview);
        VisualizerManager.getInstance().setupView(iVisualizerView);
        if (iVisualizerView != null) {
            iVisualizerView.refreshChanged();
        }

        qualityAdapter = new QualityAdapter(this, false, getContext());
        formatAdapter = new FormatAdapter(this, true, getContext());

        tvQuality = (TextView) findViewById(R.id.tv_quality);
        tvFormat = (TextView) findViewById(R.id.tv_format);
        tvTime = (TextView) findViewById(R.id.tv_time);

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
        ivSaveRecord = (ImageView) findViewById(R.id.iv_save);
        ivRecord = (ImageView) findViewById(R.id.iv_record);

        ivRecord.setOnClickListener(v -> addPermission());
        ivSaveRecord.setOnClickListener(v -> saveRecord());

    }

    private void addPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext()
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext()
                    , Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startRecord();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);

            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startRecord();
        }
    }

    private boolean isRecording = false;

    private void saveRecord() {
        getContext().startService(new Intent(getActivity(), RecordService.class).setAction(STOP_SERVICE));

        isRecording = false;

        tvTime.setText("00:00");

        ivRecord.setImageResource(R.drawable.ic_btn_pause);
        ivSaveRecord.setVisibility(View.INVISIBLE);
        ivSaveRecord.setVisibility(View.INVISIBLE);
    }

    private void startRecord() {
        Log.e("xxx", "start");
        if (!isRecording) {

            isRecording = true;

            ivRecord.setImageResource(R.drawable.ic_btn_play);

            ivSaveRecord.setVisibility(View.VISIBLE);

            File folder = new File(Environment.getExternalStorageDirectory() + Keys.DIR_APP + Keys.DIR_RECORDER);
            if (!folder.exists()) {
                folder.mkdir();
            }

            getContext().startService(new Intent(getActivity(), RecordService.class).setAction(START_SERVICE));

            updateTime();

        }
    }

    private void updateTime() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    VisualizerManager.getInstance().update((double[]) msg.obj);
                } else {
                    tvTime.setText(Utils.convertMillisecond(msg.getData().getLong(Keys.TIME)));
                    ivRecord.setImageResource(R.drawable.ic_btn_play);
                    ivSaveRecord.setVisibility(View.VISIBLE);
                }
            }
        };
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
        qualityAdapter.setSelectBitrate(index);
    }
}
