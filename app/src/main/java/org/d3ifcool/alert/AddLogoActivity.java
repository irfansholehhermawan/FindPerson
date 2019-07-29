package org.d3ifcool.alert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.d3ifcool.alert.model.Profile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddLogoActivity extends AppCompatActivity {

    private static final int GALERY_INTENT = 2;
    private static final int LOCATION_PERMISSION_ID = 1001;

    public static Uri imageUri;
    private ImageView mCompanyLogo;
    private Button mNextToCompanySpec;
    private RelativeLayout mButtonView;
    private ProgressBar mProgressUpload;

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseUser userAplication;
    public static boolean imageStatusChange = false;
    private String mCompanyUrlPhoto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_logo);

        mCompanyLogo = (ImageView) findViewById(R.id.add_company_logo);
        mNextToCompanySpec = (Button) findViewById(R.id.next_add_company_profile);
        mButtonView = (RelativeLayout) findViewById(R.id.button_view);
        mProgressUpload = (ProgressBar) findViewById(R.id.upload_logo_progressess);

        userAplication = FirebaseAuth.getInstance().getCurrentUser();

        mNextToCompanySpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddLogoActivity.this,AddProfileActivity.class);
                startActivity(intent);
            }
        });

        readUserData();

//        Glide.with(AddLogoActivity.this /* context */)
//                .load(MainActivity.mImageLoader)
//                .into(mCompanyLogo);

        FloatingActionButton floatingActionButtonAddInvoice = (FloatingActionButton) findViewById(R.id.add_logo_from_galery);
        floatingActionButtonAddInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(AddLogoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddLogoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, LOCATION_PERMISSION_ID);
            }
            else {
                getImageFromGalery();
            }
            }
        });

        mNextToCompanySpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageStatusChange = true;
                mButtonView.setVisibility(View.GONE);
                mProgressUpload.setVisibility(View.VISIBLE);
                StorageReference filepath = mStorageRef.child("File").child(userAplication.getUid());
                if(imageUri != null) {
                    filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            mButtonView.setVisibility(View.VISIBLE);
                            mProgressUpload.setVisibility(View.GONE);
                            Toast.makeText(AddLogoActivity.this, "Success Upload Images", Toast.LENGTH_SHORT).show();
                            mNextToCompanySpec.setText("NEXT");
                            mNextToCompanySpec.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(AddLogoActivity.this, AddProfileActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }else {
                    mButtonView.setVisibility(View.VISIBLE);
                    mProgressUpload.setVisibility(View.GONE);
                    mNextToCompanySpec.setText("NEXT");
                    mNextToCompanySpec.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AddLogoActivity.this, AddProfileActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        StatusActivity.downloadImage();
    }

    /**
     * this method to set image from galery to imageView
     * @param requestCode to get result from image picker
     * @param resultCode to check result form image picker
     * @param data this is Intent Access_Pick
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                mCompanyLogo.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AddLogoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(AddLogoActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

    }

    /**
     * this method to load access galery in External storage
     */
    private void getImageFromGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALERY_INTENT);
    }

    //TODO: Upload Handler will set here

    /**
     * for handling realtime permision
     * @param requestCode code request permission
     * @param permissions
     * @param grantResults permition granted or not
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getImageFromGalery();
        }
    }

    private ArrayList<Profile> readUserData(){
        final ArrayList<Profile> userData = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("profile_data");
        myRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile user = dataSnapshot.getValue(Profile.class);
                if(user != null) {
                    mCompanyUrlPhoto = user.getCompanyUrlPhoto();
                    Uri mUriPhoto = Uri.parse(mCompanyUrlPhoto);
                    Picasso.with(AddLogoActivity.this)
                            .load(mUriPhoto).resize(200, 200)
                            .memoryPolicy(MemoryPolicy.NO_CACHE )
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .centerCrop()
                            .noFade()
                            .onlyScaleDown()
                            .into(mCompanyLogo);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        return userData;
    }
}
