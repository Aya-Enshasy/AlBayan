package com.ayaenshasy.bayan.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ayaenshasy.bayan.model.QuranChapter;

import java.util.List;

@Dao
public interface QuranChapterDao {
    @Query("SELECT * FROM quranchapter")
    List<QuranChapter> getAllChapters();

    @Insert
    void insertAll(List<QuranChapter> chapters);

}
