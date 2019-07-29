package org.d3ifcool.alert.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.d3ifcool.alert.R;
import org.d3ifcool.alert.model.Location;

import java.util.List;

/**
 * Created by Sholeh Hermawan on 10/31/2017.
 */

public class LocationAdapter extends ArrayAdapter<Location> {
    private TextView locationDate;
    private TextView locationName;
    private TextView locationTime;


    public LocationAdapter(@NonNull Context context, @NonNull List<Location> objects) {
        super(context,0 , objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_list_view_controller,parent,false);
        }

        Location locationId = getItem(position);

        locationDate = (TextView) convertView.findViewById(R.id.date_location);
        locationName = (TextView) convertView.findViewById(R.id.name_location);
        locationTime = (TextView) convertView.findViewById(R.id.time_location);

        locationDate.setText(locationId.getDateOfLocation());
        locationName.setText(locationId.getNameOfLocation());
        locationTime.setText(locationId.getTimeOfLocation());

        return convertView;
    }
}
