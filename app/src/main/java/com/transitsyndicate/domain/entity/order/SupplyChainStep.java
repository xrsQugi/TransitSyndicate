package com.transitsyndicate.domain.entity.order;

import com.transitsyndicate.domain.entity.cargo.CargoType;

public class SupplyChainStep {

    private final int stepIndex;
    private final int sourceBuildingId;
    private final int destinationBuildingId;
    private final CargoType cargoType;
    private boolean completed;

    public SupplyChainStep(int stepIndex, int sourceBuildingId,
                           int destinationBuildingId, CargoType cargoType) {
        this.stepIndex = stepIndex;
        this.sourceBuildingId = sourceBuildingId;
        this.destinationBuildingId = destinationBuildingId;
        this.cargoType = cargoType;
        this.completed = false;
    }

    public void markCompleted() { this.completed = true; }
    public void reset() { this.completed = false; }

    public int getStepIndex() { return stepIndex; }
    public int getSourceBuildingId() { return sourceBuildingId; }
    public int getDestinationBuildingId() { return destinationBuildingId; }
    public CargoType getCargoType() { return cargoType; }
    public boolean isCompleted() { return completed; }
}
