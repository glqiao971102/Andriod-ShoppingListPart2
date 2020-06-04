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

public class MainActivity extends AppCompatActivity {

//    all Variable
    private EditText email;
    private EditText password;
    private Button btnLogin;
    private TextView signUp;

    private FirebaseAuth myAuth;
    private ProgressDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();
//        Pop up a message
        messageDialog = new ProgressDialog(this);
//      find and match the id in registration.xml
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        signUp = findViewById(R.id.signup_txt);

//      for button event handler when user click the button,
//      if the email and password is empty, it will pop up a error message
//      else everthings is ok, it will show Successful, but if the user authenation is didnt match the firebase in myAuth, it will pop up a error
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Hey, Please fill in your email");
                    return;
                }

                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Hey, Please fill in your password dude");
                    return;
                }

                messageDialog.setMessage("Loading now");
                messageDialog.show();

//              this part is to check the email and the password is correct, if it is correct it will go to the HOMEactivity page
//              else it will pop up the error message
                myAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(),"Login Sucessful", Toast.LENGTH_SHORT).show();
                            messageDialog.dismiss();
                        }else{

                            Toast.makeText(getApplicationContext(),"Email or Password is incorrect, Try again lol", Toast.LENGTH_SHORT).show();
                            messageDialog.dismiss();
                        }
                    }
                });


            }
        });

//      if the user click the registration, it will link to the register page
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

    }
}
