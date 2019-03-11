package com.example.admin.myapplication.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.activities.ComplainListActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ComplainCategoryFragment extends Fragment implements ComplainAdapter.ComplainAdapterListener {


    private User user;

    public ComplainCategoryFragment() {
        // Required empty public constructor
    }
    FirebaseDatabase database;
    RecyclerView recyclerView;
    ArrayList<HashMap<String,String>> categories;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_Complain_category);
        setHasOptionsMenu(true);
        context=getActivity();
        database=FirebaseDatabase.getInstance();
        View view=inflater.inflate(R.layout.fragment_complain, container, false);
        // Inflate the layout for this fragment
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        readComplainCategories();
        return view;
    }

    private void readComplainCategories() {

        DatabaseReference reference = database.getReference().child(Const.COMPLAIN_CATEGORY);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                categories=new ArrayList<>();
                for (DataSnapshot ds :iterable) {
                    String key=ds.getKey();
                    String category=ds.getValue(String.class);
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put(key,category);
                    categories.add(hm);
                }
                setCategoryAdapter(categories);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCategoryAdapter(ArrayList<HashMap<String, String>> categories) {
        ComplainAdapter adapter=new ComplainAdapter(categories,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onComplainClick(HashMap<String,String> category) {
        String key=category.keySet().iterator().next();
        String complain=category.get(key);
        Intent intent=new Intent(getActivity(), ComplainListActivity.class);
        intent.putExtra("key",key);
        intent.putExtra("complain",complain);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = database.getReference(Const.USERS).child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                if(user!=null) {
                    if(user.getType().equalsIgnoreCase("police")){
                        inflater.inflate(R.menu.new_item,menu);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        super.onCreateOptionsMenu(menu, inflater);
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
        View view = getLayoutInflater().inflate(R.layout.new_category_input,null);
        Button btnSave=view.findViewById(R.id.btnSave);
        Button btnCancel=view.findViewById(R.id.btnCancel);
        final EditText etComplainCategory = view.findViewById(R.id.etNewComplainCategory);
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
        for (HashMap<String,String> hashMap:categories) {
            String key=hashMap.keySet().iterator().next();
            String value=hashMap.get(key);
            if(value.equalsIgnoreCase(category)){
                Toast.makeText(context, category+" Category already Exist!!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        DatabaseReference reference = database.getReference().child(Const.COMPLAIN_CATEGORY).push();
        reference.setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Category Saved Successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, "Category not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
