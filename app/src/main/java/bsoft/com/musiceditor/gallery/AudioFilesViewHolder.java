package bsoft.com.musiceditor.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Utils;

public class AudioFilesViewHolder extends RecyclerView.ViewHolder {
    TextView name, size;
    ImageView avatar;

    public AudioFilesViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.name);
        avatar = view.findViewById(R.id.avatar);
        size = view.findViewById(R.id.size);

    }

    public static void bind(Context context, AudioFilesViewHolder viewHolder, AudioEntity audioEntity) {
        viewHolder.name.setText(audioEntity.getNameAudio());
        viewHolder.size.setText(Utils.convertMillisecond(Long.parseLong(audioEntity.getDuration())) + "   ");

        RequestOptions options = new RequestOptions();
        options.error(R.drawable.ic_img_ms);
        options.placeholder(R.drawable.ic_img_ms);

        Glide.with(context).load(audioEntity.getPathImage()).apply(options).into(viewHolder.avatar);
    }
}
