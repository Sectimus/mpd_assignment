package org.gcu.me.mpd_assignment.ui.list.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.gcu.me.mpd_assignment.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ItemFragment extends Fragment {
    private TextView title, description;
    private ImageView image;
    private ItemViewModel viewmodel;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        viewmodel = ViewModelProviders.of(this).get(ItemViewModel.class);
        View root = inflater.inflate(R.layout.fragment_list_item, container, false);

        this.title = root.findViewById(R.id.lbl_title);
        this.description = root.findViewById(R.id.lbl_description);
        this.image = root.findViewById(R.id.img_type);

        System.out.println("ahahaha");


        viewmodel.getmTitle().observe(this, s -> title.setText(s));
        viewmodel.getmDesc().observe(this, s -> description.setText(s));
        viewmodel.getmImage().observe(this, i -> {
            switch(i){
                case 0:{
                    //Current Incidents
                    image.setImageResource(R.drawable.ic_crash_24dp);
                    break;
                }
                case 1:{
                    //Roadworks
                    image.setImageResource(R.drawable.ic_roadworks_24dp);
                    break;
                }
                case 2:{
                    //Planned Roadworks
                    image.setImageResource(R.drawable.ic_road_time_24dp);
                    break;
                }
                default:{
                    //Unknown type
                    image.setImageResource(R.drawable.ic_android_black_24dp);
                    break;
                }
            }
        });

        return root;
    }
}
