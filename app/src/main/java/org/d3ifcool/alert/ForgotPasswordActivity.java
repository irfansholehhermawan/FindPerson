package org.d3ifcool.alert;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private TextInputLayout inputEmail;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText) findViewById(R.id.input_email_forgot_pass);
        inputEmail = (TextInputLayout) findViewById(R.id.input_layout_email_forgot_pass);
        btnSubmit = (Button) findViewById(R.id.btn_email_forgot_pass);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curEmail = email.getText().toString();
                if(emailValidation(curEmail)){
                    sentEmailVerification(curEmail);
                }
            }
        });
    }

    private boolean emailValidation(String emailAddress) {
        if(!emailAddress.contains("@") || emailAddress.equals("")){
            if(emailAddress.equals("")){
                inputEmail.setError("Email address can't empty!");
            }
            else if(!emailAddress.contains("@")){
                inputEmail.setError("Email must contain \"@\" !");
            }
            return false;
        }
        else{
            inputEmail.setErrorEnabled(false);
            return true;
        }
    }

    private void sentEmailVerification(final String email) {
        final View v = getLayoutInflater().inflate(R.layout.activity_show_request_forgot_password,null);
        final TextView massage = (TextView) v.findViewById(R.id.massage_forgot_password);
        final Button btnOke = (Button) v.findViewById(R.id.btn_ok);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(!email.equals("")){
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        String curMassage = "Please check your email \n" + email;
                        massage.setText(curMassage);

                        TextView title = new TextView(ForgotPasswordActivity.this);
                        // You Can Customise your Title here
                        title.setText("SUCCESS");
                        title.setBackgroundColor(Color.LTGRAY);
                        title.setPadding(16, 16, 16, 16);
                        title.setGravity(Gravity.CENTER);
                        title.setTextColor(Color.BLACK);
                        title.setTextSize(26);

                        builder.setCustomTitle(title).setView(v);
                        btnOke.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        builder.create().show();
                    }else{
                        String curMassage = email + "\n doesn't exist";
                        massage.setText(curMassage);

                        TextView title = new TextView(ForgotPasswordActivity.this);
                        // You Can Customise your Title here
                        title.setText("FAILED");
                        title.setBackgroundColor(Color.RED);
                        title.setPadding(16, 16, 16, 16);
                        title.setGravity(Gravity.CENTER);
                        title.setTextColor(Color.WHITE);
                        title.setTextSize(26);


                        //builder.create().show();
                        builder.setCustomTitle(title).setView(v);
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        btnOke.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });

                    }
                }
            });
        }
    }
}
