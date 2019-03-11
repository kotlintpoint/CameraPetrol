package com.example.admin.myapplication.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.adapters.ComplainAdapter;
import com.example.admin.myapplication.model.User;
import com.example.admin.myapplication.other.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ComplainListActivity extends AppCompatActivity implements ComplainAdapter.ComplainAdapterListener {
    RecyclerView recyclerView;
    Context context;
    private String catKey, category;
    private ArrayList<HashMap<String, String>> complains;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complain);
        setTitle(R.string.title_Complain);
        context=this;
        /*intent.putExtra("key",key);
        intent.putExtra("complain",complain);*/
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        Intent intent=getIntent();
        catKey=intent.getStringExtra("key");
        category=intent.getStringExtra("complain");

        readComplains();
    }

    private void readComplains() {
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        String uid= FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = database.getReference()
                .child(Const.COMPLAIN);
                //.child(uid);
                //.child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                complains=new ArrayList<>();
                for (DataSnapshot ds :iterable) {//users
                    String key=ds.getKey();
                    for (DataSnapshot ds1 :ds.getChildren()) {//complain category
                        String key1=ds1.getKey();
                        if(catKey.equals(key1)) {
                            for (DataSnapshot ds2 : ds1.getChildren()) {// complain
                                String key2 = ds2.getKey();

                                String category = ds2.getValue(String.class);
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put(key2, category);
                                complains.add(hm);
                            }
                        }
                    }
                }
                setAdapter(complains);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(ArrayList<HashMap<String, String>> complains) {
        ComplainAdapter adapter=new ComplainAdapter(complains,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = database.getReference(Const.USERS).child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                if(user!=null) {
                    if(!user.getType().equalsIgnoreCase("police")){
                        getMenuInflater().inflate(R.menu.new_item,menu);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new:
                showInputDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showInputDialog() {
        View view = getLayoutInflater().inflate(R.layout.new_complain_input,null);
        Button btnSave=view.findViewById(R.id.btnSave);
        Button btnCancel=view.findViewById(R.id.btnCancel);
        final EditText etComplainCategory = view.findViewById(R.id.etNewComplainCategory);
        etComplainCategory.setHint("Enter Complain Here");
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.show();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category=etComplainCategory.getText().toString();
                saveCategory(dialog,category);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void saveCategory(final AlertDialog dialog, String category) {
        /*for (HashMap<String,String> hashMap:categories) {
            String key=hashMap.keySet().iterator().next();
            String value=hashMap.get(key);
            if(value.equalsIgnoreCase(category)){
                Toast.makeText(context, category+" Category already Exist!!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }*/
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        String uid= FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = database.getReference()
                .child(Const.COMPLAIN).child(uid).child(catKey).push();
        reference.setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Complain Saved Successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, "Complain not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onComplainClick(HashMap<String, String> complain) {

    }
}
