package com.transitsyndicate.domain.repository;

import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;

import java.util.List;

public interface OrderRepository {
    List<Order> getAllOrders();
    Order getById(int id);
    void save(Order order);
    void delete(int id);
    List<Order> getByStatus(OrderStatus status);
    List<Order> getActiveOrders();
}
