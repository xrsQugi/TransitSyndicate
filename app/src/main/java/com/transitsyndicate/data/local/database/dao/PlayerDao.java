package com.transitsyndicate.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.transitsyndicate.data.local.database.entity.PlayerEntity;

@Dao
public interface PlayerDao {
    @Query("SELECT * FROM player WHERE id = 1 LIMIT 1")
    PlayerEntity get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlayerEntity entity);

    @Update
    void update(PlayerEntity entity);
}
