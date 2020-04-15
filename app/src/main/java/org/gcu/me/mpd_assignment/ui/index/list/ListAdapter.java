package org.gcu.me.mpd_assignment.ui.index.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private List<Traffic> traffic;
    private Fragment context;
    private ListAdapterListener listener;

    public interface ListAdapterListener {
        void onListItemClicked(Traffic t);
    }

    public ListAdapter(Fragment context, List<Traffic> traffic, ListAdapterListener listener){
        this.context = context;
        this.traffic = traffic;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_item, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onViewRecycled(@NonNull ListViewHolder holder) {
        super.onViewRecycled(holder);

        //reset recycler attributes changed by the click
        ViewGroup.LayoutParams params = holder.textWrapper.getLayoutParams();
        params.height = 0;
        holder.textWrapper.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Traffic t = traffic.get(position);
        holder.title.setText(t.getTitle());


        switch(t.getTrafficId()){
            case 0:{
                //Current Incidents
                holder.description.setText(t.getDescription());
                holder.imageView.setImageResource(R.drawable.ic_crash_24dp);
                break;
            }
            case 1:{
                //Roadworks
                Roadworks r = (Roadworks) t;

                String description = "";

                for(Map.Entry<String, String> entry : r.getProperties().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    description+=key;
                    description+=value;
                }
            //    String description = r.getDescription();
                description+="\nStart: "+r.getFormattedStart()+"\nFinish: "+r.getFormattedEnd()+"\nTime until finished:";
                holder.description.setText(description);
                holder.imageView.setImageResource(R.drawable.ic_roadworks_24dp);
                break;
            }
            case 2:{
                //Planned Roadworks
                holder.description.setText(t.getDescription());
                holder.imageView.setImageResource(R.drawable.ic_road_time_24dp);
                break;
            }
            default:{
                //Unknown type
                holder.imageView.setImageResource(R.drawable.ic_android_black_24dp);
                break;
            }
        }

        holder.textWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //invert the height from wrap_content to match_constraint
                ViewGroup.LayoutParams params = holder.textWrapper.getLayoutParams();
                params.height = params.height==0 ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
                holder.textWrapper.setLayoutParams(params);

                //call the interface listener
                listener.onListItemClicked(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.traffic.size();
    }

    public void setTraffic(List<Traffic> traffic){
        this.traffic.clear();
        this.traffic.addAll(traffic);
        this.notifyDataSetChanged();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        private TextView title, description;
        private View itemView;
        private ConstraintLayout textWrapper;
        private ImageView imageView;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.lbl_title);
            description = itemView.findViewById(R.id.lbl_description);
            textWrapper = itemView.findViewById(R.id.con_textwrapper);
            imageView = itemView.findViewById(R.id.img_type);
        }
    }
}
