package com.transitsyndicate.domain.entity.infrastructure;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class Mill extends Building {

    public Mill(int id, int districtId) {
        super(id, districtId, BuildingType.MILL,
                GameConstants.MILL_PRICE, GameConstants.MILL_MAX_LEVEL);
    }

    @Override
    public long getUpgradeCost() {
        return GameConstants.MILL_PRICE * getLevel();
    }

    public CargoType getInputCargoType() { return CargoType.GRAIN; }
    public CargoType getOutputCargoType() { return CargoType.FLOUR; }

    public int getOutputPerBatch() {
        return GameConstants.MILL_OUTPUT_PER_LEVEL * getLevel();
    }
}
