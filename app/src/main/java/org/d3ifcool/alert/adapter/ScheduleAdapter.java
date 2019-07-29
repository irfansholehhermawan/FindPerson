package org.d3ifcool.alert.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.d3ifcool.alert.R;
import org.d3ifcool.alert.model.Schedule;

import java.util.List;

/**
 * Created by Sholeh Hermawan on 9/13/2017.
 */

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    private TextView dayOfDate;
    private TextView monthOfDate;
    private TextView yearOfDate;
    private TextView scheduleName;
    private TextView scheduleTime;
    private RelativeLayout bgOfDate;


    public ScheduleAdapter(@NonNull Context context, @NonNull List<Schedule> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_list_view_controller,parent,false);
        }

        Schedule scheduleId = getItem(position);


        dayOfDate = (TextView) convertView.findViewById(R.id.day_schedule);
        monthOfDate = (TextView) convertView.findViewById(R.id.month_schedule);
        yearOfDate = (TextView) convertView.findViewById(R.id.year_schedule);
        scheduleName = (TextView) convertView.findViewById(R.id.name_schedulee);
        scheduleTime = (TextView) convertView.findViewById(R.id.schedule_time);
        bgOfDate = (RelativeLayout) convertView.findViewById(R.id.bg_of_month);

        String[] items = scheduleId.getDateOfSchedule().split("/");
        String d1 = items[0];
        String m1 = items[1];
        String y1 = items[2];

        int day = Integer.parseInt(d1);
        int month = Integer.parseInt(m1);
        int year = Integer.parseInt(y1);

        String monthString = "";

        if(month == 1){ monthString = "JAN"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_04_24dp);}
        else if(month == 2)  { monthString = "FEB"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_08_24dp);}
        else if(month == 3)  { monthString = "MAR"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_09_24dp); }
        else if(month == 4)  { monthString = "APRIL"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_03_24dp); }
        else if(month == 5)  { monthString = "MAY"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_02_24dp); }
        else if(month == 6)  { monthString = "JUNE"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_07_24dp); }
        else if(month == 7)  { monthString = "JULY"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_11_24dp); }
        else if(month == 8)  { monthString = "AUGT"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_05_24dp); }
        else if(month == 9)  { monthString = "SEPT"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_12_24dp); }
        else if(month == 10) { monthString = "OCT"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_06_24dp); }
        else if(month == 11) { monthString = "NOV"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_01_24dp); }
        else if(month == 12) { monthString = "DEC"; bgOfDate.setBackgroundResource(R.drawable.ic_circle_10_24dp); }

        dayOfDate.setText(String.valueOf(day).toString());
        monthOfDate.setText(monthString);
        yearOfDate.setText(String.valueOf(year).toString());
        scheduleName.setText(scheduleId.getNameOfSchedule());
        scheduleTime.setText(String.valueOf(scheduleId.getTimeOfSchedule()));

        return convertView;
    }
}
