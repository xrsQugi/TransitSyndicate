package com.transitsyndicate.domain.entity.infrastructure;

import com.transitsyndicate.core.constants.GameConstants;

public class SortingCenter extends Building {

    public SortingCenter(int id, int districtId) {
        super(id, districtId, BuildingType.SORTING_CENTER,
                GameConstants.SORTING_CENTER_PRICE, GameConstants.SORTING_CENTER_MAX_LEVEL);
    }

    @Override
    public long getUpgradeCost() {
        return GameConstants.SORTING_CENTER_PRICE * getLevel();
    }

    public int getConsolidationSlots() {
        return GameConstants.SORTING_CENTER_BASE_SLOTS * getLevel();
    }
}
