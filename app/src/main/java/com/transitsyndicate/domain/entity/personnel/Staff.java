package com.transitsyndicate.domain.entity.personnel;

public abstract class Staff {

    private int id;
    private final String name;
    private final StaffType type;
    private final long salary;
    private int experienceLevel;
    private float reliabilityRate;
    private Integer assignedTransportId;
    private boolean available;

    protected Staff(int id, String name, StaffType type, long salary,
                    int experienceLevel, float reliabilityRate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.salary = salary;
        this.experienceLevel = experienceLevel;
        this.reliabilityRate = reliabilityRate;
        this.available = true;
    }

    public void assignToTransport(int transportId) {
        this.assignedTransportId = transportId;
        this.available = false;
    }

    public void releaseFromTransport() {
        this.assignedTransportId = null;
        this.available = true;
    }

    public void gainExperience() {
        experienceLevel = Math.min(experienceLevel + 1, 10);
        reliabilityRate = Math.min(reliabilityRate + 0.01f, 0.99f);
    }

    public void setId(int id) { this.id = id; }

    public void restoreAssignment(Integer transportId, boolean isAvailable) {
        this.assignedTransportId = transportId;
        this.available = isAvailable;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public StaffType getType() { return type; }
    public long getSalary() { return salary; }
    public int getExperienceLevel() { return experienceLevel; }
    public float getReliabilityRate() { return reliabilityRate; }
    public Integer getAssignedTransportId() { return assignedTransportId; }
    public boolean isAvailable() { return available; }
}
