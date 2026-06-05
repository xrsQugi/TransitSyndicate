package com.transitsyndicate.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.transitsyndicate.data.local.database.entity.TransportEntity;

import java.util.List;

@Dao
public interface TransportDao {
    @Query("SELECT * FROM transport")
    List<TransportEntity> getAll();

    @Query("SELECT * FROM transport WHERE id = :id")
    TransportEntity getById(int id);

    @Query("SELECT * FROM transport WHERE state = 'IDLE'")
    List<TransportEntity> getIdle();

    @Query("SELECT * FROM transport WHERE transportType = :type")
    List<TransportEntity> getByType(String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TransportEntity entity);

    @Update
    void update(TransportEntity entity);

    @Query("DELETE FROM transport WHERE id = :id")
    void delete(int id);
}
