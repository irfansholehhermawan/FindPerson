package org.d3ifcool.alert;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Sholeh Hermawan on 10/30/2017.
 */

public class DetailScheduleFragment extends Fragment {
    private TextView mTglSchedule;
    private TextView mNamaSchedule;
    private TextView mTimeSchedule;
    private Button button;
    private String mTgl;
    private String mName;
    private String mTime;

    public DetailScheduleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_show_detail_schedule, container, false);

        mTglSchedule = (TextView) rootView.findViewById(R.id.schedule_date);
        mNamaSchedule = (TextView) rootView.findViewById(R.id.schedule_name);
        mTimeSchedule = (TextView) rootView.findViewById(R.id.schedule_time);
        button = (Button) rootView.findViewById(R.id.btn_close);
        button.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            mTgl = bundle.getString("tgl_schedule");
            mName = bundle.getString("nama_schedule");
            mTime = bundle.getString("time_schedule");
            mTglSchedule.setText(mTgl);
            mNamaSchedule.setText(mName);
            mTimeSchedule.setText(mTime);
        }

        return rootView;
    }
}
