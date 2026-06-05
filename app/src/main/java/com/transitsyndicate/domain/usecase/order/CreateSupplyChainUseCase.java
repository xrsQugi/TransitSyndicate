package com.transitsyndicate.domain.usecase.order;

import com.transitsyndicate.domain.entity.cargo.CargoType;
import com.transitsyndicate.domain.entity.infrastructure.Building;
import com.transitsyndicate.domain.entity.infrastructure.BuildingType;
import com.transitsyndicate.domain.entity.order.SupplyChainRoute;
import com.transitsyndicate.domain.entity.order.SupplyChainStep;
import com.transitsyndicate.domain.repository.BuildingRepository;

import java.util.ArrayList;
import java.util.List;

public class CreateSupplyChainUseCase {

    private final BuildingRepository buildingRepository;
    private int nextRouteId = 1;

    public CreateSupplyChainUseCase(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public SupplyChainRoute createBreadRoute() {
        List<Building> farms = buildingRepository.getByType(BuildingType.FARM);
        List<Building> bakeries = buildingRepository.getByType(BuildingType.BAKERY);
        if (farms.isEmpty() || bakeries.isEmpty()) return null;

        int farmId = farms.get(0).getId();
        int bakeryId = bakeries.get(0).getId();

        List<SupplyChainStep> steps = new ArrayList<>();
        steps.add(new SupplyChainStep(0, farmId, bakeryId, CargoType.GRAIN));
        steps.add(new SupplyChainStep(1, bakeryId, -1, CargoType.BREAD));

        return new SupplyChainRoute(nextRouteId++, "supply_chain_bread_route", steps, true);
    }
}
