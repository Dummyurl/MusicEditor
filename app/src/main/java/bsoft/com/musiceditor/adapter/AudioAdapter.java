package bsoft.com.musiceditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import bsoft.com.musiceditor.model.AudioEntity;

import static bsoft.com.musiceditor.utils.Utils.CONVERT_LONG_TO_DATE;
import static bsoft.com.musiceditor.utils.Utils.convertDate;
import static bsoft.com.musiceditor.utils.Utils.convertMillisecond;
import static bsoft.com.musiceditor.utils.Utils.getFileExtension;
import static bsoft.com.musiceditor.utils.Utils.getStringSizeLengthFile;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private List<AudioEntity> audioEntities;
    private Context context;
    private OnClick callback;
    private boolean isStudio = false;

    private List<AudioEntity> listSongChecked = new ArrayList<>();

    public AudioAdapter(List<AudioEntity> audioEntities, Context context, OnClick callback, boolean isStudio) {
        this.audioEntities = audioEntities;
        this.callback = callback;
        this.context = context;
        this.isStudio = isStudio;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (isStudio) {
            View itemView = inflater.inflate(R.layout.item_studio, parent, false);
            return new ViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_audio, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public void setFilter(List<AudioEntity> list) {
        audioEntities = new ArrayList<>();
        audioEntities = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioEntity audioEntity = audioEntities.get(position);
        if (isStudio) {

            holder.tvNameSong.setText(audioEntity.getNameAudio());
            holder.tvNameArtist.setText(convertDate(audioEntity.getDateModifier(), CONVERT_LONG_TO_DATE));
            holder.tvTimeSizeFormat.setText(getStringSizeLengthFile(audioEntity.getSize())
                    + "  " + getFileExtension(audioEntity.getPath())
                    + "  " + convertMillisecond(Long.parseLong(audioEntity.getDuration())));

            RequestOptions options = new RequestOptions();
            options.error(R.drawable.ic_img_ms);
            options.placeholder(R.drawable.ic_img_ms);
            Glide.with(context).load(audioEntity.getPathImage()).apply(options).into(holder.ivAudio);

        } else {

            holder.tvNameSong.setText(audioEntity.getNameAudio());
            holder.tvNameArtist.setText(audioEntity.getNameArtist());
            holder.tvDuration.setText(convertMillisecond(Long.parseLong(audioEntity.getDuration())));

            RequestOptions options = new RequestOptions();
            options.error(R.drawable.ic_img_ms);
            options.placeholder(R.drawable.ic_img_ms);
            Glide.with(context).load(audioEntity.getPathImage()).apply(options).into(holder.ivAudio);

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
            if (isStudio) {
                tvNameSong = itemView.findViewById(R.id.name_song);
                tvNameArtist = itemView.findViewById(R.id.name_artist);
                tvTimeSizeFormat = itemView.findViewById(R.id.tv_size_time_format);
                ivAudio = itemView.findViewById(R.id.image_audio);
                ivMore = itemView.findViewById(R.id.iv_more);
                checkBox = itemView.findViewById(R.id.checkbox);

                ivMore.setOnClickListener(v -> callback.onOptionClick(getAdapterPosition()));
                itemView.setOnLongClickListener(v -> callback.onLongClick(getAdapterPosition()));

                checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    boolean isExist = false;
                    for (AudioEntity song : listSongChecked) {
                        if (song.getPath().equals(audioEntities.get(getAdapterPosition()).getPath())) {
                            isExist = true;
                        }
                    }

                    if (compoundButton.isChecked() && !isExist) {
                        audioEntities.get(getAdapterPosition()).setCheck(true);
                        listSongChecked.add(audioEntities.get(getAdapterPosition()));

                    } else if (!compoundButton.isChecked() && isExist) {
                        audioEntities.get(getAdapterPosition()).setCheck(false);
                        listSongChecked.remove(audioEntities.get(getAdapterPosition()));
                    }

                    callback.onListChecked(listSongChecked);

                });


            } else {
                tvNameSong = itemView.findViewById(R.id.name_song);
                tvNameArtist = itemView.findViewById(R.id.name_artist);
                tvDuration = itemView.findViewById(R.id.duration);
                ivAudio = itemView.findViewById(R.id.image_audio);

                itemView.setOnClickListener(v -> callback.onClick(getAdapterPosition()));

            }
        }
    }

    public interface OnClick {
        void onClick(int index);

        boolean onLongClick(int index);

        void onOptionClick(int index);

        void onListChecked(List<AudioEntity> listChecked);
    }
}
