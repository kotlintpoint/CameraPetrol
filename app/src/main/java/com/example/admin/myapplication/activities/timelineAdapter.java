package com.example.admin.myapplication.activities;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class timelineAdapter  extends RecyclerView.Adapter<timelineAdapter.TimeViewHolder>{



    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder timeViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder{
        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
