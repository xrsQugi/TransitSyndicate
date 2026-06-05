package com.transitsyndicate.domain.entity.infrastructure;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class Farm extends Building {

    public Farm(int id, int districtId) {
        super(id, districtId, BuildingType.FARM,
                GameConstants.FARM_PRICE, GameConstants.FARM_MAX_LEVEL);
    }

    @Override
    public long getUpgradeCost() {
        return GameConstants.FARM_PRICE * getLevel();
    }

    public CargoType getProducedCargoType() {
        return CargoType.GRAIN;
    }

    public int getProductionRatePerTick() {
        return getLevel();
    }
}
