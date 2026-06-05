package com.transitsyndicate.domain.usecase.transport;

import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.personnel.Staff;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.repository.OrderRepository;
import com.transitsyndicate.domain.repository.StaffRepository;
import com.transitsyndicate.domain.repository.TransportRepository;

public class AssignTransportUseCase {

    private final TransportRepository transportRepository;
    private final OrderRepository orderRepository;
    private final StaffRepository staffRepository;

    public AssignTransportUseCase(TransportRepository transportRepository,
                                  OrderRepository orderRepository,
                                  StaffRepository staffRepository) {
        this.transportRepository = transportRepository;
        this.orderRepository = orderRepository;
        this.staffRepository = staffRepository;
    }

    public boolean execute(int transportId, int orderId, int staffId) {
        Transport transport = transportRepository.getById(transportId);
        Order order = orderRepository.getById(orderId);
        Staff staff = staffRepository.getById(staffId);

        if (transport == null || order == null || staff == null) return false;
        if (!transport.isAvailable()) return false;
        if (!transport.canCarry(order.getCargoType())) return false;
        if (!staff.isAvailable()) return false;

        order.assign(transportId, staffId);
        order.startDelivery();
        transport.startDelivery();
        staff.assignToTransport(transportId);

        transportRepository.save(transport);
        orderRepository.save(order);
        staffRepository.save(staff);
        return true;
    }
}
