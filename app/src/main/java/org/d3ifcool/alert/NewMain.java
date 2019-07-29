package org.d3ifcool.alert;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewMain extends AppCompatActivity {
    private static FirebaseUser userAplication;
    public static Uri mImageLoader;
    public static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_camera:
                    changeFragment(new CameraFragment());
                    return true;
                case R.id.navigation_loation:
                    changeFragment(new MapFragment());
                    return true;
                case R.id.navigation_schedule:
                    changeFragment(new ScheduleFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        userAplication = FirebaseAuth.getInstance().getCurrentUser();
        checkUser(userAplication);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setUpLandingFragment();
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    private void setUpLandingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().add(R.id.content, new CameraFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //to add option menu
        switch (item.getItemId()) {
            case R.id.add_profile:
                Intent intentProfile = new Intent(NewMain.this, ProfileActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(NewMain.this);
        builder.setMessage("Do you want to logout ?")
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intentLogin = new Intent(NewMain.this, LoginActivity.class);
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

    public static void downloadImage(){
        storageReference.child("File/"+ userAplication.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                mImageLoader = uri;
                //TODO: fixed low image load respon
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void checkUser(FirebaseUser firebaseUser){
        if(firebaseUser == null){
            goToLoginActivity();
        }
    }

    private void goToLoginActivity(){
        Intent intentLogin = new Intent(NewMain.this, LoginActivity.class);
        startActivity(intentLogin);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}