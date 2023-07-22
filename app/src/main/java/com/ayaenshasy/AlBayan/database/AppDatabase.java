package com.ayaenshasy.AlBayan.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ayaenshasy.AlBayan.model.Attendance;
import com.ayaenshasy.AlBayan.model.Exam;
import com.ayaenshasy.AlBayan.model.QuranChapter;
import com.ayaenshasy.AlBayan.model.quran.Verse;

@Database(entities = {QuranChapter.class, Verse.class, Attendance.class, Exam.class}, version = 2)
@TypeConverters({BooleanMapConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract QuranChapterDao quranChapterDao();

    public abstract SoraDao verseDao();

    public abstract AttendanceDao attendanceDao();

    public abstract ExamDao examDao();

}
