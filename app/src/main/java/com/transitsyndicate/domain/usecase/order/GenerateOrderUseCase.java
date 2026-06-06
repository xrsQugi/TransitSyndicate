package com.transitsyndicate.domain.usecase.order;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.cargo.CargoType;
import com.transitsyndicate.domain.entity.infrastructure.Building;
import com.transitsyndicate.domain.entity.infrastructure.BuildingType;
import com.transitsyndicate.domain.entity.map.District;
import com.transitsyndicate.domain.entity.map.DistrictType;
import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderType;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.entity.transport.TransportType;
import com.transitsyndicate.domain.repository.BuildingRepository;
import com.transitsyndicate.domain.repository.MapRepository;
import com.transitsyndicate.domain.repository.OrderRepository;
import com.transitsyndicate.domain.repository.TransportRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GenerateOrderUseCase {

    private final OrderRepository orderRepository;
    private final MapRepository mapRepository;
    private final TransportRepository transportRepository;
    private final BuildingRepository buildingRepository;
    private final Random random = new Random();

    public GenerateOrderUseCase(OrderRepository orderRepository,
                                MapRepository mapRepository,
                                TransportRepository transportRepository,
                                BuildingRepository buildingRepository) {
        this.orderRepository = orderRepository;
        this.mapRepository = mapRepository;
        this.transportRepository = transportRepository;
        this.buildingRepository = buildingRepository;
    }

    public Order execute(long currentTick) {
        List<District> unlocked = mapRepository.getUnlockedDistricts();
        if (unlocked.isEmpty()) return null;

        Set<TransportType> fleet = getOwnedTransportTypes();
        Set<BuildingType> buildings = getOwnedBuildingTypes();

        District from = unlocked.get(random.nextInt(unlocked.size()));
        District to   = pickDestination(unlocked, from.getId());

        CargoType cargoType = pickCargoForDistrict(from.getType(), fleet, buildings);
        if (cargoType == null) return null;

        long reward   = pickRewardForCargo(cargoType, from.getType());
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

    private CargoType pickCargoForDistrict(DistrictType type,
                                           Set<TransportType> fleet,
                                           Set<BuildingType> buildings) {
        List<CargoType> candidates = getCandidatesForDistrict(type, fleet, buildings);
        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    private List<CargoType> getCandidatesForDistrict(DistrictType type,
                                                      Set<TransportType> fleet,
                                                      Set<BuildingType> buildings) {
        List<CargoType> pool;
        switch (type) {
            case BUSINESS:   pool = Arrays.asList(CargoType.DOCUMENTS, CargoType.FOOD); break;
            case INDUSTRIAL: pool = Arrays.asList(CargoType.HEAVY, CargoType.GRAIN,
                                                  CargoType.FLOUR, CargoType.FUEL); break;
            case GLOBAL:     pool = Arrays.asList(CargoType.HEAVY, CargoType.FOOD,
                                                  CargoType.FUEL, CargoType.PERISHABLE); break;
            default:         pool = Arrays.asList(CargoType.FOOD, CargoType.BREAD,
                                                  CargoType.PERISHABLE); break;
        }

        List<CargoType> result = new ArrayList<>();
        for (CargoType cargo : pool) {
            if (isFulfillable(cargo, fleet, buildings)) result.add(cargo);
        }
        return result;
    }

    private boolean isFulfillable(CargoType cargo, Set<TransportType> fleet,
                                   Set<BuildingType> buildings) {
        switch (cargo) {
            case FOOD:
            case DOCUMENTS:
                return true;
            case HEAVY:
                return fleet.contains(TransportType.LARGUS)
                    || fleet.contains(TransportType.GAZEL_TRUCK)
                    || fleet.contains(TransportType.SEMI_TRAILER);
            case GRAIN:
                return buildings.contains(BuildingType.FARM)
                    && (fleet.contains(TransportType.LARGUS)
                     || fleet.contains(TransportType.GAZEL_TRUCK)
                     || fleet.contains(TransportType.SEMI_TRAILER));
            case FLOUR:
                return buildings.contains(BuildingType.FARM)
                    && buildings.contains(BuildingType.MILL)
                    && (fleet.contains(TransportType.LARGUS)
                     || fleet.contains(TransportType.GAZEL_TRUCK)
                     || fleet.contains(TransportType.SEMI_TRAILER));
            case BREAD:
                return buildings.contains(BuildingType.FARM)
                    && buildings.contains(BuildingType.MILL)
                    && buildings.contains(BuildingType.BAKERY);
            case FUEL:
                return buildings.contains(BuildingType.OIL_DEPOT)
                    && fleet.contains(TransportType.TANKER);
            case PERISHABLE:
                return buildings.contains(BuildingType.COLD_STORAGE)
                    && fleet.contains(TransportType.REFRIGERATOR);
            default:
                return false;
        }
    }

    private Set<TransportType> getOwnedTransportTypes() {
        Set<TransportType> types = EnumSet.noneOf(TransportType.class);
        for (Transport t : transportRepository.getAllTransports()) {
            types.add(t.getType());
        }
        return types;
    }

    private Set<BuildingType> getOwnedBuildingTypes() {
        Set<BuildingType> types = EnumSet.noneOf(BuildingType.class);
        for (Building b : buildingRepository.getAllBuildings()) {
            types.add(b.getType());
        }
        return types;
    }

    private long pickRewardForCargo(CargoType cargo, DistrictType district) {
        long base = pickRewardForDistrict(district);
        switch (cargo) {
            case FUEL:       return (long) (base * 1.5);
            case PERISHABLE: return (long) (base * 1.4);
            case BREAD:      return (long) (base * 1.2);
            case GRAIN:
            case FLOUR:      return (long) (base * 1.1);
            default:         return base;
        }
    }

    private long pickRewardForDistrict(DistrictType type) {
        switch (type) {
            case BUSINESS:   return GameConstants.BUSINESS_BASE_REWARD;
            case INDUSTRIAL: return GameConstants.INDUSTRIAL_BASE_REWARD;
            case GLOBAL:     return GameConstants.INTERCITY_BASE_REWARD;
            default:         return GameConstants.RESIDENTIAL_BASE_REWARD;
        }
    }

    private long pickDeadlineForDistrict(DistrictType type) {
        switch (type) {
            case BUSINESS:   return GameConstants.BUSINESS_DEADLINE_TICKS;
            case INDUSTRIAL: return GameConstants.INDUSTRIAL_DEADLINE_TICKS;
            case GLOBAL:     return GameConstants.INTERCITY_DEADLINE_TICKS;
            default:         return GameConstants.RESIDENTIAL_DEADLINE_TICKS;
        }
    }
}
