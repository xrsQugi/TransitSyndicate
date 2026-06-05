package com.transitsyndicate.data.local.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "player")
public class PlayerEntity {
    @PrimaryKey
    public int id;
    public String name;
    public long money;
    public long experience;
    public int level;
    public int stamina;
    public float runSpeedMultiplier;
    public int negotiationSkill;
    public int availableSkillPoints;
}
