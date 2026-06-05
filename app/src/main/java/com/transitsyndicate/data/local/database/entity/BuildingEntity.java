package com.transitsyndicate.data.local.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "building")
public class BuildingEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int districtId;
    public String buildingType;
    public int level;
}
