package com.zeruk.java_fragment_artbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

    List<Art> artList;

    public ArtAdapter(List<Art> artList)
    {
        this.artList = artList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new ArtHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.recyclerRow);
        textView.setText(artList.get(position).artname);
        int id = artList.get(position).id;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("addOrSee", 1);
                bundle.putInt("id", id);
                Navigation.findNavController(view).navigate(R.id.action_recyclerFragment_to_contentFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artList.size();
    }

    public class ArtHolder extends RecyclerView.ViewHolder
    {
        View view;
        public ArtHolder(View view)
        {
            super(view.getRootView());
            this.view = view;
        }
    }

}
