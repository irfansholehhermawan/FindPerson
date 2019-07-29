package org.d3ifcool.alert;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";
    String idImage;
    private ProgressBar progressBar;
    ImageView imageView;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String title = getIntent().getStringExtra("title");
        String bitmap = getIntent().getStringExtra("image");
        idImage = getIntent().getStringExtra("ids");

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_detail_picture);
        progressBar.setVisibility(View.VISIBLE);
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);
        imageView = (ImageView) findViewById(R.id.image);
//        SGD = new ScaleGestureDetector(this, new ScaleListener());
        Picasso.with(this).load(bitmap).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom();
            }
        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
