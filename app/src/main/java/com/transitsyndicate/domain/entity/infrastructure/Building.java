package com.transitsyndicate.domain.entity.infrastructure;

public abstract class Building {

    private int id;
    private final int districtId;
    private final BuildingType type;
    private final long constructionCost;
    private final int maxLevel;
    private int level;

    protected Building(int id, int districtId, BuildingType type,
                       long constructionCost, int maxLevel) {
        this.id = id;
        this.districtId = districtId;
        this.type = type;
        this.constructionCost = constructionCost;
        this.maxLevel = maxLevel;
        this.level = 1;
    }

    public void setId(int id) { this.id = id; }

    public abstract long getUpgradeCost();

    public boolean isMaxLevel() {
        return level >= maxLevel;
    }

    public void upgrade() {
        if (!isMaxLevel()) level++;
    }

    public int getId() { return id; }
    public int getDistrictId() { return districtId; }
    public BuildingType getType() { return type; }
    public long getConstructionCost() { return constructionCost; }
    public int getMaxLevel() { return maxLevel; }
    public int getLevel() { return level; }
}
