package com.transitsyndicate.domain.entity.transport;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class Largus extends Transport {

    public Largus(int id) {
        super(id, TransportType.LARGUS, GameConstants.LARGUS_PRICE);
    }

    @Override
    public int getMaxSlots() { return GameConstants.LARGUS_SLOTS; }

    @Override
    public float getSpeedMultiplier() { return GameConstants.CAR_SPEED; }

    @Override
    public long getFuelCostPerDelivery() { return GameConstants.LARGUS_FUEL_PER_DELIVERY; }

    @Override
    public boolean canCarry(CargoType cargoType) {
        return cargoType == CargoType.FOOD
                || cargoType == CargoType.DOCUMENTS
                || cargoType == CargoType.HEAVY
                || cargoType == CargoType.BREAD
                || cargoType == CargoType.GRAIN
                || cargoType == CargoType.FLOUR;
    }
}
