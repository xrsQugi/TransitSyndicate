package com.transitsyndicate.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.transitsyndicate.data.local.database.entity.DistrictEntity;

import java.util.List;

@Dao
public interface DistrictDao {
    @Query("SELECT * FROM district")
    List<DistrictEntity> getAll();

    @Query("SELECT * FROM district WHERE id = :id")
    DistrictEntity getById(int id);

    @Query("SELECT * FROM district WHERE unlocked = 1")
    List<DistrictEntity> getUnlocked();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DistrictEntity entity);

    @Update
    void update(DistrictEntity entity);
}
