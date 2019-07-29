package org.d3ifcool.alert;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.d3ifcool.alert.model.Akun;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;

    private EditText editTextEmailAddress;
    private EditText editTextPassword;
    private EditText editTextPasswordConf;
    private Button btnRegisterWithEmail;
    private TextInputLayout inputLayoutEmailAddress;
    private TextInputLayout inputLayoutPassword;
    private ProgressBar progressBarRegister;
    private TextInputLayout inputLayoutPasswordConf;
    private Spinner mTextStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        progressBarRegister = (ProgressBar) findViewById(R.id.check_register);
        editTextEmailAddress = (EditText) findViewById(R.id.input_email);
        editTextPassword = (EditText) findViewById(R.id.input_password);
        editTextPasswordConf = (EditText) findViewById(R.id.input_password_conf);
        btnRegisterWithEmail = (Button) findViewById(R.id.btn_register_email);
        inputLayoutEmailAddress = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutPasswordConf = (TextInputLayout) findViewById(R.id.input_layout_password_conf);
        progressBarRegister.setVisibility(View.GONE);

        btnRegisterWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarRegister.setVisibility(View.VISIBLE);
                String email = editTextEmailAddress.getText().toString();
                String password = editTextPassword.getText().toString();
                String passwordConf = editTextPasswordConf.getText().toString();
                if(validationEmailAndEmail(email, password, passwordConf)){
                    register(email, password);
                }
            }
        });
    }

    /**
     * validating input from user
     * @param emailAddress user email addres
     * @param password user password
     * @return valid or not email and password
     */
    private boolean validationEmailAndEmail(String emailAddress, String password, String passwordConf) {
        if(!emailAddress.contains("@") || emailAddress.equals("")){
            progressBarRegister.setVisibility(View.GONE);
            if(emailAddress.equals("")){
                inputLayoutEmailAddress.setError("Email address can't empty!");
            }
            else if(!emailAddress.contains("@")){
                inputLayoutEmailAddress.setError("Email must contain \"@\" !");
            }
            return false;
        }
        else if(password.length() <6 || password.equals("")){
            inputLayoutEmailAddress.setErrorEnabled(false);
            progressBarRegister.setVisibility(View.GONE);
            if (password.equals("")){
                inputLayoutPassword.setError("Password can't empty!");
            }
            else if(password.length() <6){
                inputLayoutPassword.setError("Password must more than 6 character!");
            }
            return false;
        }
        else if(!password.equals(passwordConf)){
            progressBarRegister.setVisibility(View.GONE);
            inputLayoutEmailAddress.setErrorEnabled(false);
            inputLayoutPassword.setErrorEnabled(false);
            if(!passwordConf.equals(password)){
                inputLayoutPasswordConf.setError("Password doesn't match!");
            }
            return false;
        }
        else {
            inputLayoutEmailAddress.setErrorEnabled(false);
            inputLayoutPassword.setErrorEnabled(false);
            return true;
        }
    }

    private void register(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            progressBarRegister.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, R.string.confirm_register,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
}
