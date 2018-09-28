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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.FormatAdapter;
import bsoft.com.musiceditor.adapter.QualityAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Utils;

public class ConvertFragment extends BaseFragment implements FormatAdapter.OnClick, QualityAdapter.OnClick {
    private RecyclerView rvBitrate, rvFormat;
    private FormatAdapter formatAdapter;
    private QualityAdapter bitrateAdapter;
    private TextView tvTitleInputFormat, tvInputFormat, tvStartTime, tvEndTime, tvOutputFormat, tvBitrate, tvArtist, tvSong;
    private AudioEntity audioEntity;
    private ImageView ivThumb;

    public static ConvertFragment newInstance(Bundle bundle) {
        ConvertFragment fragment = new ConvertFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews() {

        if (getArguments() == null) {
            getFragmentManager().popBackStack();

        } else {
            audioEntity = getArguments().getParcelable(Utils.AUDIO_ENTITY);
        }

        initToolbar();
        initActions();

        formatAdapter = new FormatAdapter(this, false, getContext());
        bitrateAdapter = new QualityAdapter(this, true, getContext());

        rvFormat = (RecyclerView) findViewById(R.id.rv_format);
        rvFormat.setHasFixedSize(true);
        rvFormat.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFormat.setAdapter(formatAdapter);

        rvBitrate = (RecyclerView) findViewById(R.id.rv_bitrate);
        rvBitrate.setHasFixedSize(true);
        rvBitrate.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvBitrate.setAdapter(bitrateAdapter);

        tvTitleInputFormat = (TextView) findViewById(R.id.tv_title_input_format);
        tvInputFormat = (TextView) findViewById(R.id.tv_input_format);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        tvOutputFormat = (TextView) findViewById(R.id.tv_output_format);
        tvBitrate = (TextView) findViewById(R.id.tv_bitrate);
        ivThumb = (ImageView) findViewById(R.id.iv_thumb_music);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvSong = (TextView) findViewById(R.id.tv_song);

        tvTitleInputFormat.setText(getString(R.string.input_format) + ":");
        tvOutputFormat.setText(getString(R.string.output_format) + ":");
        tvBitrate.setText(getString(R.string.bitrate) + ":");

        tvSong.setText(audioEntity.getNameAudio());
        tvArtist.setText(audioEntity.getNameArtist());
        tvStartTime.setText("00:00");
        tvEndTime.setText(Utils.convertMillisecond(Long.parseLong(audioEntity.getDuration())));

        RequestOptions options = new RequestOptions();
        options.error(R.drawable.ic_img_ms);
        options.centerCrop();
        Glide.with(getContext()).load(audioEntity.getPathImage()).into(ivThumb);

    }

    private void initActions() {
        findViewById(R.id.view_seekbar).setOnClickListener(view -> {
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(getString(R.string.converter));
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_convert, container, false);
    }

    @Override
    public void onClick(int index) {
        formatAdapter.setIndexSelected(index);
    }

    @Override
    public void onItemClick(int index) {
        bitrateAdapter.setSelectBitrate(index);
    }
}
