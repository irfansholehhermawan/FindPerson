package org.d3ifcool.alert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.alert.model.Location;

/**
 * Created by Sholeh Hermawan on 11/30/2017.
 */

public class HistoryDetailFragment extends Fragment implements OnMapReadyCallback {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ID_ANAK = "anak_id";

    private GoogleMap mMap;

    private TextView textViewDate;
    private TextView textViewLocation;
    private TextView textViewTime;

    private LatLng latLng;
    private String historyId;
    private String anakId;

    public HistoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            historyId = getArguments().getString(ARG_ITEM_ID);
        }
        if (getArguments().containsKey(ID_ANAK)) {
            anakId = getArguments().getString(ID_ANAK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_detail, container, false);

        Log.i("HistoryDetailFragment", "ID List : "+historyId);
        Log.i("HistoryDetailFragment", "ID ListAnak : "+anakId);

        // Show the dummy content as text in a TextView.
        if (historyId != null) {
            readData(historyId, anakId);
        }

        textViewDate = (TextView) rootView.findViewById(R.id.text_view_date);
        textViewLocation = (TextView) rootView.findViewById(R.id.text_view_location);
        textViewTime = (TextView) rootView.findViewById(R.id.text_view_time);

        MapView mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(latLng != null)
            setMarker(latLng);
    }

    private void setMarker(LatLng latLng){
        mMap.addMarker(new MarkerOptions()
                .position(latLng));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setTextView(Location history){
        textViewDate.setText(history.getDateOfLocation());
        textViewLocation.setText(history.getNameOfLocation());
        textViewTime.setText(history.getTimeOfLocation());
    }

    private void readData(String historyId, String anakId){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("history").child(anakId).child(historyId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location history = dataSnapshot.getValue(Location.class);
                latLng = new LatLng(history.getLatitude(), history.getLongitude());
                setTextView(history);
                if(mMap != null)
                    setMarker(latLng);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

