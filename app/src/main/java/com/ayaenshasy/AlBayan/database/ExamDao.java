package com.ayaenshasy.AlBayan.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ayaenshasy.AlBayan.model.Exam;

import java.util.List;

// Create the DAO interface
@Dao
public interface ExamDao {
    @Insert
    void insert(Exam exam);

    @Query("SELECT * FROM exams WHERE id = :examId")
    Exam getExamById(int examId);

    @Query("SELECT * FROM exams WHERE studentId = :studentId")
    List<Exam> getExamByStudentId(String studentId);

    @Update
    void update(Exam exam);

    @Delete
    void delete(Exam exam);

    @Query("SELECT * FROM exams")
    List<Exam> getAllExams();

    @Query("SELECT id,studentId, name, degree, mosque, date,image,shack_name,exam_type FROM exams LIMIT :pageSize OFFSET :offset")
    List<Exam> getExamsWithPagination(int pageSize, int offset);

    @Query("SELECT COUNT(*) FROM exams")
    int getCount();

}