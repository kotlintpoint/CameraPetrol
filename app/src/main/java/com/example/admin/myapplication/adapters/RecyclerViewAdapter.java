package com.example.admin.myapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.TimeLine;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private final ArrayList<HashMap<String, TimeLine>> timeLines;
    private final Context context;
    private boolean isPolice;
    public interface TimeLineClickListener{
        void onTimeLineDelete(HashMap<String,TimeLine> hashMap);
        void onTimeLineShare(TimeLine timeLine) throws IOException;
    }
    TimeLineClickListener listener;

    public RecyclerViewAdapter(Context context, ArrayList<HashMap<String, TimeLine>> timeLines, boolean isPolice, TimeLineClickListener listener) {
        this.timeLines=timeLines;
        this.context=context;
        this.isPolice=isPolice;
        this.listener=listener;
    }

    public boolean isPolice() {
        return isPolice;
    }

    public void setPolice(boolean police) {
        isPolice = police;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.timeline_row,viewGroup,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int position) {
        HashMap<String, TimeLine> hashMap = timeLines.get(position);
        String key=hashMap.keySet().iterator().next();
        TimeLine timeLine=hashMap.get(key);
        recyclerViewHolder.tvMessage.setText(timeLine.getDescription());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(timeLine.getImage()+".jpg");

        Glide.with(context)
                .load(storageReference)
                .into(recyclerViewHolder.imageView);
        recyclerViewHolder.videoView.setVisibility(View.GONE);
        if(!isPolice){
            recyclerViewHolder.btnDelete.setVisibility(View.GONE);
        }
        else{
            recyclerViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        listener.onTimeLineDelete(hashMap);
                }
            });
        }
        recyclerViewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onTimeLineShare(timeLine);
                } catch (IOException e) {
                    Log.i("Error",e.toString());
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeLines.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;
        TextView tvMessage;
        Button btnLike, btnShare, btnDelete;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            videoView=itemView.findViewById(R.id.videoView);
            tvMessage=itemView.findViewById(R.id.tvMessage);
            btnLike=itemView.findViewById(R.id.btnLike);
            btnShare=itemView.findViewById(R.id.btnShare);
            btnDelete=itemView.findViewById(R.id.btnDelete);
        }
    }
}
