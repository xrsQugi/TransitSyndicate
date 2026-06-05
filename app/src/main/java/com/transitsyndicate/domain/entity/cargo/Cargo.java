package com.transitsyndicate.domain.entity.cargo;

public class Cargo {

    private final int id;
    private final CargoType type;
    private final float weight;
    private final boolean requiresRefrigeration;

    public Cargo(int id, CargoType type, float weight, boolean requiresRefrigeration) {
        this.id = id;
        this.type = type;
        this.weight = weight;
        this.requiresRefrigeration = requiresRefrigeration;
    }

    public int getId() { return id; }
    public CargoType getType() { return type; }
    public float getWeight() { return weight; }
    public boolean requiresRefrigeration() { return requiresRefrigeration; }
}
