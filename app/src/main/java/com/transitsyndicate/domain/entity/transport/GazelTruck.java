package com.transitsyndicate.domain.entity.transport;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class GazelTruck extends Transport {

    public GazelTruck(int id) {
        super(id, TransportType.GAZEL_TRUCK, GameConstants.GAZEL_PRICE);
    }

    @Override
    public int getMaxSlots() { return GameConstants.GAZEL_SLOTS; }

    @Override
    public float getSpeedMultiplier() { return GameConstants.TRUCK_SPEED; }

    @Override
    public long getFuelCostPerDelivery() { return GameConstants.GAZEL_FUEL_PER_DELIVERY; }

    @Override
    public boolean canCarry(CargoType cargoType) {
        return cargoType == CargoType.HEAVY
                || cargoType == CargoType.FOOD
                || cargoType == CargoType.GRAIN
                || cargoType == CargoType.FLOUR
                || cargoType == CargoType.BREAD;
    }
}
