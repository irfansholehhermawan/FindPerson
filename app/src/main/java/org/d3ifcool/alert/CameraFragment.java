package org.d3ifcool.alert;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private DatabaseReference mDatabaseRef;
    private FirebaseUser userAplication;
    private List<ImageUpload> imgList;
    private GridView lv;
    private ImageListAdapter adapter;
    private ProgressDialog progressDialog;
    private ProgressBar loadingData;
    private TextView emptyNotification;
    String idMasterUpload;
    private boolean twoPanel;

    public CameraFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        loadingData = (ProgressBar) rootView.findViewById(R.id.item_progres_bar);
        emptyNotification = (TextView) rootView.findViewById(R.id.infoTextView);
        loadingData.setVisibility(View.GONE);

        userAplication = FirebaseAuth.getInstance().getCurrentUser();
      
        if ((FrameLayout) rootView.findViewById(R.id.kategori_tab_camera) != null){
            twoPanel = true;
            Log.d("BERHASIL","YES");
        }
      
        imgList = new ArrayList<>();
        lv = (GridView) rootView.findViewById(R.id.listViewImage);
        lv.setEmptyView(emptyNotification);
        //Show progress dialog during list image loading
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait!");
        progressDialog.show();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(ImageUploadActivity.FB_DATABASE_PATH);

        mDatabaseRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                //Fetch image data from firebase database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //ImageUpload class require default constructor
                    ImageUpload img = snapshot.getValue(ImageUpload.class);
                    imgList.add(img);
                }


                if (getActivity()!=null) {
                    //Init adapter
                    adapter = new ImageListAdapter(getActivity(), R.layout.grid_item_layout, imgList);
                    //Set adapter for listview
                    lv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (twoPanel){
                    Bundle bundle = new Bundle();
                    bundle.putString("title", imgList.get(position).getName() );
                    bundle.putString("bitmap", imgList.get(position).getUrl());
                    Log.i(TAG, "onItemClick: berhasil"+imgList.get(position).getName());
                    Log.i(TAG, "onItemClick: berhasil"+imgList.get(position).getUrl());
                    DetailsFragment fragment = new DetailsFragment();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.kategori_tab_camera,fragment).commit();

                }
                else {
                    ImageUpload image = (ImageUpload) parent.getItemAtPosition(position);
                    String ids = getActivity().getIntent().getStringExtra("com.sample.MESSAGE");
                    Log.i(TAG, "onItemClick: " + ids);
                    //Create intent
                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    intent.putExtra("title", image.getName());
                    intent.putExtra("image", image.getUrl());
                    intent.putExtra("ids", image.getUrl());

                    //Start details activity
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton floatingActionButtonAddInvoice = (FloatingActionButton) rootView.findViewById(R.id.add_image);
        floatingActionButtonAddInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ImageUploadActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

}