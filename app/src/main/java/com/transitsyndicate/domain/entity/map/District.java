package com.transitsyndicate.domain.entity.map;

public class District {

    private final int id;
    private final String nameKey;
    private final DistrictType type;
    private final int trafficLevel;
    private final int unlockRequiredLevel;
    private boolean unlocked;

    public District(int id, String nameKey, DistrictType type,
                    int trafficLevel, int unlockRequiredLevel, boolean unlocked) {
        this.id = id;
        this.nameKey = nameKey;
        this.type = type;
        this.trafficLevel = trafficLevel;
        this.unlockRequiredLevel = unlockRequiredLevel;
        this.unlocked = unlocked;
    }

    public void unlock() { this.unlocked = true; }

    public boolean isTruckAccessible() {
        return type != DistrictType.RESIDENTIAL || trafficLevel < 3;
    }

    public boolean requiresBicycleForTraffic() {
        return type == DistrictType.BUSINESS && trafficLevel >= 3;
    }

    public int getId() { return id; }
    public String getNameKey() { return nameKey; }
    public DistrictType getType() { return type; }
    public int getTrafficLevel() { return trafficLevel; }
    public int getUnlockRequiredLevel() { return unlockRequiredLevel; }
    public boolean isUnlocked() { return unlocked; }
}
