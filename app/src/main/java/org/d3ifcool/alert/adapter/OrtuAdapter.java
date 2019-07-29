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
import org.d3ifcool.alert.model.Ortu;

import java.util.List;

/**
 * Created by Sholeh Hermawan on 10/31/2017.
 */

public class OrtuAdapter extends ArrayAdapter<Ortu> {
    private TextView ortuName;
    private TextView ortuNoHP;


    public OrtuAdapter(@NonNull Context context, @NonNull List<Ortu> objects) {
        super(context,0 , objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ortu_list_view_controller,parent,false);
        }

        Ortu ortuId = getItem(position);

        ortuName = (TextView) convertView.findViewById(R.id.name_contact_ortu);
        ortuNoHP = (TextView) convertView.findViewById(R.id.nohp_contact_ortu);

        ortuName.setText(ortuId.getNamaOrtu());
        ortuNoHP.setText(ortuId.getNoHPOrtu());

        return convertView;
    }
}
