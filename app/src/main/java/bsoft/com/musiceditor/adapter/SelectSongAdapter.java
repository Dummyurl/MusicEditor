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

public class SelectSongAdapter extends RecyclerView.Adapter<SelectSongAdapter.ViewHolder> {
    private List<AudioEntity> songList;
    private Context context;
    private OnClick callback;
    private List<AudioEntity> listSongChecked = new ArrayList<>();

    public SelectSongAdapter(List<AudioEntity> songs, Context context, OnClick callback) {
        this.songList = songs;
        this.callback = callback;
        this.context = context;

    }

    public void setFilter(List<AudioEntity> list) {
        songList = new ArrayList<>();
        songList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectSongAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_select, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioEntity song = songList.get(position);
        holder.tvArtist.setText(song.getNameArtist());
        holder.tvNameSong.setText(song.getNameAudio());
        holder.checkBox.setChecked(song.isCheck());


        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ic_img_ms);
        options.error(R.drawable.ic_img_ms);
        Glide.with(context).load(song.getPathImage()).apply(options).into(holder.ivSong);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public interface OnClick {
        void onClick(int index);

        void onListChecked(List<AudioEntity> listChecked);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSong;
        private TextView tvNameSong, tvArtist;
        private CheckBox checkBox;


        public ViewHolder(View itemView) {
            super(itemView);
            ivSong = itemView.findViewById(R.id.image_song);
            tvNameSong = itemView.findViewById(R.id.name_song);
            tvArtist = itemView.findViewById(R.id.name_artist);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                boolean isExist = false;
                for (AudioEntity audioEntity : listSongChecked) {
                    if (audioEntity.getPath().equals(songList.get(getAdapterPosition()).getPath())) {
                        isExist = true;
                    }
                }

                if (compoundButton.isChecked() && !isExist) {
                    songList.get(getAdapterPosition()).setCheck(true);
                    listSongChecked.add(songList.get(getAdapterPosition()));

                } else if (!compoundButton.isChecked() && isExist) {
                    songList.get(getAdapterPosition()).setCheck(false);
                    listSongChecked.remove(songList.get(getAdapterPosition()));
                }

                callback.onListChecked(listSongChecked);

            });

            itemView.setOnClickListener(view -> {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
            });
        }
    }
}
