package com.transitsyndicate.domain.entity.transport;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class Scooter extends Transport {

    public Scooter(int id) {
        super(id, TransportType.SCOOTER, GameConstants.SCOOTER_PRICE);
    }

    @Override
    public int getMaxSlots() { return GameConstants.SCOOTER_SLOTS; }

    @Override
    public float getSpeedMultiplier() { return GameConstants.SCOOTER_SPEED; }

    @Override
    public long getFuelCostPerDelivery() { return GameConstants.SCOOTER_MAINTENANCE; }

    @Override
    public boolean canCarry(CargoType cargoType) {
        return cargoType == CargoType.FOOD
                || cargoType == CargoType.DOCUMENTS
                || cargoType == CargoType.BREAD;
    }
}
