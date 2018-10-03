package bsoft.com.musiceditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.listener.IItemTouchHelperAdapter;
import bsoft.com.musiceditor.listener.IListSongChanged;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Utils;

import static bsoft.com.musiceditor.utils.Utils.convertMillisecond;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder> implements IItemTouchHelperAdapter {
    private List<AudioEntity> audioEntityList;
    private Context context;
    private OnStartDragListener callback;
    private IListSongChanged iListSongChanged;

    public SortAdapter(List<AudioEntity> songList, Context context, OnStartDragListener callback, IListSongChanged listener) {
        this.audioEntityList = songList;
        this.context = context;
        this.callback = callback;
        this.iListSongChanged = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SortAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sort_audio, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AudioEntity audioEntity = audioEntityList.get(position);

        holder.tvName.setText(audioEntity.getNameAudio());
        holder.tvArtist.setText(audioEntity.getNameArtist());
        holder.tvDuration.setText(convertMillisecond(Long.parseLong(audioEntity.getDuration())));

        holder.ivSort.setOnTouchListener((view, motionEvent) -> {

            if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                callback.onStartDrag(holder);

            }
            return false;
        });

       holder.itemView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                   callback.onStartDrag(holder);

               }
               return true;
           }
       });

    }


    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    @Override
    public int getItemCount() {
        return audioEntityList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        audioEntityList.remove(position);
        iListSongChanged.onNoteListChanged(audioEntityList);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(audioEntityList, fromPosition, toPosition);
        iListSongChanged.onNoteListChanged(audioEntityList);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSort;
        private TextView tvName, tvArtist, tvDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            ivSort = itemView.findViewById(R.id.iv_sort);
            tvName = itemView.findViewById(R.id.name_song);
            tvArtist = itemView.findViewById(R.id.name_artist);
            tvDuration = itemView.findViewById(R.id.duration);
        }
    }
}
