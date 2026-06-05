package com.transitsyndicate.domain.entity.transport;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class SemiTrailerTruck extends Transport {

    public SemiTrailerTruck(int id) {
        super(id, TransportType.SEMI_TRAILER, GameConstants.SEMI_PRICE);
    }

    @Override
    public int getMaxSlots() { return GameConstants.SEMI_SLOTS; }

    @Override
    public float getSpeedMultiplier() { return GameConstants.SEMI_SPEED; }

    @Override
    public long getFuelCostPerDelivery() { return GameConstants.SEMI_FUEL_PER_DELIVERY; }

    @Override
    public boolean canCarry(CargoType cargoType) {
        return cargoType == CargoType.HEAVY
                || cargoType == CargoType.FOOD
                || cargoType == CargoType.GRAIN
                || cargoType == CargoType.FLOUR
                || cargoType == CargoType.BREAD;
    }
}
