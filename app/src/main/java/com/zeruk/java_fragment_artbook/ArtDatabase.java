package com.zeruk.java_fragment_artbook;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Art.class}, version = 1, exportSchema = false)
public abstract class ArtDatabase extends RoomDatabase{

    public abstract ArtDao artDao();

}
