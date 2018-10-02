package bsoft.com.musiceditor.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.MenuItem;
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
import static bsoft.com.musiceditor.utils.Keys.STOP_RECORD;
import static bsoft.com.musiceditor.utils.Keys.STOP_SERVICE;

public class RecorderFragment extends BaseFragment implements QualityAdapter.OnClick, FormatAdapter.OnClick {
    private RecyclerView rvQuality, rvFormat;
    private QualityAdapter qualityAdapter;
    private FormatAdapter formatAdapter;
    private TextView tvQuality, tvFormat, tvTime;
    private String monoSelected = "44100";
    private ImageView ivRecord, ivSaveRecord;
    private int quality[] = new int[]{11000, 16000, 22050, 44100};
    private String aFormat[] = new String[]{".mp3", ".wav"};
    public static Handler mHandler;
    public static int sQuality = 44100;
    public static String sFormat = ".mp3";
    private boolean isRecording = false;
    private IVisualizerView iVisualizerView;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getAction() != null) {

                switch (intent.getAction()) {
                    case STOP_RECORD:
                        ivRecord.setImageResource(R.drawable.ic_play_arrow_white);
                        break;
                }

            }
        }
    };

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

        getContext().registerReceiver(receiver, new IntentFilter(STOP_RECORD));

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

    private void saveRecord() {
        getContext().startService(new Intent(getActivity(), RecordService.class).setAction(STOP_SERVICE));

        isRecording = false;

        tvTime.setText("00:00");

        ivSaveRecord.setVisibility(View.INVISIBLE);
        ivSaveRecord.setVisibility(View.INVISIBLE);
        ivRecord.setImageResource(R.drawable.ic_play_arrow_white);

        mHandler.removeCallbacksAndMessages(null);
    }

    private void startRecord() {
        if (isRecording) {
            return;

        } else {

            File folder = new File(Environment.getExternalStorageDirectory() + Keys.DIR_APP + Keys.DIR_RECORDER);
            if (!folder.exists()) {
                folder.mkdir();
            }

            getContext().startService(new Intent(getActivity(), RecordService.class).setAction(START_SERVICE));

            updateTime();

            ivRecord.setImageResource(R.drawable.ic_pause_white);

            ivSaveRecord.setVisibility(View.VISIBLE);

            isRecording = true;

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
                    ivRecord.setImageResource(R.drawable.ic_pause_white);
                    ivSaveRecord.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
        toolbar.inflateMenu(R.menu.menu_save);
        toolbar.getMenu().findItem(R.id.item_folder).setOnMenuItemClickListener(menuItem -> openStudio());
    }

    private boolean openStudio() {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.CHECK_OPEN_STUDIO, Keys.FROM_RECORDER);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.animation_left_to_right, R.anim.animation_right_to_left, R.anim.animation_left_to_right, R.anim.animation_right_to_left)
                .add(R.id.view_container, StudioFragment.newInstance(bundle))
                .addToBackStack(null)
                .commit();
        return true;
    }

    public static RecorderFragment newInstance() {
        Bundle args = new Bundle();
        RecorderFragment fragment = new RecorderFragment();
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
        if (isRecording) {
            return;

        } else {
            sFormat = aFormat[index];
            formatAdapter.setIndexSelected(index);
        }
    }

    @Override
    public void onItemClick(int index) {
        if (isRecording) {
            return;
        } else {
            sQuality = quality[index];
            qualityAdapter.setSelectBitrate(index, false);
        }
    }
}
