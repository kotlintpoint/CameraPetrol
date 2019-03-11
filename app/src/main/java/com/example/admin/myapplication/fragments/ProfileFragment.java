package com.example.admin.myapplication.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.User;
import com.example.admin.myapplication.other.Const;
import com.example.admin.myapplication.other.HomeActivityListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match

    TextView tvChangePassword;
    TextInputLayout input_layout_firstname,input_last_name,input_Aadhar_Number,
    input_Mobile_Number,input_layout_email;
    EditText etFirstName,etLastName,etAadharNumber,etMobileNumber, etEmail;
    Button Submit, btnLogout;
    RadioGroup rgType;
    RadioButton rbPolice, rbCitizen;

    Context context;
    HomeActivityListener listener;
    private FirebaseAuth mAuth;
    private User user;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener= (HomeActivityListener) context;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_Profile);
        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        context=getActivity();
        tvChangePassword=view.findViewById(R.id.tvChangePassword);
        input_layout_firstname=view.findViewById(R.id.input_layout_firstname);
        input_last_name=view.findViewById(R.id.input_last_name);
        input_Aadhar_Number=view.findViewById(R.id.input_Aadhar_Number);
        input_Mobile_Number=view.findViewById(R.id.input_Mobile_Number);

        etFirstName=view.findViewById(R.id.input_first_name);
        etLastName=view.findViewById(R.id.LastName);
        etAadharNumber=view.findViewById(R.id.AadharNumber);
        etMobileNumber=view.findViewById(R.id.MobileNumber);
        etEmail=view.findViewById(R.id.input_email);

        Submit=view.findViewById(R.id.Submit);
        rgType=view.findViewById(R.id.rgType);
        rbPolice=view.findViewById(R.id.rbPolice);
        rbCitizen=view.findViewById(R.id.rbCitizen);
        Submit.setOnClickListener(this);
        btnLogout=view.findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*SharedPreferences sharedPreferences=context.getSharedPreferences(Const.FILE_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.clear();
                editor.commit();*/
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                listener.onLogout();
            }
        });
        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordMail();
            }
        });
        // Inflate the layout for this fragment
        readUserProfile();
        return view;
    }

    private void changePasswordMail() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.getInstance().sendPasswordResetEmail(currentUser.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Change Password Mail Sent", Toast.LENGTH_SHORT).show();;
                        }
                    }
                });
    }

    private void readUserProfile() {
        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference("users").child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                if(user!=null) {
                    etFirstName.setText(user.getFirstName());
                    etLastName.setText(user.getLastName());
                    etAadharNumber.setText(user.getAadhar());
                    etMobileNumber.setText(user.getMobile());
                    if(user.getType().equalsIgnoreCase("police")){
                        rbPolice.setChecked(true);
                    }else{
                        rbCitizen.setChecked(true);
                    }
                    etEmail.setText(currentUser.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onClick(View view) {
        String fname=etFirstName.getText().toString();
        String lname=etLastName.getText().toString();
        String Aadharnum=etAadharNumber.getText().toString();
        String mnum=etMobileNumber.getText().toString();
        String type="0";

        if(fname.equals("")) {
            input_layout_firstname.setError("Enter First Name");
            return;
        }
        if(lname.equals("")) {
            input_last_name.setError("Enter last Name");
            return;
        }
        if(Aadharnum.equals("")) {
            input_Aadhar_Number.setError("Enter Aadhar Number");
            return;
        }
        if(mnum.equals("")) {
            input_Mobile_Number.setError("Enter Mobile Number");
            return;
        }
        switch (rgType.getCheckedRadioButtonId()){
            case R.id.rbPolice:
                type="1";
                break;
            case R.id.rbCitizen:
                type="2";
                break;
        }
        User user=new User();
        user.setType(type);
        user.setFirstName(etFirstName.getText().toString());
        user.setLastName(etLastName.getText().toString());
        user.setAadhar(etAadharNumber.getText().toString());
        user.setMobile(etMobileNumber.getText().toString());
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference(Const.USERS)
                .child(currentUser.getUid());
        myRef.setValue(user);
    }
}
