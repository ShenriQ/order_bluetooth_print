package core.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.fooddeliverysystem.R;
import java.util.ArrayList;
import java.util.Arrays;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<String> items;
    int resources;
    private final OnItemClickListener listener;

    public StringAdapter(/*List<String> items*/int resources, OnItemClickListener listener) {
//        this.items = items;
        items = new ArrayList<String>(Arrays.asList("1. Ali", "2. Imran https://google.com, https://youtu.be/dhYOPzcsbGM", "3. Fatima", "4. Qazi https://github.com/LeonardoCardoso/Android-Link-Preview", "5. Sohail",
                "6. Faizan", "7. Talha", "8. Noman", "9. Waqas", "10. Shahzad"));
        this.resources = resources;
        this.listener = listener;
    }

    public StringAdapter(ArrayList<String> items, int resources, OnItemClickListener listener) {
        this.items = items;
        this.resources = resources;
        this.listener = listener;
    }

    public static StringAdapter build(int resources, OnItemClickListener listener) {
        return new StringAdapter(resources, listener);
    }

    public static StringAdapter build(ArrayList<String> items, int resources, OnItemClickListener listener) {
        return new StringAdapter(items, resources, listener);
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
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title);
        }

        public void bind(final int position, final OnItemClickListener listener) {
            name.setText(items.get(position));
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