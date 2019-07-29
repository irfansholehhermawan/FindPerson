package org.d3ifcool.alert;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.d3ifcool.alert.adapter.OrtuAdapter;
import org.d3ifcool.alert.model.Ortu;
import org.d3ifcool.alert.model.Profile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddProfileActivity extends AppCompatActivity {
    private TextView mInputNama;
    private TextView mInputTempat;
    private TextView mInputTglLahir;
    private TextView mInputNoHP;
    private Spinner mTextJenisKelamin;
    private Spinner mTextStatus;

    private Calendar myCalendar = Calendar.getInstance();
    private String myFormat = "dd/MM/yyyy"; //In which you need put here
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    private FirebaseUser userAplication;
    private StorageReference storageReference;
    private Uri mImageLoader;
    private String idOrtu;
    private ListView ortuListView;
    private OrtuAdapter ortuAdapter;
    private ArrayList<Ortu> dataOrtu = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        userAplication = FirebaseAuth.getInstance().getCurrentUser();

//        ortuListView = (ListView) findViewById(R.id.list_contact_ortu);
//        ArrayList<Ortu> realData = readDataContact();
//        ortuAdapter = new OrtuAdapter(AddProfileActivity.this,realData);
//        ortuListView.setAdapter(ortuAdapter);
//        getDataOrtu();

        mInputNama = (TextView) findViewById(R.id.input_add_nama);
        mInputTempat = (TextView) findViewById(R.id.input_add_tempat);
        mInputTglLahir = (TextView) findViewById(R.id.input_add_tgl);
        mInputNoHP = (TextView) findViewById(R.id.input_add_nohp);

        mTextJenisKelamin = (Spinner) findViewById(R.id.add_jk);
        mTextStatus = (Spinner) findViewById(R.id.add_status);

        mInputTglLahir.setText(sdf.format(myCalendar.getTime()));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mInputTglLahir.setText(sdf.format(myCalendar.getTime()));
            }
        };

        mInputTglLahir.setText(sdf.format(myCalendar.getTime()));

        mInputTglLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayAdapter<CharSequence> adapterJK = ArrayAdapter.createFromResource(this,
                R.array.array_of_jk, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this,
                R.array.array_of_status, android.R.layout.simple_spinner_item);

        adapterJK.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTextJenisKelamin.setAdapter(adapterJK);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTextStatus.setAdapter(adapterStatus);

        downloadImage();
        getCompanySpesification();

        FloatingActionButton fabAddContactOrtu = (FloatingActionButton) findViewById(R.id.fab_add_contact_ortu);
        fabAddContactOrtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataContactOrtu(null);
            }
        });
    }

    private ArrayList<Ortu> readDataContact() {
        final ArrayList<Ortu> ortuData = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("contact_ortu");
        myRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ortuData.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String nama = postSnapshot.child("namaOrtu").getValue(String.class);
                    String no = postSnapshot.child("noHPOrtu").getValue(String.class);
                    ortuData.add(new Ortu(nama, no));
                    Log.i("Coba 1 ", "nama : "+postSnapshot.child("namaOrtu").getValue(String.class));
                    Log.i("Coba 2 ", "no HP : "+postSnapshot.child("noHPOrtu").getValue(String.class));
                    Log.i("Coba 3 ", "UID : "+userAplication.getUid());

                }
                ortuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("AddProfileActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        return ortuData;
    }

    private ArrayList<Ortu> getData() {
        final ArrayList<Ortu> currentOrtu = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference scheduleRef = database.getReference("contact_ortu");
        scheduleRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentOrtu.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Ortu data = postSnapshot.getValue(Ortu.class);
                    Log.i("AddProfileActivity", "contact ortu : " + data.getNamaOrtu());
                    Log.i("AddProfileActivity", "contact ortu : " + data.getNoHPOrtu());
                }
                ortuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        });
        return currentOrtu;
    }

    private void getDataOrtu() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference scheduleRef = database.getReference("contact_ortu");
        scheduleRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot realDataSnapshot : dataSnapshot.getChildren()) {

                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Ortu schedule = snap.getValue(Ortu.class);
                    if (schedule != null) {
                        dataOrtu.add(schedule);
                        Log.d("AddProfile","Data Ortu nama"+schedule.getNamaOrtu());
                        Log.d("AddProfile","Data Ortu nohp"+schedule.getNoHPOrtu());
                    }else{
                        Log.d("Add Profile","ZONK");
                    }
                }

                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * this method to create submit menu on the top corner
     *
     * @param menu apier menu
     * @return always true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menus = getMenuInflater();
        menus.inflate(R.menu.add_profile, menu);
        return true;
    }

    /**
     * this method to check selecting menu
     *
     * @param item selected menu
     * @return always true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_profile:
                saveProfileData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * this method for handling when user click save
     */
    private void saveProfileData() {
        String profileName = mInputNama.getText().toString();
        String profileTempat = mInputTempat.getText().toString();
        String profileTglLahir = mInputTglLahir.getText().toString();
        String profileNoHP = mInputNoHP.getText().toString();

        String jkOption = mTextJenisKelamin.getSelectedItem().toString();
        String statusOption = mTextStatus.getSelectedItem().toString();
        String urlPhoto = mImageLoader.toString();

        if (validationInputField(profileName, jkOption, profileTempat, profileTglLahir, profileNoHP, statusOption)) {
            Profile user = new Profile(profileName, jkOption, profileTempat, profileTglLahir, profileNoHP, statusOption, urlPhoto);
            saveData(user);
            setPrefForCompanyData();
            NavUtils.navigateUpFromSameTask(AddProfileActivity.this);
        }
    }

    /**
     * this method for Saving data to firebase
     *
     * @param profile data Item will be save
     */
    private void saveData(Profile profile) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("profile_data");
        myRef.child(userAplication.getUid()).setValue(profile);
    }


    /**
     * this method for set preferences of add data
     */
    private void setPrefForCompanyData() {
        PrefManager prefManager = new PrefManager(this);
        prefManager.setCompleteInputCompanyData(true);
    }

    private boolean validationInputField(String profileName, String profileJK, String profileTempat, String profileTglLahir, String profileNoHP, String profileStatus) {
        boolean isValid = true;
        if (profileName.equals("")) {
            mInputNama.setError(getText(R.string.empty_message));
            isValid = false;
        }
        if (profileTempat.equals("")) {
            mInputTempat.setError(getText(R.string.empty_message));
            isValid = false;
        }
        if (profileTglLahir.equals("")) {
            mInputTglLahir.setError(getText(R.string.empty_message));
            isValid = false;
        }
        if (profileNoHP.equals("")) {
            mInputNoHP.setError(getText(R.string.empty_message));
            isValid = false;
        }
        return isValid;
    }

    private void getCompanySpesification(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myItemRef = database.getReference("profile_data");
        myItemRef.child(userAplication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile user = dataSnapshot.getValue(Profile.class);
                if(user != null) {
                    setEditCompanySpesification(user, user.getProfileJK(), user.getProfileStatus());
                    Log.d("Name",user.getProfileName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setEditCompanySpesification(Profile user, String areas1, String areas2) {
        mInputNama.setText(user.getProfileName());
        mInputTempat.setText(user.getProfileTempat());
        mInputTglLahir.setText(user.getProfileTglLahir());
        mInputNoHP.setText(user.getProfileNoHP());

        ArrayAdapter<CharSequence> adapterJK = ArrayAdapter.createFromResource(this,
                R.array.array_of_jk, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this,
                R.array.array_of_status, android.R.layout.simple_spinner_item);

        adapterJK.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTextJenisKelamin.setAdapter(adapterJK);
        mTextStatus.setAdapter(adapterStatus);
        mTextJenisKelamin.setSelection(adapterJK.getPosition(areas1));
        mTextStatus.setSelection(adapterStatus.getPosition(areas2));
    }


    private void downloadImage(){
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference
                .child("File/"+ userAplication.getUid())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    public void addDataContactOrtu(final Ortu ortu) {
        View v = this.getLayoutInflater().inflate(R.layout.activity_add_button_help,null);
        final EditText inputNameOrtu = (EditText) v.findViewById(R.id.input_name_ortu);
        final EditText inputNoHPOrtu = (EditText) v.findViewById(R.id.input_nohp_ortu);

        final Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
        final Button btnSave = (Button) v.findViewById(R.id.btn_save);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View title = this.getLayoutInflater().inflate(R.layout.text_view_customize,null);

        final ImageView icon = (ImageView) title.findViewById(R.id.custom_icon_of_title);
        final TextView text = (TextView) title.findViewById(R.id.custom_text_of_title);


        if(ortu != null) {
            inputNameOrtu.setText(ortu.getNamaOrtu());
            inputNoHPOrtu.setText(ortu.getNoHPOrtu());
            icon.setImageResource(R.drawable.ic_person_add_black_24dp);
            text.setText("Add Contact Orang Tua");

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
                    final String nameData = inputNameOrtu.getText().toString();
                    String noHPData = inputNoHPOrtu.getText().toString();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference ortuRef = database.getReference("contact_ortu");

                    if(nameData.matches("") || noHPData.matches("")){
                        Toast.makeText(AddProfileActivity.this, "Save Failed : Fields can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        ortuRef.child(userAplication.getUid()).setValue(new Ortu(nameData,noHPData));
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
            icon.setImageResource(R.drawable.ic_person_add_black_24dp);
            text.setText("Add Contact Orang Tua");

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
                    final String nameData = inputNameOrtu.getText().toString();
                    final String noHPData = inputNoHPOrtu.getText().toString();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference ortuRef = database.getReference("contact_ortu");

                    if(nameData.matches("") || noHPData.matches("")){
                        Toast.makeText(AddProfileActivity.this, "Save Failed : Fields can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        ortuRef.child(userAplication.getUid()).setValue(new Ortu(nameData,noHPData));
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
}
