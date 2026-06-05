package com.transitsyndicate.domain.entity.transport;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.Cargo;
import com.transitsyndicate.domain.entity.cargo.CargoType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Transport {

    private int id;
    private final TransportType type;
    private final long purchasePrice;
    private TransportState state;
    private final List<Cargo> loadedCargo;
    private int fatigueLevel;

    protected Transport(int id, TransportType type, long purchasePrice) {
        this.id = id;
        this.type = type;
        this.purchasePrice = purchasePrice;
        this.state = TransportState.IDLE;
        this.loadedCargo = new ArrayList<>();
        this.fatigueLevel = 0;
    }

    public abstract int getMaxSlots();
    public abstract float getSpeedMultiplier();
    public abstract long getFuelCostPerDelivery();
    public abstract boolean canCarry(CargoType cargoType);

    public boolean isAvailable() {
        return state == TransportState.IDLE && loadedCargo.size() < getMaxSlots();
    }

    public boolean loadCargo(Cargo cargo) {
        if (!canCarry(cargo.getType())) return false;
        if (loadedCargo.size() >= getMaxSlots()) return false;
        loadedCargo.add(cargo);
        return true;
    }

    public void unloadAllCargo() {
        loadedCargo.clear();
    }

    public void startDelivery() {
        state = TransportState.DELIVERING;
    }

    public void finishDelivery() {
        state = TransportState.IDLE;
        unloadAllCargo();
    }

    public void startLoading() {
        state = TransportState.LOADING;
    }

    public void startRefueling() {
        state = TransportState.REFUELING;
    }

    public void breakdown() {
        state = TransportState.BROKEN;
    }

    public void repair() {
        state = TransportState.IDLE;
    }

    public void addFatigue(int amount) {
        fatigueLevel = Math.min(fatigueLevel + amount, GameConstants.FATIGUE_MAX);
    }

    public void rest(int amount) {
        fatigueLevel = Math.max(0, fatigueLevel - amount);
    }

    public boolean needsRest() {
        return fatigueLevel >= GameConstants.FATIGUE_REST_THRESHOLD;
    }

    public void setId(int id) { this.id = id; }

    public void restoreState(TransportState state, int fatigueLevel) {
        this.state = state;
        this.fatigueLevel = fatigueLevel;
    }

    public int getId() { return id; }
    public TransportType getType() { return type; }
    public long getPurchasePrice() { return purchasePrice; }
    public TransportState getState() { return state; }
    public List<Cargo> getLoadedCargo() { return Collections.unmodifiableList(loadedCargo); }
    public int getFatigueLevel() { return fatigueLevel; }
    public int getLoadedSlots() { return loadedCargo.size(); }
}
