package org.d3ifcool.alert;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.alert.adapter.ScheduleAdapter;
import org.d3ifcool.alert.model.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {
    private static final String TAG = "ScheduleFragment";
    private SwipeMenuListView scheduleListView;
    private ScheduleAdapter scheduleAdapter;
    private TextView emptyNotification;
    private ProgressBar loadingData;
    private boolean twoPanel;

    private String idSchedule;

    private Calendar myCalendar = Calendar.getInstance();
    private String myFormat = "dd/MM/yyyy"; //In which you need put here
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    private FirebaseUser currentUser;
    private AlertDialog dialog = null;
    private ArrayList<Schedule> dataSchedule = new ArrayList<>();

    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        if ((FrameLayout) rootView.findViewById(R.id.kategori_tabs) != null){
            twoPanel = true;
            Log.d("BERHASIL","YES");
        }

        scheduleListView = (SwipeMenuListView) rootView.findViewById(R.id.list_view_schedule);
        loadingData = (ProgressBar) rootView.findViewById(R.id.item_progres_bar);
        emptyNotification = (TextView) rootView.findViewById(R.id.infoTextView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getActivity().getIntent();
        idSchedule = intent.getStringExtra("idSchedule");

        ArrayList<Schedule> realData = getData();
        scheduleAdapter = new ScheduleAdapter(getContext(),realData);
        scheduleListView.setAdapter(scheduleAdapter);
        scheduleListView.setEmptyView(emptyNotification);
        moreDetail(realData);

        FloatingActionButton floatingActionButtonAddInvoice = (FloatingActionButton) rootView.findViewById(R.id.add_schedule);
        floatingActionButtonAddInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDataSchedule(null);
            }
        });

        setMenuWhenSwiping();
        onListenerClicked(realData);
        getDataSchedule();
        return rootView;
    }

    private void getDataSchedule() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference scheduleRef = database.getReference("schedule_data");
        scheduleRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot realDataSnapshot : dataSnapshot.getChildren()) {

                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Schedule schedule = snap.getValue(Schedule.class);
                    if (schedule != null) {
                        dataSchedule.add(schedule);
                        Log.d(TAG,"Data"+schedule.getNameOfSchedule());
                    }else{
                        Log.d(TAG,"ZONK");
                    }
                }

                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void moreDetail(final ArrayList<Schedule> scheduleData) {
        final View v = getActivity().getLayoutInflater().inflate(R.layout.activity_show_detail_schedule, null);

        final TextView date = (TextView) v.findViewById(R.id.schedule_date);
        final TextView names = (TextView) v.findViewById(R.id.schedule_name);
        final TextView times = (TextView) v.findViewById(R.id.schedule_time);

        final Button btnClose = (Button) v.findViewById(R.id.btn_close);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        date.setText("");
        times.setText("");

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (twoPanel){
                    Log.i(TAG, "onItemClick: berhasil");

                    Bundle bundle = new Bundle();
                    bundle.putString("tgl_schedule", dataSchedule.get(position).getDateOfSchedule() );
                    bundle.putString("nama_schedule", dataSchedule.get(position).getNameOfSchedule());
                    bundle.putString("time_schedule", dataSchedule.get(position).getTimeOfSchedule());
                    Log.i(TAG, "nama_schedule : "+ dataSchedule.get(position).getNameOfSchedule());
                    DetailScheduleFragment fragment = new DetailScheduleFragment();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.kategori_tabs,fragment).commit();

                }
                else {
                    Log.i(TAG, "onItemClick: tidak berhasil" );
                    String scheduleDetail = scheduleData.get(position).getIdScheduleList();
                    getAllDetailData(scheduleDetail, date, names, times);

                    View title = getActivity().getLayoutInflater().inflate(R.layout.text_view_customize, null);

                    ImageView icon = (ImageView) title.findViewById(R.id.custom_icon_of_title);
                    TextView text = (TextView) title.findViewById(R.id.custom_text_of_title);

                    icon.setImageResource(R.drawable.ic_info_black_24dp);
                    text.setText("Detail Schedule");

                    if (v.getParent() != null) {
                        ((ViewGroup) v.getParent()).removeView(v);
                        builder.setCustomTitle(title).setView(v);
                    } else {
                        builder.setCustomTitle(title).setView(v);
                    }

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                }
            }
        });
    }

    private void onListenerClicked(final ArrayList<Schedule> scheduleData){
        scheduleListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0 : dialogUpdateData(scheduleData.get(position).getIdScheduleList());break;
                    case 1 : dialogDeleteData(scheduleData.get(position).getIdScheduleList()); break;
                }

                return false;
            }
        });

        scheduleListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }


    public void setMenuWhenSwiping() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(getContext());
                editItem.setBackground(R.color.colorBackgroundEdit);
                editItem.setWidth(180);
                editItem.setIcon(R.drawable.ic_edit_black_24dp);
                menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                deleteItem.setBackground(R.color.colorBackgroundDelete);
                deleteItem.setWidth(180);
                deleteItem.setIcon(R.drawable.ic_delete_black_24dp);
                menu.addMenuItem(deleteItem);
            }
        };

        scheduleListView.setMenuCreator(creator);
    }

    private void dialogDeleteData(final String idPurch){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_data_question)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteData(idPurch);
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    /**
     * menampilkan dialog untuk mengambil waktu
     *
     * @param editText seting tempat untuk menampilkannya
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void timePicker(final EditText editText) {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute == 1 || minute == 2 || minute == 3 || minute == 4 || minute == 5 ||
                                minute == 6 || minute == 7 || minute == 8 || minute == 9 || minute == 0){
                            String minuteFix = "0"+ minute;
                            editText.setText(hourOfDay + ":" + minuteFix);
                        }
                        else {
                            editText.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    public void addDataSchedule(final Schedule schedule) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.activity_add_schedule,null);
        final EditText inputDate = (EditText) v.findViewById(R.id.input_date_schedule);
        final EditText inputNameSchedule = (EditText) v.findViewById(R.id.input_name_schedule);
        final EditText inputTimeSchedule = (EditText) v.findViewById(R.id.input_time_schedule);

        final Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
        final Button btnSave = (Button) v.findViewById(R.id.btn_save);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View title = getActivity().getLayoutInflater().inflate(R.layout.text_view_customize,null);

        final ImageView icon = (ImageView) title.findViewById(R.id.custom_icon_of_title);
        final TextView text = (TextView) title.findViewById(R.id.custom_text_of_title);

        inputDate.setText(sdf.format(myCalendar.getTime()));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                inputDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        inputDate.setText(sdf.format(myCalendar.getTime()));

        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        inputTimeSchedule.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                timePicker(inputTimeSchedule);
            }
        });

        if(schedule != null) {
            inputDate.setText(schedule.getDateOfSchedule());
            inputNameSchedule.setText(schedule.getNameOfSchedule());
            inputTimeSchedule.setText("" + schedule.getTimeOfSchedule());
            icon.setImageResource(R.drawable.ic_add_schedule_black_24dp);
            text.setText(R.string.title_add_schedule);

            if(v.getParent() != null) {
                ((ViewGroup)v.getParent()).removeView(v);
                builder.setCustomTitle(title).setView(v);
            }else{
                builder.setCustomTitle(title).setView(v);
            }

            final AlertDialog dialog = builder.create();

            dialog.show();

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String dateData = inputDate.getText().toString();
                    final String nameData = inputNameSchedule.getText().toString();
                    String timeData = inputTimeSchedule.getText().toString();

//                    String string = timeData;
//                    String[] parts = string.split(":");
//                    String part1 = parts[0];
//                    String part2 = parts[1];
//                    long jam = Long.valueOf(part1);
//                    long menit = Long.valueOf(part2);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference scheduleRef = database.getReference("schedule_data");

                    String currentIdSchedule;
                    if(idSchedule != null){
                        currentIdSchedule = idSchedule;
                    }else{
                        currentIdSchedule = scheduleRef.push().getKey();
                    }
                    if(dateData.matches("") || nameData.matches("") || timeData.matches("")){
                        Toast.makeText(getActivity(), "Save Failed : Fields can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
//                        final long timeSchedule = ((jam*3600)+(menit*60));
                        scheduleRef.child(currentUser.getUid()).child(currentIdSchedule).setValue(new Schedule(dateData,nameData,timeData));
                        scheduleAdapter.notifyDataSetChanged();
                    }

                    dialog.cancel();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }else {
            icon.setImageResource(R.drawable.ic_add_schedule_black_24dp);
            text.setText("Add Schedule");

            if (v.getParent() != null) {
                ((ViewGroup) v.getParent()).removeView(v);
                builder.setCustomTitle(title).setView(v);
            } else {
                builder.setCustomTitle(title).setView(v);
            }

            final AlertDialog dialog = builder.create();

            dialog.show();

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String dateData = inputDate.getText().toString();
                    final String nameData = inputNameSchedule.getText().toString();
                    final String timeData = inputTimeSchedule.getText().toString();

//                    String string = timeData;
//                    String[] parts = string.split(":");
//                    String part1 = parts[0];
//                    String part2 = parts[1];
//                    long jam = Long.valueOf(part1);
//                    long menit = Long.valueOf(part2);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference scheduleRef = database.getReference("schedule_data");

                    String currentIdSchedule;
                    if(idSchedule != null){
                        currentIdSchedule = idSchedule;
                    }else{
                        currentIdSchedule = scheduleRef.push().getKey();
                    }
                    if(dateData.matches("") || nameData.matches("") || timeData.matches("")){
                        Toast.makeText(getActivity(), "Save Failed : Fields can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
//                        final long timeSchedule = ((jam*3600)+(menit*60));
                        scheduleRef.child(currentUser.getUid()).child(currentIdSchedule).setValue(new Schedule(dateData,nameData,timeData));
                        scheduleAdapter.notifyDataSetChanged();
                    }

                    dialog.cancel();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }

    }

    private void dialogUpdateData(final String idSched){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View v = getActivity().getLayoutInflater().inflate(R.layout.activity_add_schedule,null);

        final EditText inputDate = (EditText) v.findViewById(R.id.input_date_schedule);
        final EditText inputNameSchedule = (EditText) v.findViewById(R.id.input_name_schedule);
        final EditText inputTimeSchedule = (EditText) v.findViewById(R.id.input_time_schedule);

        final Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
        final Button btnSave = (Button) v.findViewById(R.id.btn_save);

        inputDate.setText(sdf.format(myCalendar.getTime()));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                inputDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        inputDate.setText(sdf.format(myCalendar.getTime()));

        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        inputTimeSchedule.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                timePicker(inputTimeSchedule);
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("schedule_data");
        myRef.child(currentUser.getUid()).child(idSched).addValueEventListener(new ValueEventListener() {
            @Override
            public void  onDataChange(DataSnapshot dataSnapshot) {
                final Schedule dataSchedule = dataSnapshot.getValue(Schedule.class);
                //for (DataSnapshot realDataSnapshot : dataSnapshot.getChildren()) {
                if (dataSchedule != null) {
                    inputDate.setText(dataSchedule.getDateOfSchedule());
                    inputNameSchedule.setText(dataSchedule.getNameOfSchedule());
//                    long timeSchedule = dataSchedule.getTimeOfSchedule();
//                    long jam = (timeSchedule/3600);
//                    long menit = (timeSchedule%3600/60);
                    inputTimeSchedule.setText(dataSchedule.getTimeOfSchedule());

                }
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        View title = getActivity().getLayoutInflater().inflate(R.layout.text_view_customize,null);

        ImageView icon = (ImageView) title.findViewById(R.id.custom_icon_of_title);
        TextView text = (TextView) title.findViewById(R.id.custom_text_of_title);

        icon.setImageResource(R.drawable.ic_edit_black_24dp);
        text.setText("Edit Schedule Data");

        if(v.getParent() != null) {
            ((ViewGroup)v.getParent()).removeView(v);
            builder.setCustomTitle(title).setView(v);
        }else{
            builder.setCustomTitle(title).setView(v);
        }

        final AlertDialog dialog = builder.create();

        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = inputDate.getText().toString();
                String names = inputNameSchedule.getText().toString();
                String times = inputTimeSchedule.getText().toString();

//                String string = times;
//                String[] parts = string.split(":");
//                String part1 = parts[0];
//                String part2 = parts[1];
//                long jam = Long.valueOf(part1);
//                long menit = Long.valueOf(part2);
//                final long timeBase = ((jam*3600)+(menit*60));

                Schedule schedule = new Schedule(date, names, times);
                myRef.child(currentUser.getUid()).child(idSched).setValue(schedule);

                Toast.makeText(getActivity(), "Data saved.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public ArrayList<Schedule> getData() {
        final ArrayList<Schedule> currentSchedule = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        loadingData.setVisibility(View.VISIBLE);
        DatabaseReference scheduleRef = database.getReference("schedule_data");
        scheduleRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentSchedule.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final Schedule data = postSnapshot.getValue(Schedule.class);
                    final Schedule dataId = new Schedule(data, postSnapshot.getKey());
                    currentSchedule.add(dataId);
                }
                scheduleAdapter.notifyDataSetChanged();
                loadingData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        });
        return currentSchedule;
    }

    private void deleteData(final String idScheduleList){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myDeletMastereRef = database.getReference("schedule_data");
        myDeletMastereRef.child(currentUser.getUid()).child(idScheduleList).removeValue();
    }

    private void getAllDetailData(String idScheduleMaster, final TextView mDate, final TextView mNames, final TextView mTimes){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference scheduleRef = database.getReference("schedule_data");
        scheduleRef.child(currentUser.getUid()).child(idScheduleMaster).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot realDataSnapshot : dataSnapshot.getChildren()) {
                Schedule schedule = dataSnapshot.getValue(Schedule.class);

                if (schedule != null) {
                    mDate.setText(schedule.getDateOfSchedule());
                    mNames.setText(schedule.getNameOfSchedule());
//                    long timeSchedule = schedule.getTimeOfSchedule();
//                    long jam = (timeSchedule/3600);
//                    long menit = (timeSchedule%3600/60);
//                    String timeSchedule = schedule.getTimeOfSchedule();
//                    String[] parts = timeSchedule.split(":");
//                    String part1 = parts[0];
//                    String part2 = parts[1];
//                    long jam = Long.valueOf(part1);
//                    long menit = Long.valueOf(part2);
//                    final long timeBase = ((jam*3600)+(menit*60));
                    mTimes.setText(schedule.getTimeOfSchedule());
                }
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
