package bsoft.com.musiceditor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bsoft.com.musiceditor.R;

public class QualityAdapter extends RecyclerView.Adapter<QualityAdapter.ViewHolder> {
    private OnClick callback;
    private boolean isSetBitrate;
    private int indexBitrate;
    private Context context;

    public QualityAdapter(OnClick callback, boolean isSetBitrate, Context context) {
        this.context = context;
        this.callback = callback;
        this.isSetBitrate = isSetBitrate;
    }

    private String quality[] = new String[]{"Mono -11k", "Mono -16k", "Mono -22k", "Mono -44k"};
    private String bitrate[] = new String[]{"128kbs", "160kbs", "192kbs", "256kbs", "320kbs"};

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_format, parent, false);
        return new QualityAdapter.ViewHolder(itemView);
    }

    public void setSelectBitrate(int index) {
        indexBitrate = index;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isSetBitrate) {
            if (indexBitrate == position) {
                holder.tvQuality.setText(bitrate[position]);
                holder.tvQuality.setTextColor(context.getResources().getColor(R.color.devider));
                holder.ivState.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            } else {
                holder.tvQuality.setText(bitrate[position]);
                holder.tvQuality.setTextColor(Color.WHITE);
                holder.ivState.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }


        } else {
            holder.tvQuality.setText(quality[position]);
            holder.ivState.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);

        }
    }

    @Override
    public int getItemCount() {
        if (isSetBitrate) {
            return bitrate.length;

        } else {
            return quality.length;

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivState;
        private TextView tvQuality;

        public ViewHolder(View itemView) {
            super(itemView);

            ivState = itemView.findViewById(R.id.iv_state);

            tvQuality = itemView.findViewById(R.id.tv_format);

            itemView.setOnClickListener(v -> callback.onItemClick(getAdapterPosition()));
        }
    }

    public interface OnClick {
        void onItemClick(int index);
    }
}
