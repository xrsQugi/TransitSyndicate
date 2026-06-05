package com.transitsyndicate.domain.entity.infrastructure;

import com.transitsyndicate.core.constants.GameConstants;

public class Garage extends Building {

    public Garage(int id, int districtId) {
        super(id, districtId, BuildingType.GARAGE,
                GameConstants.GARAGE_PRICE, GameConstants.GARAGE_MAX_LEVEL);
    }

    @Override
    public long getUpgradeCost() {
        return GameConstants.GARAGE_PRICE * getLevel();
    }

    public int getVehicleCapacity() {
        return GameConstants.GARAGE_BASE_VEHICLE_CAPACITY
                + (getLevel() - 1) * GameConstants.GARAGE_CAPACITY_PER_LEVEL;
    }
}
