package com.transitsyndicate.domain.usecase.personnel;

import com.transitsyndicate.domain.entity.cargo.CargoType;
import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;
import com.transitsyndicate.domain.entity.personnel.Staff;
import com.transitsyndicate.domain.entity.personnel.StaffType;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.entity.transport.TransportType;
import com.transitsyndicate.domain.repository.OrderRepository;
import com.transitsyndicate.domain.repository.StaffRepository;
import com.transitsyndicate.domain.repository.TransportRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AutoDispatchUseCase {

    private final OrderRepository orderRepository;
    private final TransportRepository transportRepository;
    private final StaffRepository staffRepository;

    public AutoDispatchUseCase(OrderRepository orderRepository,
                               TransportRepository transportRepository,
                               StaffRepository staffRepository) {
        this.orderRepository = orderRepository;
        this.transportRepository = transportRepository;
        this.staffRepository = staffRepository;
    }

    public void execute(Set<CargoType> allowedCargo) {
        if (staffRepository.getByType(StaffType.DISPATCHER).isEmpty()) return;

        List<Order> pending = filterByCargo(
                orderRepository.getByStatus(OrderStatus.PENDING), allowedCargo);
        List<Transport> idle = new ArrayList<>(transportRepository.getIdleTransports());
        List<Staff> available = new ArrayList<>(staffRepository.getAvailableStaff());

        for (Order order : pending) {
            Transport transport = findSuitableTransport(order, idle);
            if (transport == null) continue;

            Staff driver = findDriver(available, transport);
            if (driver == null) continue;

            order.assign(transport.getId(), driver.getId());
            order.startDelivery();
            transport.startDelivery();
            driver.assignToTransport(transport.getId());

            orderRepository.save(order);
            transportRepository.save(transport);
            staffRepository.save(driver);

            idle.remove(transport);
            available.remove(driver);
        }
    }

    private List<Order> filterByCargo(List<Order> orders, Set<CargoType> allowed) {
        if (allowed == null || allowed.isEmpty()) return orders;
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (allowed.contains(o.getCargoType())) result.add(o);
        }
        return result;
    }

    private Transport findSuitableTransport(Order order, List<Transport> transports) {
        for (Transport t : transports) {
            if (t.isAvailable() && t.canCarry(order.getCargoType())) return t;
        }
        return null;
    }

    private Staff findDriver(List<Staff> staff, Transport transport) {
        boolean needsCourier = transport.getType() == TransportType.WALKING_COURIER;
        for (Staff s : staff) {
            if (!s.isAvailable()) continue;
            if (needsCourier && s.getType() == StaffType.COURIER) return s;
            if (!needsCourier && s.getType() == StaffType.DRIVER) return s;
        }
        return null;
    }
}
