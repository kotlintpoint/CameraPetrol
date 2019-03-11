package com.example.admin.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.admin.myapplication.R;

public class timelineActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Context context;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        context=this;
        recyclerView=findViewById(R.layout.activity_timeline);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        prepareData();
    }

    private void prepareData() {

    }
}
