package com.example.admin.myapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.other.Const;
import com.example.admin.myapplication.other.MyAsyncTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class SigninActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btLogin, btSignUp;
    RadioGroup rgType;
    RadioButton rbPolice, rbCitizen;
    private static final int VERIFY = 1;
    Context context;
    private SigninActivity Context;
    private FirebaseAuth mAuth;
    TextView tvForgetPassword;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        context = this;
        mAuth = FirebaseAuth.getInstance();

        tvForgetPassword=findViewById(R.id.tvForgetPassword);
        tvForgetPassword.setVisibility(View.GONE);

        rgType=findViewById(R.id.rgType);
        rbPolice=findViewById(R.id.rbPolice);
        rbCitizen=findViewById(R.id.rbCitizen);
        btLogin = findViewById(R.id.btLogin);
        btSignUp = findViewById(R.id.btSignUp);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RegActivity.class);
                startActivity(intent);
                //signup();
            }
        });
        tvForgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=etUsername.getText().toString();
                if(email.equals("")){
                    Toast.makeText(context, "Enter Email Id and Then Press Forget Password...",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                sendForgetPasswordLink(email);
            }
        });
        btLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        //checkLogin();
        FirebaseUser user=mAuth.getCurrentUser();
        updateUI(user);
    }

    private void sendForgetPasswordLink(String email) {

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Change Password Mail Sent", Toast.LENGTH_SHORT).show();;
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null){
            //Toast.makeText(context, currentUser.getUid(), Toast.LENGTH_SHORT).show();
            navigateToDashboard();
        }
    }

  /*  private void signup() {
        String email=etUsername.getText().toString();
        String password=etPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }*/

    private void login() {
        String username=etUsername.getText().toString();
        String password=etPassword.getText().toString();
        pd=ProgressDialog.show(context,"","");
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            tvForgetPassword.setVisibility(View.VISIBLE);
                            updateUI(null);
                        }
                    }
                });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
        finish();
    }


}