package com.example.admin.myapplication.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.fragments.ComplainCategoryFragment;
import com.example.admin.myapplication.fragments.ProfileFragment;
import com.example.admin.myapplication.fragments.TimeLineFragment;
import com.example.admin.myapplication.model.User;
import com.example.admin.myapplication.other.HomeActivityListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity implements HomeActivityListener{

//    private TextView mTextMessage;
    boolean isProfileSet=false;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment=null;
            switch (item.getItemId()) {
                case R.id.navigation_timeline:
                    fragment=new TimeLineFragment();
                    break;
                case R.id.navigation_complain:
                    fragment=new ComplainCategoryFragment();
                    break;
                case R.id.navigation_profile:
                    fragment=new ProfileFragment();
                    break;
            }
            if(fragment!=null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame,fragment)
                        .commit();
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //mTextMessage =  findViewById(R.id.message);
        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        TimeLineFragment fragment = new TimeLineFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame,fragment)
                .commit();
    }

    private void checkProfile() {
        /*final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference("users").child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Fragment fragment;
                if(user==null) {
                    fragment=new ProfileFragment();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.frame,fragment)
                            .commit();

                }else{
                    isProfileSet=true;
                    fragment=new TimeLineFragment();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame,fragment)
                            .commit();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    @Override
    public void onLogout() {
        finish();
    }
}
