package com.transitsyndicate.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.transitsyndicate.data.local.database.entity.BuildingEntity;

import java.util.List;

@Dao
public interface BuildingDao {
    @Query("SELECT * FROM building")
    List<BuildingEntity> getAll();

    @Query("SELECT * FROM building WHERE id = :id")
    BuildingEntity getById(int id);

    @Query("SELECT * FROM building WHERE buildingType = :type")
    List<BuildingEntity> getByType(String type);

    @Query("SELECT * FROM building WHERE districtId = :districtId")
    List<BuildingEntity> getByDistrict(int districtId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BuildingEntity entity);

    @Update
    void update(BuildingEntity entity);

    @Query("DELETE FROM building WHERE id = :id")
    void delete(int id);
}
