package com.transitsyndicate.domain.entity.infrastructure;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class Bakery extends Building {

    public Bakery(int id, int districtId) {
        super(id, districtId, BuildingType.BAKERY,
                GameConstants.BAKERY_PRICE, GameConstants.BAKERY_MAX_LEVEL);
    }

    @Override
    public long getUpgradeCost() {
        return GameConstants.BAKERY_PRICE * getLevel();
    }

    public CargoType getInputCargoType() { return CargoType.FLOUR; }
    public CargoType getOutputCargoType() { return CargoType.BREAD; }

    public int getOutputPerBatch() {
        return GameConstants.BAKERY_OUTPUT_PER_LEVEL * getLevel();
    }
}
