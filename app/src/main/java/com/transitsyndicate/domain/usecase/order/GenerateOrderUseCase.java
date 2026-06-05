package com.transitsyndicate.domain.usecase.order;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;
import com.transitsyndicate.domain.entity.map.District;
import com.transitsyndicate.domain.entity.map.DistrictType;
import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderType;
import com.transitsyndicate.domain.repository.MapRepository;
import com.transitsyndicate.domain.repository.OrderRepository;

import java.util.List;
import java.util.Random;

public class GenerateOrderUseCase {

    private final OrderRepository orderRepository;
    private final MapRepository mapRepository;
    private final Random random = new Random();

    public GenerateOrderUseCase(OrderRepository orderRepository, MapRepository mapRepository) {
        this.orderRepository = orderRepository;
        this.mapRepository = mapRepository;
    }

    public Order execute(long currentTick) {
        List<District> unlocked = mapRepository.getUnlockedDistricts();
        if (unlocked.isEmpty()) return null;

        District from = unlocked.get(random.nextInt(unlocked.size()));
        District to = pickDestination(unlocked, from.getId());
        CargoType cargoType = pickCargoForDistrict(from.getType());
        long reward = pickRewardForDistrict(from.getType());
        long deadline = currentTick + pickDeadlineForDistrict(from.getType());

        Order order = new Order(0, OrderType.MANUAL, cargoType,
                from.getId(), to.getId(), reward, deadline, currentTick);
        orderRepository.save(order);
        return order;
    }

    private District pickDestination(List<District> districts, int excludeId) {
        if (districts.size() == 1) return districts.get(0);
        District dest;
        do {
            dest = districts.get(random.nextInt(districts.size()));
        } while (dest.getId() == excludeId);
        return dest;
    }

    private CargoType pickCargoForDistrict(DistrictType type) {
        switch (type) {
            case BUSINESS: return CargoType.DOCUMENTS;
            case INDUSTRIAL: return CargoType.HEAVY;
            case GLOBAL: return random.nextBoolean() ? CargoType.HEAVY : CargoType.FOOD;
            default: return CargoType.FOOD;
        }
    }

    private long pickRewardForDistrict(DistrictType type) {
        switch (type) {
            case BUSINESS: return GameConstants.BUSINESS_BASE_REWARD;
            case INDUSTRIAL: return GameConstants.INDUSTRIAL_BASE_REWARD;
            case GLOBAL: return GameConstants.INTERCITY_BASE_REWARD;
            default: return GameConstants.RESIDENTIAL_BASE_REWARD;
        }
    }

    private long pickDeadlineForDistrict(DistrictType type) {
        switch (type) {
            case BUSINESS: return GameConstants.BUSINESS_DEADLINE_TICKS;
            case INDUSTRIAL: return GameConstants.INDUSTRIAL_DEADLINE_TICKS;
            case GLOBAL: return GameConstants.INTERCITY_DEADLINE_TICKS;
            default: return GameConstants.RESIDENTIAL_DEADLINE_TICKS;
        }
    }
}
