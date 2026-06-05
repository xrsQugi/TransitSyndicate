package com.transitsyndicate.domain.entity.transport;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class WalkingCourier extends Transport {

    public WalkingCourier(int id) {
        super(id, TransportType.WALKING_COURIER, 0L);
    }

    @Override
    public int getMaxSlots() { return GameConstants.WALKING_COURIER_SLOTS; }

    @Override
    public float getSpeedMultiplier() { return GameConstants.WALKING_SPEED; }

    @Override
    public long getFuelCostPerDelivery() { return 0L; }

    @Override
    public boolean canCarry(CargoType cargoType) {
        return cargoType == CargoType.FOOD
                || cargoType == CargoType.DOCUMENTS
                || cargoType == CargoType.BREAD;
    }
}
