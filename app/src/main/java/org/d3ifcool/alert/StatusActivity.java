package org.d3ifcool.alert;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.d3ifcool.alert.model.Akun;

public class StatusActivity extends AppCompatActivity {
    private static final String TAG = "StatusActivity";
    private Spinner mTextStatus;
    private Button buttonSubmit;

    private static FirebaseUser userAplication;
    public static Uri mImageLoader;
    public static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        userAplication = FirebaseAuth.getInstance().getCurrentUser();
        mTextStatus = (Spinner) findViewById(R.id.add_status);
        buttonSubmit = (Button) findViewById(R.id.btn_submit_status);

        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this,
                R.array.array_of_status, android.R.layout.simple_spinner_item);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTextStatus.setAdapter(adapterStatus);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataAkun();
//                cekStatus();
            }
        });
        cekStatus();
    }

    private void saveDataAkun() {
        String id = userAplication.getUid();
        String statusOption = mTextStatus.getSelectedItem().toString();
        Akun user = new Akun(id, statusOption);
        saveData(user);
    }

    private void saveData(Akun akun) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("akun_status_data");
        myRef.child(userAplication.getUid()).setValue(akun);
    }

    private void cekStatus() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("akun_status_data");
        myRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void  onDataChange(DataSnapshot dataSnapshot) {
                Akun dataAkun = dataSnapshot.getValue(Akun.class);
                //for (DataSnapshot realDataSnapshot : dataSnapshot.getChildren()) {

                if (dataAkun != null) {
                    String statusAkun = dataAkun.getStatusAkun();
                    Log.i(TAG, "DataStatusAkun: " + statusAkun);
                    if (statusAkun.equals("Anak")) {
                        Intent intent = new Intent(StatusActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if (statusAkun.equals("Orang Tua")) {
                        Intent intent = new Intent(StatusActivity.this, MainOrtuActivity.class);
                        startActivity(intent);
                    }
                }
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
