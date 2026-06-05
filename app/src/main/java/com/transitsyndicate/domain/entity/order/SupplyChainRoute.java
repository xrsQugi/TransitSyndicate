package com.transitsyndicate.domain.entity.order;

import java.util.List;

public class SupplyChainRoute {

    private final int id;
    private final String nameKey;
    private final List<SupplyChainStep> steps;
    private final boolean autoRepeat;
    private int currentStepIndex;

    public SupplyChainRoute(int id, String nameKey,
                            List<SupplyChainStep> steps, boolean autoRepeat) {
        this.id = id;
        this.nameKey = nameKey;
        this.steps = steps;
        this.autoRepeat = autoRepeat;
        this.currentStepIndex = 0;
    }

    public SupplyChainStep getCurrentStep() {
        if (currentStepIndex < steps.size()) return steps.get(currentStepIndex);
        return null;
    }

    public void advanceStep() {
        if (currentStepIndex < steps.size()) {
            steps.get(currentStepIndex).markCompleted();
            currentStepIndex++;
        }
        if (isCompleted() && autoRepeat) {
            reset();
        }
    }

    public boolean isCompleted() {
        return currentStepIndex >= steps.size();
    }

    private void reset() {
        currentStepIndex = 0;
        for (SupplyChainStep step : steps) step.reset();
    }

    public int getId() { return id; }
    public String getNameKey() { return nameKey; }
    public List<SupplyChainStep> getSteps() { return steps; }
    public boolean isAutoRepeat() { return autoRepeat; }
    public int getCurrentStepIndex() { return currentStepIndex; }
}
