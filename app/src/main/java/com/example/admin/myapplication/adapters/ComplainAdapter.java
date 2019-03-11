package com.example.admin.myapplication.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.Complain;

import java.util.ArrayList;
import java.util.HashMap;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ComplainViewHolder> {
    private final ArrayList<HashMap<String, String>> categories;

    public interface ComplainAdapterListener {
        void onComplainClick(HashMap<String,String> complain);
    }
    ComplainAdapterListener listener;

    public ComplainAdapter(ArrayList<HashMap<String, String>> categories, ComplainAdapterListener listener) {
        this.categories=categories;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ComplainViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.complain_row,viewGroup,false);
        ComplainViewHolder viewHolder=new ComplainViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComplainViewHolder complainViewHolder, int i) {
        final HashMap<String,String> hashMap=categories.get(i);
        String key=hashMap.keySet().iterator().next();
        final String category=hashMap.get(key);
        complainViewHolder.textView.setText(category);
        complainViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onComplainClick(hashMap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ComplainViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ComplainViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.tvType);
        }
    }
}
