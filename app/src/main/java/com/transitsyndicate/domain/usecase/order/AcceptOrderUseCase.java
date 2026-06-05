package com.transitsyndicate.domain.usecase.order;

import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;
import com.transitsyndicate.domain.repository.OrderRepository;

public class AcceptOrderUseCase {

    private final OrderRepository orderRepository;

    public AcceptOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean execute(int orderId, int transportId, int staffId) {
        Order order = orderRepository.getById(orderId);
        if (order == null || order.getStatus() != OrderStatus.PENDING) return false;

        order.assign(transportId, staffId);
        orderRepository.save(order);
        return true;
    }
}
