package com.transitsyndicate.data.repository;

import com.transitsyndicate.data.local.database.dao.OrderDao;
import com.transitsyndicate.data.local.database.entity.OrderEntity;
import com.transitsyndicate.domain.entity.cargo.CargoType;
import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;
import com.transitsyndicate.domain.entity.order.OrderType;
import com.transitsyndicate.domain.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    private final OrderDao dao;

    public OrderRepositoryImpl(OrderDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Order> getAllOrders() {
        return toList(dao.getAll());
    }

    @Override
    public Order getById(int id) {
        OrderEntity e = dao.getById(id);
        return e != null ? toDomain(e) : null;
    }

    @Override
    public void save(Order order) {
        OrderEntity e = toEntity(order);
        if (e.id == 0) {
            int newId = (int) dao.insert(e);
            order.setId(newId);
        } else {
            dao.insert(e); 
        }
    }

    @Override
    public void delete(int id) {
        dao.delete(id);
    }

    @Override
    public List<Order> getByStatus(OrderStatus status) {
        return toList(dao.getByStatus(status.name()));
    }

    @Override
    public List<Order> getActiveOrders() {
        return toList(dao.getActive());
    }

    private List<Order> toList(List<OrderEntity> entities) {
        List<Order> result = new ArrayList<>();
        for (OrderEntity e : entities) result.add(toDomain(e));
        return result;
    }

    private Order toDomain(OrderEntity e) {
        return new Order(
                e.id,
                OrderType.valueOf(e.orderType),
                CargoType.valueOf(e.cargoType),
                e.fromDistrictId, e.toDistrictId,
                e.reward, e.deadlineTick, e.createdAtTick,
                OrderStatus.valueOf(e.status),
                e.assignedTransportId, e.assignedStaffId
        );
    }

    private OrderEntity toEntity(Order o) {
        OrderEntity e = new OrderEntity();
        e.id = o.getId();
        e.orderType = o.getType().name();
        e.cargoType = o.getCargoType().name();
        e.fromDistrictId = o.getFromDistrictId();
        e.toDistrictId = o.getToDistrictId();
        e.reward = o.getReward();
        e.deadlineTick = o.getDeadlineTick();
        e.createdAtTick = o.getCreatedAtTick();
        e.status = o.getStatus().name();
        e.assignedTransportId = o.getAssignedTransportId();
        e.assignedStaffId = o.getAssignedStaffId();
        return e;
    }
}
