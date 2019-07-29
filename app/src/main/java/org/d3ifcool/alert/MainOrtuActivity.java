package org.d3ifcool.alert;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.alert.adapter.LocationAdapter;
import org.d3ifcool.alert.model.Location;
import org.d3ifcool.alert.model.Profile;

import java.util.ArrayList;

public class MainOrtuActivity extends AppCompatActivity {
    private String TAG="MainOrtuActivity";
    private ArrayList<String> historyIds;
    private String anakIds;
    private LocationAdapter locationAdapter;
    private FirebaseUser userAplication;
    DatabaseReference databaseReference;
    private TextView emptyDataLocation;
    private TextView mProfileNama;
    private TextView mProfileTTL;
    private TextView mProfileNoHP;
    private TextView mProfileJK;
    ListView listView;
    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBarDataLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ortu);

        userAplication = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressBarDataLocation = (ProgressBar) findViewById(R.id.progressBar);
        emptyDataLocation = (TextView) findViewById(R.id.empty_view);
        mProfileNama = (TextView) findViewById(R.id.profile_nama_ortu);
        mProfileTTL = (TextView) findViewById(R.id.profile_ttl_ortu);
        mProfileNoHP = (TextView) findViewById(R.id.profile_nohp_ortu);
        mProfileJK = (TextView) findViewById(R.id.profile_jk_ortu);

        listView = (ListView) findViewById(R.id.list_history);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainOrtuActivity.this, HistoryDetailActivity.class);
                intent.putExtra(HistoryDetailFragment.ARG_ITEM_ID, historyIds.get(i));
                intent.putExtra(HistoryDetailFragment.ID_ANAK, anakIds);
                startActivity(intent);
                Log.i(TAG, "ID List : "+ historyIds.get(i));
                Log.i(TAG, "ID ListANAK : "+ anakIds);

            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button_go_to_grenerator);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusAnak();
                Intent intent = new Intent(MainOrtuActivity.this, ReaderActivity.class);
                startActivity(intent);
            }
        });
        readIdAnak();
        Log.i(TAG, "idAnak: "+anakIds);
    }

    private void hapusAnak(){
        databaseReference.child("user_profile").child(userAplication.getUid()).removeValue();
    }

    private ArrayList<String> readIdAnak() {
        final ArrayList<String> idAnak = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user_profile");
        myRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idAnak.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    anakIds.add(postSnapshot.getKey());
                    anakIds = postSnapshot.getKey();
                    readProfileData(anakIds);
                    readHistoryData(anakIds);
                    historyIds = new ArrayList<>();
                    ArrayList<Location> histories = readHistoryData(anakIds);
                    locationAdapter = new LocationAdapter(MainOrtuActivity.this, histories);
                    listView.setAdapter(locationAdapter);
                    listView.setEmptyView(emptyDataLocation);
                    Log.i(TAG, "postSnapshot: " + anakIds);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        Log.i(TAG, "readIdAnak: "+idAnak);
        return idAnak;
    }

    private ArrayList<Location> readHistoryData(String idAnakHistory) {
        final ArrayList<Location> locationData = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        progressBarDataLocation.setVisibility(View.VISIBLE);
        DatabaseReference myRef = database.getReference("history");
        myRef.child(idAnakHistory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationData.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    historyIds.add(postSnapshot.getKey());
                    Location location = postSnapshot.getValue(Location.class);
                    locationData.add(location);
//                    final Location data = postSnapshot.getValue(Location.class);
//                    final Location dataId = new Location(data, postSnapshot.getKey());
//                    locationData.add(dataId);
//                    Log.d(TAG,"Data Date"+data.getDateOfLocation());
//                    Log.d(TAG,"Data Nama"+data.getNameOfLocation());
//                    Log.d(TAG,"Data Time"+data.getTimeOfLocation());
                }
                locationAdapter.notifyDataSetChanged();
                progressBarDataLocation.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        return locationData;
    }

    private ArrayList<Profile> readProfileData(String idAnak){
        final ArrayList<Profile> profileData = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("profile_data");
        myRef.child(idAnak).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile user = dataSnapshot.getValue(Profile.class);
                if (user != null) {
                    mProfileNama.setText(user.getProfileName());
                    mProfileTTL.setText(user.getProfileTempat() + ", " + user.getProfileTglLahir());
                    mProfileNoHP.setText(user.getProfileNoHP());
                    mProfileJK.setText(user.getProfileJK());
                }else {
                    mProfileNama.setText("none");
                    mProfileTTL.setText("none");
                    mProfileNoHP.setText("none");
                    mProfileJK.setText("none");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        return profileData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ortu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //to add option menu
        switch (item.getItemId()) {
            case R.id.add_profile:
                Intent intentProfile = new Intent(MainOrtuActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                return true;
            case R.id.logout:
                signOutCheck();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOutCheck(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainOrtuActivity.this);
        builder.setMessage("Do you want to logout ?")
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intentLogin = new Intent(MainOrtuActivity.this, LoginActivity.class);
                        startActivity(intentLogin);
                    }
                }).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
