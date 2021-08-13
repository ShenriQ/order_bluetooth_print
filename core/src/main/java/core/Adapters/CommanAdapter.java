package core.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommanAdapter extends RecyclerView.Adapter<CommanAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<String> items;
    int resources;
    private final OnItemClickListener listener;

    public CommanAdapter(List<String> items, int resources, OnItemClickListener listener) {
        this.items = items;
        this.resources = resources;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resources, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return (items != null && items.size() > 0) ? items.size() : 10;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

//        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
//            name = (TextView) itemView.findViewById(R.id.english);
        }

        public void bind(final int position, final OnItemClickListener listener) {
//            name.setText(item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(position);
                }
            });
        }
    }
}