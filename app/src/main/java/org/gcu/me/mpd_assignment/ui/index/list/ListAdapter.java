package org.gcu.me.mpd_assignment.ui.index.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.PlannedRoadworks;
import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.models.Incident;

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

    private TableRow createTableRowFromProps(String key, String value){
        TableRow tr = (TableRow) context.getLayoutInflater().inflate(R.layout._fragment_list_item_property, null);

        ((TextView) tr.findViewById(R.id.key)).setText(key);
        ((TextView) tr.findViewById(R.id.value)).setText(value);

        return tr;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Traffic t = traffic.get(position);
        holder.title.setText(t.getTitle());

        //go through and create each property
        holder.properties.removeAllViews();
        for(Map.Entry<String, String> entry : t.getProperties().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            //works / delay info have a special case of description
            if(key.toLowerCase().equals("works")) {
                //this is the type of roadwork
                holder.description.setText(value);
            } else if(key.toLowerCase().equals("delay information")){
                holder.description.setText(value);
            } else{
                TableRow tr = createTableRowFromProps(key, value);
                holder.properties.addView(tr);
            }
        }

        //add the start and end times to the properties, these attributes are only shared by the roadworks class and its inheritants
        if(t instanceof Roadworks){
            Roadworks r = (Roadworks) t;
            TableRow trStart = (TableRow) context.getLayoutInflater().inflate(R.layout._fragment_list_item_property, null);
            ((TextView) trStart.findViewById(R.id.key)).setText("Start");
            ((TextView) trStart.findViewById(R.id.value)).setText(r.getFormattedStart());
            holder.properties.addView(trStart);

            TableRow trEnd = (TableRow) context.getLayoutInflater().inflate(R.layout._fragment_list_item_property, null);
            ((TextView) trEnd.findViewById(R.id.key)).setText("End");
            ((TextView) trEnd.findViewById(R.id.value)).setText(r.getFormattedEnd());
            holder.properties.addView(trEnd);
        }


        if(t instanceof PlannedRoadworks){
            //Planned Roadworks
            PlannedRoadworks pr = (PlannedRoadworks) t;

            holder.imageView.setImageResource(R.drawable.ic_road_time_24dp);
        } else if(t instanceof Roadworks){
            //Roadworks
            Roadworks r = (Roadworks) t;

            holder.description.setText(r.getDelayInformation());
            holder.imageView.setImageResource(R.drawable.ic_roadworks_24dp);
        } else if(t instanceof Incident){
            //Current Incidents
            Incident i = (Incident) t;
            holder.imageView.setImageResource(R.drawable.ic_crash_24dp);
        } else{
            //Unknown type
            holder.imageView.setImageResource(R.drawable.ic_android_black_24dp);
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
        private TableLayout properties;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.lbl_title);
            description = itemView.findViewById(R.id.lbl_description);
            textWrapper = itemView.findViewById(R.id.con_textwrapper);
            imageView = itemView.findViewById(R.id.img_type);
            properties = itemView.findViewById(R.id.lay_props);
        }
    }
}
