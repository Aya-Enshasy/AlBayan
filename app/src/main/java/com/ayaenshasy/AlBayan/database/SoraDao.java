package com.ayaenshasy.AlBayan.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.ayaenshasy.AlBayan.model.quran.Verse;

import java.util.List;

@Dao
public interface SoraDao {
    @Query("SELECT * FROM Verse")
    List<Verse> getAllVerses();

    @Insert
    void insertAll(List<Verse> Verses);
}
