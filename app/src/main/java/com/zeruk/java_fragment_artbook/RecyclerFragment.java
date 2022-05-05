package com.zeruk.java_fragment_artbook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class RecyclerFragment extends Fragment {

    View _view;


    ArtAdapter artAdapter;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    ArtDao artDao;
    ArtDatabase artDatabase;

    public RecyclerFragment() {
        // Required empty public constructor
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _view = view;
        artDatabase = Room.databaseBuilder(view.getContext(), ArtDatabase.class, "Arts").build();
        artDao = artDatabase.artDao();
        compositeDisposable.add(artDao.getArtWithNameAndId().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(RecyclerFragment.this::getData));


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }



    public void getData(List<Art> arts) {

        RecyclerView recyclerView = _view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(_view.getContext()));
        artAdapter = new ArtAdapter(arts);
        recyclerView.setAdapter(artAdapter);

        artAdapter.notifyDataSetChanged();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.clear();
    }
}