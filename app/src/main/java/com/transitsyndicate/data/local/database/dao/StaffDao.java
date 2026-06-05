package com.transitsyndicate.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.transitsyndicate.data.local.database.entity.StaffEntity;

import java.util.List;

@Dao
public interface StaffDao {
    @Query("SELECT * FROM staff")
    List<StaffEntity> getAll();

    @Query("SELECT * FROM staff WHERE id = :id")
    StaffEntity getById(int id);

    @Query("SELECT * FROM staff WHERE staffType = :type")
    List<StaffEntity> getByType(String type);

    @Query("SELECT * FROM staff WHERE available = 1")
    List<StaffEntity> getAvailable();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(StaffEntity entity);

    @Update
    void update(StaffEntity entity);

    @Query("DELETE FROM staff WHERE id = :id")
    void delete(int id);
}
