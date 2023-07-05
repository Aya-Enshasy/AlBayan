package com.ayaenshasy.AlBayan.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ayaenshasy.AlBayan.model.Attendance;
import com.ayaenshasy.AlBayan.model.QuranChapter;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Query("SELECT * FROM Attendance")
    List<Attendance> readAll();
    @Insert
    public long insert(Attendance attendance);

    @Insert
    void insertAll(List<Attendance> chapters);

    @Delete
    void delete(Attendance attendance);

    @Query("SELECT COUNT(*) FROM attendance")
    int getCount();

}
