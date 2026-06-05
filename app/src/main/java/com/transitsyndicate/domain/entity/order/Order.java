package com.transitsyndicate.domain.entity.order;

import com.transitsyndicate.domain.entity.cargo.CargoType;

public class Order {

    private int id;
    private final OrderType type;
    private final CargoType cargoType;
    private final int fromDistrictId;
    private final int toDistrictId;
    private final long reward;
    private final long deadlineTick;
    private final long createdAtTick;
    private OrderStatus status;
    private Integer assignedTransportId;
    private Integer assignedStaffId;

    public Order(int id, OrderType type, CargoType cargoType,
                 int fromDistrictId, int toDistrictId,
                 long reward, long deadlineTick, long createdAtTick,
                 OrderStatus status, Integer assignedTransportId, Integer assignedStaffId) {
        this(id, type, cargoType, fromDistrictId, toDistrictId, reward, deadlineTick, createdAtTick);
        this.status = status;
        this.assignedTransportId = assignedTransportId;
        this.assignedStaffId = assignedStaffId;
    }

    public Order(int id, OrderType type, CargoType cargoType,
                 int fromDistrictId, int toDistrictId,
                 long reward, long deadlineTick, long createdAtTick) {
        this.id = id;
        this.type = type;
        this.cargoType = cargoType;
        this.fromDistrictId = fromDistrictId;
        this.toDistrictId = toDistrictId;
        this.reward = reward;
        this.deadlineTick = deadlineTick;
        this.createdAtTick = createdAtTick;
        this.status = OrderStatus.PENDING;
    }

    public boolean isExpired(long currentTick) {
        return status == OrderStatus.PENDING && currentTick > deadlineTick;
    }

    public void assign(int transportId, int staffId) {
        this.assignedTransportId = transportId;
        this.assignedStaffId = staffId;
        this.status = OrderStatus.ASSIGNED;
    }

    public void startDelivery() {
        this.status = OrderStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void fail() {
        this.status = OrderStatus.FAILED;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public OrderType getType() { return type; }
    public CargoType getCargoType() { return cargoType; }
    public int getFromDistrictId() { return fromDistrictId; }
    public int getToDistrictId() { return toDistrictId; }
    public long getReward() { return reward; }
    public long getDeadlineTick() { return deadlineTick; }
    public long getCreatedAtTick() { return createdAtTick; }
    public OrderStatus getStatus() { return status; }
    public Integer getAssignedTransportId() { return assignedTransportId; }
    public Integer getAssignedStaffId() { return assignedStaffId; }
}
