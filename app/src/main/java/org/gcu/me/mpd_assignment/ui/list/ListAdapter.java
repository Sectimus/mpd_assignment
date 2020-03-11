package org.gcu.me.mpd_assignment.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.Traffic;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private List<Traffic> traffic;
    private Fragment context;

    public ListAdapter(Fragment context, List<Traffic> traffic){
        this.context = context;
        this.traffic = traffic;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_item, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Traffic t = traffic.get(position);
        holder.title.setText(t.getTitle());
        holder.description.setText(t.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //invert the height from wrap_content to match_constraint
                ViewGroup.LayoutParams params = holder.textWrapper.getLayoutParams();
                params.height = params.height==0 ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
                holder.textWrapper.setLayoutParams(params);
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
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.lbl_title);
            description = itemView.findViewById(R.id.lbl_description);
            textWrapper = itemView.findViewById(R.id.con_textwrapper);
        }
    }
}
