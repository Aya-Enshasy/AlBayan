package com.ayaenshasy.AlBayan.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.ayaenshasy.AlBayan.model.QuranChapter;

import java.util.List;

@Dao
public interface QuranChapterDao {
    @Query("SELECT * FROM quranchapter")
    List<QuranChapter> getAllChapters();

    @Insert
    void insertAll(List<QuranChapter> chapters);

}
