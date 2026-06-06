package com.transitsyndicate.domain.entity.infrastructure;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class OilDepot extends Building {

    public OilDepot(int id, int districtId) {
        super(id, districtId, BuildingType.OIL_DEPOT,
                GameConstants.OIL_DEPOT_PRICE, GameConstants.OIL_DEPOT_MAX_LEVEL);
    }

    @Override
    public long getUpgradeCost() {
        return GameConstants.OIL_DEPOT_PRICE * getLevel();
    }

    public CargoType getProducedCargoType() { return CargoType.FUEL; }

    public int getProductionRatePerTick() { return getLevel(); }
}
