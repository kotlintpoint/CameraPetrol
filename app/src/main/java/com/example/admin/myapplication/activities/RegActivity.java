package com.example.admin.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.User;
import com.example.admin.myapplication.other.Const;
import com.example.admin.myapplication.other.MyAsyncTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegActivity extends AppCompatActivity implements MyAsyncTask.AsyncTaskListener{


    EditText etFirstName, etLastName,etAadharNumber,
            etPassword, etMobile, etEmail;
    Context context;
    RadioGroup rgType;
    RadioButton rbPolice, rbCitizen;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        context=this;
        mAuth = FirebaseAuth.getInstance();
        rgType=findViewById(R.id.rgType);
        rbPolice=findViewById(R.id.rbPolice);
        rbCitizen=findViewById(R.id.rbCitizen);
        etFirstName=findViewById(R.id.etFirstName);
        etLastName=findViewById(R.id.etLastName);
        etAadharNumber=findViewById(R.id.etAadharNumber);
       etPassword=findViewById(R.id.etPassword);
       etEmail=findViewById(R.id.etEmail);
       etMobile=findViewById(R.id.etMobileNumber);

    }

    public void submit(View view) {

        String email=etEmail.getText().toString();
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

    }

    private void updateUI(FirebaseUser user) {
        if(user!=null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference(Const.USERS).child(user.getUid());
            User mUser = new User();
            mUser.setFirstName(etFirstName.getText().toString());
            mUser.setLastName(etLastName.getText().toString());
            mUser.setAadhar(etAadharNumber.getText().toString());
            String type="";
            switch (rgType.getCheckedRadioButtonId()){
                case R.id.rbPolice:
                    type=rbPolice.getText().toString();
                    break;
                case R.id.rbCitizen:
                    type=rbCitizen.getText().toString();
                    break;
            }
            mUser.setType(type);
            mUser.setMobile(etMobile.getText().toString());
            reference.setValue(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        Toast.makeText(context, "Registration Success!!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(context, "Registration Fail!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public void asyncCallBack(int requestCode, String data) {
        Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
    }
}
