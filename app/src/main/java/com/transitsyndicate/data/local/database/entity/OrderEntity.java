package com.transitsyndicate.data.local.database.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class OrderEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String orderType;
    public String cargoType;
    public int fromDistrictId;
    public int toDistrictId;
    public long reward;
    public long deadlineTick;
    public long createdAtTick;
    public String status;
    @Nullable
    public Integer assignedTransportId;
    @Nullable
    public Integer assignedStaffId;
}
