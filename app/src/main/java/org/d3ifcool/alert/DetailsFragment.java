package org.d3ifcool.alert;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailsFragment extends Fragment {
    private static final String TAG = "DetailsActivity";
    private String tName;
    private String tUrl;
    private ProgressBar progressBar;
    ImageView imageView;
    PhotoViewAttacher mAttacher;
    public DetailsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_details,container, false);

//        String title = getIntent().getStringExtra("title");
//        String bitmap = getIntent().getStringExtra("image");
//        idImage = getIntent().getStringExtra("ids");

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_detail_picture);
        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tName = bundle.getString("title");
            tUrl = bundle.getString("bitmap");

            TextView titleTextView = (TextView) rootView.findViewById(R.id.title);
            titleTextView.setText(tName);
            imageView = (ImageView) rootView.findViewById(R.id.image);
            Picasso.with(getContext()).load(tUrl).into(imageView);

            Log.i(TAG, "url: "+tName);
            Log.i(TAG, "url: "+tUrl);

//        SGD = new ScaleGestureDetector(this, new ScaleListener());
            //  Picasso.with(this).load(bitmap).into(imageView);
        }

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                zoom();
//            }
//        });
        return rootView;
    }

    private void zoom(){
        mAttacher = new PhotoViewAttacher(imageView);
        //        mAttacher.update();
    }

//     @Override
//     public boolean onCreateOptionsMenu(Menu menu) {
//         MenuInflater menus = getMenuInflater();
//         menus.inflate(R.menu.detail_picture, menu);
//         return true;
//     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_picture:
                dialogDeleteData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dialogDeleteData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_data_question)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteData();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    private void deleteData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myDeletMastereRef = database.getReference("image");
        //myDeletMastereRef.child(idImage).removeValue();
    }



}
