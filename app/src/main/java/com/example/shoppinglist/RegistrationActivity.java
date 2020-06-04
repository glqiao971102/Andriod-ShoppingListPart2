package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class RegistrationActivity extends AppCompatActivity {

    //    all Variable session
    private EditText email;
    private EditText password;
    private TextView signin;
    private Button btnReg;

    private FirebaseAuth myAuth;
    private ProgressDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        myAuth = FirebaseAuth.getInstance();

//        for pop up the message
        messageDialog = new ProgressDialog(this);

//        link all the variable to the registration ID
        email = findViewById(R.id.email_reg);
        password = findViewById(R.id.password_reg);

        btnReg = findViewById((R.id.btn_reg));
        signin = findViewById(R.id.signin_txt);

//        check the btnReg to make sure the input is not empty
//        if it is empty, show the error message
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Hey, please fill in email");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)){
                    password.setError("Hey, please fill in password");
                    return;
                }
//              Once is success it will return Succesful, it the userName is same, it will show the error
                messageDialog.setMessage("Processing..");
                messageDialog.show();

                myAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Wow, Successful", Toast.LENGTH_SHORT).show();
                            messageDialog.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(), "No...Something wrong, try again", Toast.LENGTH_SHORT).show();
                            messageDialog.dismiss();
                        }
                    }
                });

            }
        });

//        Once the user successful create an account, it will go to the maina ctivity page
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        }
    }
