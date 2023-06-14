package com.ayaenshasy.bayan.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ayaenshasy.bayan.model.QuranChapter;
import com.ayaenshasy.bayan.model.quran.Verse;

@Database(entities = {QuranChapter.class, Verse.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract QuranChapterDao quranChapterDao();

    public abstract SoraDao verseDao();
}
