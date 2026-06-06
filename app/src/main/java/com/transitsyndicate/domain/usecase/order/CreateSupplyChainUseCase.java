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

    // Farm -> Mill (GRAIN) -> Bakery (FLOUR) -> delivery (BREAD)
    public SupplyChainRoute createBreadRoute() {
        List<Building> farms    = buildingRepository.getByType(BuildingType.FARM);
        List<Building> mills    = buildingRepository.getByType(BuildingType.MILL);
        List<Building> bakeries = buildingRepository.getByType(BuildingType.BAKERY);
        if (farms.isEmpty() || mills.isEmpty() || bakeries.isEmpty()) return null;

        int farmId   = farms.get(0).getId();
        int millId   = mills.get(0).getId();
        int bakeryId = bakeries.get(0).getId();

        List<SupplyChainStep> steps = new ArrayList<>();
        steps.add(new SupplyChainStep(0, farmId,   millId,   CargoType.GRAIN));
        steps.add(new SupplyChainStep(1, millId,   bakeryId, CargoType.FLOUR));
        steps.add(new SupplyChainStep(2, bakeryId, -1,       CargoType.BREAD));

        return new SupplyChainRoute(nextRouteId++, "supply_chain_bread_route", steps, true);
    }

    // OilDepot -> delivery (FUEL)
    public SupplyChainRoute createFuelRoute() {
        List<Building> depots = buildingRepository.getByType(BuildingType.OIL_DEPOT);
        if (depots.isEmpty()) return null;

        int depotId = depots.get(0).getId();

        List<SupplyChainStep> steps = new ArrayList<>();
        steps.add(new SupplyChainStep(0, depotId, -1, CargoType.FUEL));

        return new SupplyChainRoute(nextRouteId++, "supply_chain_fuel_route", steps, true);
    }

    // ColdStorage -> delivery (PERISHABLE)
    public SupplyChainRoute createColdRoute() {
        List<Building> storages = buildingRepository.getByType(BuildingType.COLD_STORAGE);
        if (storages.isEmpty()) return null;

        int storageId = storages.get(0).getId();

        List<SupplyChainStep> steps = new ArrayList<>();
        steps.add(new SupplyChainStep(0, storageId, -1, CargoType.PERISHABLE));

        return new SupplyChainRoute(nextRouteId++, "supply_chain_cold_route", steps, true);
    }
}
