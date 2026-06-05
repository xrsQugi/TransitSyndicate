package com.transitsyndicate.data.local.database.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "staff")
public class StaffEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String staffType;
    public int experienceLevel;
    public float reliabilityRate;
    public boolean available;
    @Nullable
    public Integer assignedTransportId;
    @Nullable
    public Integer assignedDistrictId;
}
