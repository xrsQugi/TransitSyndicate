package com.transitsyndicate.data.local.database.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transport")
public class TransportEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String transportType;
    public String state;
    public int fatigueLevel;
    @Nullable
    public String specialVehicleType;
}
