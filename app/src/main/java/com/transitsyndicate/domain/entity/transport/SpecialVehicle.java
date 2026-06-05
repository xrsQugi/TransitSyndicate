package com.transitsyndicate.domain.entity.transport;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;

public class SpecialVehicle extends Transport {

    private final SpecialVehicleType specialType;

    public SpecialVehicle(int id, SpecialVehicleType specialType) {
        super(id, resolveTransportType(specialType), resolvePrice(specialType));
        this.specialType = specialType;
    }

    private static TransportType resolveTransportType(SpecialVehicleType specialType) {
        return specialType == SpecialVehicleType.REFRIGERATOR
                ? TransportType.REFRIGERATOR
                : TransportType.TANKER;
    }

    private static long resolvePrice(SpecialVehicleType specialType) {
        return specialType == SpecialVehicleType.REFRIGERATOR
                ? GameConstants.REFRIGERATOR_PRICE
                : GameConstants.TANKER_PRICE;
    }

    @Override
    public int getMaxSlots() { return GameConstants.SPECIAL_SLOTS; }

    @Override
    public float getSpeedMultiplier() { return GameConstants.TRUCK_SPEED; }

    @Override
    public long getFuelCostPerDelivery() { return GameConstants.SPECIAL_FUEL_PER_DELIVERY; }

    @Override
    public boolean canCarry(CargoType cargoType) {
        if (specialType == SpecialVehicleType.REFRIGERATOR) {
            return cargoType == CargoType.PERISHABLE || cargoType == CargoType.FOOD;
        }
        return cargoType == CargoType.FUEL;
    }

    public SpecialVehicleType getSpecialType() { return specialType; }
}
