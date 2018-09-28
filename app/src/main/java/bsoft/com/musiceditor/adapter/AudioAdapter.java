package bsoft.com.musiceditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Utils;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private List<AudioEntity> audioEntities;
    private Context context;
    private OnClick callback;

    public AudioAdapter(List<AudioEntity> audioEntities, Context context, OnClick callback) {
        this.audioEntities = audioEntities;
        this.callback = callback;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_audio, parent, false);
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
        holder.tvNameArtist.setText(audioEntity.getNameArtist());
        holder.tvDuration.setText(Utils.convertMillisecond(Long.parseLong(audioEntity.getDuration())));

        RequestOptions options = new RequestOptions();
        options.error(R.drawable.ic_img_ms);
        Glide.with(context).load(audioEntity.getPathImage()).apply(options).into(holder.ivAudio);
    }

    @Override
    public int getItemCount() {
        return audioEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameSong, tvNameArtist, tvDuration;
        private ImageView ivAudio;
        private ImageView ivMore, ivDefault;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNameSong = itemView.findViewById(R.id.name_song);
            tvNameArtist = itemView.findViewById(R.id.name_artist);
            tvDuration = itemView.findViewById(R.id.duration);
            ivAudio = itemView.findViewById(R.id.image_audio);

            itemView.setOnClickListener(v -> callback.onClick(getAdapterPosition()));

        }
    }

    public interface OnClick {
        void onClick(int index);

        void onLongClick(int index);
    }
}
