package org.d3ifcool.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.alert.model.Ortu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SALADKING on 27/09/2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int LOCATION_PERMISSION_ID = 1001;
    private static final String TAG = "MapFragment";
    private DatabaseReference databaseReference;
    private FirebaseUser curentUser;
    private FloatingActionButton fabButtonHelp, floatingActionButtonGoToLocation;
    private GPSTracker gpsTracker;
    private Location mLocation;
    double latitude, longitude;
    private TextView namaOrtu;
    private TextView noHPOrtu;
    private String nohp;

    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, null);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        curentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
        }

        fabButtonHelp = (FloatingActionButton) rootView.findViewById(R.id.fab_go_to_help);
        fabButtonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GPSTracker(getContext());
                mLocation = gpsTracker.getLocation();
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                addToHistory();
                if (nohp == null){
                    Toast.makeText(getContext(), R.string.notifikasi_nohp, Toast.LENGTH_SHORT).show();
                    confirmNoHP();
                } else {
                    dialPhoneNumber(nohp);
                }
            }
        });

        floatingActionButtonGoToLocation = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button_go_to_location);
        floatingActionButtonGoToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpsTracker = new GPSTracker(getContext());
                mLocation = gpsTracker.getLocation();
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                addToHistory();
            }
        });

        namaOrtu = (TextView) rootView.findViewById(R.id.profile_name_ortu);
        noHPOrtu = (TextView) rootView.findViewById(R.id.profile_no_hp_ortu);

        readContactOrtu();

        return rootView;
    }

    private void confirmNoHP(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_contact_ortu)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    private ArrayList<Ortu> readContactOrtu(){
        final ArrayList<Ortu> ortuData = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("contact_ortu");
        myRef.child(curentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ortu user = dataSnapshot.getValue(Ortu.class);
                if (user != null) {
                    namaOrtu.setText(user.getNamaOrtu());
                    noHPOrtu.setText(user.getNoHPOrtu());
                    nohp = user.getNoHPOrtu();
                }else {
                    namaOrtu.setText("Isikan Contact Orang Tua");
                    noHPOrtu.setText("Isikan Contact Orang Tua");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        return ortuData;
    }

    /**
     * intent for daling
     * @param phoneNumber phone number will call
     */
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String getNameLocation(){
        String namaLocation = gpsTracker.getLocationName(gpsTracker.getLatitude(),gpsTracker.getLongitude());
        return namaLocation;
    }

    private LatLng getPosition() {
        gpsTracker = new GPSTracker(getContext());
        LatLng myPosition = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        return  myPosition;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDateNow(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        String currentDateTimeString = simpleDateFormat.format(calendar.getTime());
        return currentDateTimeString;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getTimeNow(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        String currentDateTimeString = simpleDateFormat.format(calendar.getTime());
        String string = currentDateTimeString;
        String[] parts = string.split(":");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];
        int jam = Integer.valueOf(part1);
        int jamFix = 0;
        jamFix = jam+22;
        String ketTime = null;
        if (jamFix >= 24){
            jamFix = jamFix-24;
        }
        int menit = Integer.valueOf(part2);
        String menitFix = null;
        if (menit == 0 || menit == 1 || menit == 2 || menit == 3 || menit == 4 || menit == 5 || menit == 6 || menit == 7 || menit == 8 || menit == 9){
            menitFix = "0"+String.valueOf(menit);
        }
        else {
            menitFix = String.valueOf(menit);
        }
        if (jamFix < 12){
            ketTime = "AM";
        }
        else {
            ketTime = "PM";
        }
        int detik = Integer.valueOf(part3);
        String time = jamFix + ":" + menitFix + " " + ketTime;
        return time;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addToHistory(){
        //TODO: chagne to real activity
        org.d3ifcool.alert.model.Location history = new org.d3ifcool.alert.model.Location(getDateNow(), getNameLocation(), getTimeNow(), gpsTracker.getLatitude(), gpsTracker.getLongitude());
        databaseReference.child("history").child(curentUser.getUid()).push().setValue(history);
    }

    private void updateUserForOffline(){
        databaseReference.child("user_online").child(curentUser.getUid()).removeValue();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Add a marker in Sydney and move the camera
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng here = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(here).title("I'am Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 14));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
