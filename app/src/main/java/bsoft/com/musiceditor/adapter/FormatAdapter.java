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

public class FormatAdapter extends RecyclerView.Adapter<FormatAdapter.ViewHolder> {
    private String format[] = new String[]{"mp3", "wav", "m4a", "aac"};
    private String formatRecord[] = new String[]{"mp3", "wav"};
    private OnClick callback;
    private boolean isRecord;
    private Context context;
    private int indexSelect = 0;

    public FormatAdapter(OnClick callback, boolean isRecord, Context context) {
        this.callback = callback;
        this.isRecord = isRecord;
        this.context = context;
    }

    public void setIndexSelected(int index) {
        indexSelect = index;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_format, parent, false);
        return new FormatAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isRecord) {
            holder.tvFormat.setText(formatRecord[position]);
            holder.ivState.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        } else {
            holder.tvFormat.setText(format[position]);
            holder.ivState.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        }

        if (indexSelect == position) {
            holder.ivState.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            holder.tvFormat.setTextColor(context.getResources().getColor(R.color.devider));
        } else {
            holder.ivState.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            holder.tvFormat.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        if (isRecord) {
            return formatRecord.length;
        } else {
            return format.length;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivState;
        private TextView tvFormat;

        public ViewHolder(View itemView) {
            super(itemView);
            ivState = itemView.findViewById(R.id.iv_state);
            tvFormat = itemView.findViewById(R.id.tv_format);
            itemView.setOnClickListener(v -> callback.onClick(getAdapterPosition()));
        }
    }

    public interface OnClick {
        void onClick(int index);
    }
}
