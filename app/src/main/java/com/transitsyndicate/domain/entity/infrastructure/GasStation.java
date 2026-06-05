package com.transitsyndicate.domain.entity.infrastructure;

import com.transitsyndicate.core.constants.GameConstants;

public class GasStation extends Building {

    public GasStation(int id, int districtId) {
        super(id, districtId, BuildingType.GAS_STATION,
                GameConstants.GAS_STATION_PRICE, GameConstants.GAS_STATION_MAX_LEVEL);
    }

    @Override
    public long getUpgradeCost() {
        return GameConstants.GAS_STATION_PRICE * getLevel();
    }

    public float getFuelDiscount() {
        return GameConstants.GAS_STATION_DISCOUNT_PER_LEVEL * getLevel();
    }

    public long applyDiscount(long baseCost) {
        return (long) (baseCost * (1.0f - getFuelDiscount()));
    }
}
