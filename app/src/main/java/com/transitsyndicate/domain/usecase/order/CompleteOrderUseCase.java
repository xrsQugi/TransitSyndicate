package com.transitsyndicate.domain.usecase.order;

import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;
import com.transitsyndicate.domain.entity.personnel.Staff;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.repository.OrderRepository;
import com.transitsyndicate.domain.repository.PlayerRepository;
import com.transitsyndicate.domain.repository.StaffRepository;
import com.transitsyndicate.domain.repository.TransportRepository;

public class CompleteOrderUseCase {

    private final OrderRepository orderRepository;
    private final PlayerRepository playerRepository;
    private final TransportRepository transportRepository;
    private final StaffRepository staffRepository;

    public CompleteOrderUseCase(OrderRepository orderRepository,
                                PlayerRepository playerRepository,
                                TransportRepository transportRepository,
                                StaffRepository staffRepository) {
        this.orderRepository = orderRepository;
        this.playerRepository = playerRepository;
        this.transportRepository = transportRepository;
        this.staffRepository = staffRepository;
    }

    public long execute(int orderId) {
        Order order = orderRepository.getById(orderId);
        if (order == null || order.getStatus() != OrderStatus.IN_PROGRESS) return 0L;

        Player player = playerRepository.getPlayer();
        long reward = (long) (order.getReward() * player.getNegotiationMultiplier());

        player.earn(reward);
        player.addExperience((long) (reward * 0.1));
        order.complete();

        if (order.getAssignedTransportId() != null) {
            Transport transport = transportRepository.getById(order.getAssignedTransportId());
            if (transport != null) {
                transport.finishDelivery();
                transportRepository.save(transport);
            }
        }

        if (order.getAssignedStaffId() != null) {
            Staff staff = staffRepository.getById(order.getAssignedStaffId());
            if (staff != null) {
                staff.releaseFromTransport();
                staff.gainExperience();
                staffRepository.save(staff);
            }
        }

        orderRepository.save(order);
        playerRepository.savePlayer(player);
        return reward;
    }
}
