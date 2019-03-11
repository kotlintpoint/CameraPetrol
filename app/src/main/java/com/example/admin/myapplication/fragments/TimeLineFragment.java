package com.example.admin.myapplication.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.activities.NewTimeLineActivity;
import com.example.admin.myapplication.adapters.RecyclerViewAdapter;
import com.example.admin.myapplication.model.TimeLine;
import com.example.admin.myapplication.model.User;
import com.example.admin.myapplication.other.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeLineFragment extends Fragment implements RecyclerViewAdapter.TimeLineClickListener {
    RecyclerView recyclerView;
    ArrayList<HashMap<String,TimeLine>> timeLines;
    private User user;
    Context context;
    private FirebaseDatabase database;
    private ProgressDialog pd;
    private boolean isPolice;
    private RecyclerViewAdapter adapter;
    private File localFile;

    public TimeLineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.title_TimeLine);
        View view=inflater.inflate(R.layout.fragment_time_line, container, false);
        // Inflate the layout for this fragment
        context=getActivity();
        recyclerView=view.findViewById(R.id.recyclerView);

        //timeLines=new ArrayList<>();
        /*timeLines.add(new TimeLine(1,R.drawable.most_wanted,-1));
        timeLines.add(new TimeLine(1,-1,R.raw.video_sample_1));
        timeLines.add(new TimeLine(1,R.drawable.wanted,-1));*/
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

       readTimeLines();
        return view;

    }

    private void readTimeLines() {
        database= FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Const.TIMELINE);
        pd= ProgressDialog.show(context,"","");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd.dismiss();
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                timeLines=new ArrayList<>();
                for (DataSnapshot ds :iterable) {
                    String key=ds.getKey();
                    TimeLine timeLine=ds.getValue(TimeLine.class);
                    HashMap<String,TimeLine> hm=new HashMap<>();
                    hm.put(key,timeLine);
                    timeLines.add(hm);
                }
                setTimeLineAdapter(timeLines);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
                Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        checkUser();
    }

    private void checkUser() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = database.getReference(Const.USERS).child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                if(user!=null) {
                    if(user.getType().equalsIgnoreCase("police")){
                        isPolice=true;
                        getActivity().invalidateOptionsMenu();
                        if(adapter!=null) {
                            adapter.setPolice(isPolice);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUser();
    }

    private void setTimeLineAdapter(ArrayList<HashMap<String, TimeLine>> timeLines) {
        adapter=new RecyclerViewAdapter(context,timeLines,isPolice,this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        if(isPolice)
            inflater.inflate(R.menu.new_item,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new:
                Intent intent=new Intent(context, NewTimeLineActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeLineDelete(HashMap<String, TimeLine> hashMap) {
        showConfirmDialog(hashMap);
    }

    @Override
    public void onTimeLineShare(TimeLine timeLine) throws IOException {
        //final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(timeLine.getImage()+".jpg");
        localFile = Environment.getExternalStorageDirectory();
        localFile=new File(localFile,"test.jpg");

        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                if(localFile.exists()) {
                    intentShareFile.setType("image/jpg");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+localFile.getAbsolutePath()));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,R.string.app_name);
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, timeLine.getDescription());
                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmDialog(HashMap<String, TimeLine> hashMap) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Decision")
                .setMessage("Are you sure want to delete this timeline?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String key=hashMap.keySet().iterator().next();
                        TimeLine timeLine=hashMap.get(key);
                        removeTimeLine(key,timeLine);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void removeTimeLine(String key, TimeLine timeLine) {
        DatabaseReference reference = database.getReference().child(Const.TIMELINE).child(key);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Entry Successfully Removed", Toast.LENGTH_SHORT).show();
                    removeTimeLineImage(timeLine);
                }
            }
        });
    }

    private void removeTimeLineImage(TimeLine timeLine) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(timeLine.getImage()+".jpg");
        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Image Successfully Removed", Toast.LENGTH_SHORT).show();
                    removeTimeLineImage(timeLine);
                }
            }
        });
    }
}
