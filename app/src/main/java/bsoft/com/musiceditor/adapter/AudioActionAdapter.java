package bsoft.com.musiceditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.fragment.StudioCutterFragment;
import bsoft.com.musiceditor.model.AudioEntity;

import static bsoft.com.musiceditor.utils.Utils.CONVERT_LONG_TO_DATE;
import static bsoft.com.musiceditor.utils.Utils.convertDate;
import static bsoft.com.musiceditor.utils.Utils.convertMillisecond;
import static bsoft.com.musiceditor.utils.Utils.getFileExtension;
import static bsoft.com.musiceditor.utils.Utils.getStringSizeLengthFile;

public class AudioActionAdapter extends RecyclerView.Adapter<AudioActionAdapter.ViewHolder> {

    private List<AudioEntity> audioEntities;
    private StudioCutterFragment context;
    private OnClick callback;
    private boolean isStudio = false;

    private List<AudioEntity> listSongChecked = new ArrayList<>();

    public AudioActionAdapter(List<AudioEntity> audioEntities, StudioCutterFragment context, OnClick callback) {
        this.audioEntities = audioEntities;
        this.callback = callback;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.item_studio, parent, false);
        return new ViewHolder(itemView);

    }

    public void setFilter(List<AudioEntity> list) {
        audioEntities = new ArrayList<>();
        audioEntities = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioEntity audioEntity = audioEntities.get(position);

        holder.tvNameSong.setText(audioEntity.getNameAudio());
        holder.tvNameArtist.setText(convertDate(audioEntity.getDateModifier(), CONVERT_LONG_TO_DATE));
        holder.tvTimeSizeFormat.setText(getStringSizeLengthFile(audioEntity.getSize())
                + "  " + getFileExtension(audioEntity.getPath())
                + "  " + convertMillisecond(Long.parseLong(audioEntity.getDuration())));

        RequestOptions options = new RequestOptions();
        options.error(R.drawable.ic_img_ms);
        options.placeholder(R.drawable.ic_img_ms);
        Glide.with(context).load(audioEntity.getPathImage()).apply(options).into(holder.ivAudio);

        // action mode
        if (context.isActionMode) {
            if (context.isSelectAll) {
                holder.checkBox.setChecked(audioEntities.get(position).isCheck());
            }
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.ivMore.setVisibility(View.GONE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.ivMore.setVisibility(View.VISIBLE);
        }

    }



    @Override
    public int getItemCount() {
        return audioEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameSong, tvNameArtist, tvDuration, tvTimeSizeFormat;
        private ImageView ivAudio;
        private ImageView ivMore, ivDefault;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNameSong = itemView.findViewById(R.id.name_song);
            tvNameArtist = itemView.findViewById(R.id.name_artist);
            tvTimeSizeFormat = itemView.findViewById(R.id.tv_size_time_format);
            ivAudio = itemView.findViewById(R.id.image_audio);
            ivMore = itemView.findViewById(R.id.iv_more);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener((compoundButton, b) -> {

                if (context.isActionMode) {
                    AudioEntity record = audioEntities.get(getAdapterPosition());
                    if (b) {

                        audioEntities.get(getAdapterPosition()).setCheck(true);

                        boolean isAll = true;

                        for (AudioEntity record1 : audioEntities) {
                            if (!record1.isCheck()) {
                                isAll = false;
                            }
                        }

                        if (isAll) context.isSelectAll = true;

                    } else {
                        context.isSelectAll = false;
                        audioEntities.get(getAdapterPosition()).setCheck(false);
                    }
                    context.prepareSelection(checkBox, getAdapterPosition());
                }
            });

            ivMore.setOnClickListener(view -> callback.onOptionClick(getAdapterPosition()));
            itemView.setOnClickListener(view -> {
                if (context.isActionMode) {

                    AudioEntity record = audioEntities.get(getAdapterPosition());

                    context.isSelectAll = false;

                    if (record.isCheck()) {
                        record.setCheck(false);
                        checkBox.setChecked(record.isCheck());
                    } else {
                        record.setCheck(true);
                        checkBox.setChecked(record.isCheck());
                    }

                    context.prepareSelection(checkBox, getAdapterPosition());

                } else {
                    callback.onClick(getAdapterPosition(), false);
                }
                context.isSelectAll = false;
            });
            itemView.setOnLongClickListener(view -> {
                if (context.isActionMode) {
                    AudioEntity record = audioEntities.get(getAdapterPosition());
                    context.isSelectAll = false;
                    if (record.isCheck()) {
                        record.setCheck(false);
                        checkBox.setChecked(record.isCheck());
                    } else {
                        record.setCheck(true);
                        checkBox.setChecked(record.isCheck());
                    }
                    context.prepareSelection(checkBox, getAdapterPosition());
                    context.isSelectAll = false;
                } else {
                    callback.onLongClick(getAdapterPosition(), false);
                }

                return true;
            });
        }
    }

    public interface OnClick {
        void onClick(int index,boolean isAction);

        boolean onLongClick(int index,boolean isAction);

        void onOptionClick(int index);

        void onListChecked(List<AudioEntity> listChecked);
    }
}
