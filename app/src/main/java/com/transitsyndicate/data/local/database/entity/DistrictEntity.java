package com.transitsyndicate.data.local.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "district")
public class DistrictEntity {
    @PrimaryKey
    public int id;
    public String nameKey;
    public String districtType;
    public int trafficLevel;
    public int unlockRequiredLevel;
    public boolean unlocked;
}
