package com.invincible.securitycamera;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    private FirebaseAuth mAuth;
    public void submit(View view)
    {

        if(TextUtils.isEmpty(emailInput.getText().toString()) || TextUtils.isEmpty(passwordInput.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Enter Email and Password",Toast.LENGTH_SHORT).show();
        }
        else {
            String username = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(
                                        @NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),
                                                "Login successful!!",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        Intent myIntent = new Intent(MainActivity.this, ListActivity.class);
                                        myIntent.putExtra("key", "Login"); //Optional parameters
                                        MainActivity.this.startActivity(myIntent);
                                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Login failed!!",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginbutton);
    }

}
