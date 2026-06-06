package com.transitsyndicate.domain.entity.infrastructure;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class ColdStorage extends Building {

    public ColdStorage(int id, int districtId) {
        super(id, districtId, BuildingType.COLD_STORAGE,
                GameConstants.COLD_STORAGE_PRICE, GameConstants.COLD_STORAGE_MAX_LEVEL);
    }

    @Override
    public long getUpgradeCost() {
        return GameConstants.COLD_STORAGE_PRICE * getLevel();
    }

    public CargoType getProducedCargoType() { return CargoType.PERISHABLE; }

    public int getProductionRatePerTick() { return getLevel(); }
}
