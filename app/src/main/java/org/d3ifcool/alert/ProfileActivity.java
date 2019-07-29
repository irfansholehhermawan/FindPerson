package org.d3ifcool.alert;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.d3ifcool.alert.model.Ortu;
import org.d3ifcool.alert.model.Profile;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TextView mProfileNama;
    private TextView mProfileStatus;
    private TextView mProfileTTL;
    private TextView mProfileNoHP;
    private TextView mProfileJK;
    private TextView mOrtuNama;
    private TextView mOrtuNoHP;

    private ImageView mCompanyProfile;
    private ImageView mEditProfile;
    private String mCompanyUrlPhoto = "";

    public static CoordinatorLayout mCoordinatorLayout;
    public static ProgressBar mCheckProfileData;

    private FirebaseUser userAplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mCompanyProfile = (ImageView) findViewById(R.id.company_image_profil);
        mEditProfile = (ImageView) findViewById(R.id.company_profil_editor);

        mProfileNama = (TextView) findViewById(R.id.profile_nama);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileTTL = (TextView) findViewById(R.id.profile_ttl);
        mProfileNoHP = (TextView) findViewById(R.id.profile_phone_number);
        mProfileJK = (TextView) findViewById(R.id.profile_jk);
        mOrtuNama = (TextView) findViewById(R.id.profile_nama_orangtua);
        mOrtuNoHP = (TextView) findViewById(R.id.profile_phone_number_ortu);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.load_profil_data);
        mCheckProfileData = (ProgressBar) findViewById(R.id.check_profile_data);

//        Glide.with(this /* context */)
//                .load(MainActivity.mImageLoader)
//                .into(mCompanyProfile);

        userAplication = FirebaseAuth.getInstance().getCurrentUser();

        readProfileData();
        readOrtuData();
    }

    public void edit_profile(View v) {
        Intent intent = new Intent(ProfileActivity.this, AddProfileActivity.class);
        startActivity(intent);
    }

    public void edit_logo(View v) {
        Intent intent = new Intent(ProfileActivity.this, AddLogoActivity.class);
        startActivity(intent);
    }

    private ArrayList<Profile> readProfileData(){
        final ArrayList<Profile> profileData = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        mCheckProfileData.setVisibility(View.VISIBLE);
        DatabaseReference myRef = database.getReference("profile_data");
        myRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile user = dataSnapshot.getValue(Profile.class);
                if (user != null) {
                    mProfileNama.setText(user.getProfileName());
                    mProfileStatus.setText(user.getProfileStatus());
                    mProfileTTL.setText(user.getProfileTempat()+", "+user.getProfileTglLahir());
                    mProfileNoHP.setText(user.getProfileNoHP());
                    mProfileJK.setText(user.getProfileJK());
                    mCompanyUrlPhoto = user.getCompanyUrlPhoto();
                    Uri mUriPhoto = Uri.parse(mCompanyUrlPhoto);
                    Picasso.with(ProfileActivity.this)
                            .load(mUriPhoto).resize(200, 200)
                            .memoryPolicy(MemoryPolicy.NO_CACHE )
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .centerCrop()
                            .noFade()
                            .onlyScaleDown()
                            .into(mCompanyProfile);
                }
                else {
                    mEditProfile.setVisibility(View.GONE);
                }
                mCheckProfileData.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        return profileData;
    }

    private ArrayList<Ortu> readOrtuData(){
        final ArrayList<Ortu> OrtuData = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        mCheckProfileData.setVisibility(View.VISIBLE);
        DatabaseReference myRef = database.getReference("contact_ortu");
        myRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ortu user = dataSnapshot.getValue(Ortu.class);
                if (user != null) {
                    mOrtuNama.setText(user.getNamaOrtu());
                    mOrtuNoHP.setText(user.getNoHPOrtu());
                    Log.i(TAG, "Data Nama Ortu : " + user.getNamaOrtu());
                    Log.i(TAG, "Data No HP Ortu : " + user.getNoHPOrtu());
                }
                else {
                    mOrtuNama.setText("Isikan Contact Orang Tua");
                    mOrtuNoHP.setText("Isikan Contact Orang Tua");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        return OrtuData;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        readProfileData();
    }
}
