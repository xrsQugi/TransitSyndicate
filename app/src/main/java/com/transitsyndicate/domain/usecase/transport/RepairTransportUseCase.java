package com.transitsyndicate.domain.usecase.transport;

import com.transitsyndicate.domain.entity.infrastructure.Building;
import com.transitsyndicate.domain.entity.infrastructure.BuildingType;
import com.transitsyndicate.domain.entity.infrastructure.GasStation;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.entity.transport.TransportState;
import com.transitsyndicate.domain.repository.BuildingRepository;
import com.transitsyndicate.domain.repository.PlayerRepository;
import com.transitsyndicate.domain.repository.TransportRepository;

import java.util.List;

public class RepairTransportUseCase {

    private final PlayerRepository playerRepository;
    private final TransportRepository transportRepository;
    private final BuildingRepository buildingRepository;

    public RepairTransportUseCase(PlayerRepository playerRepository,
                                  TransportRepository transportRepository,
                                  BuildingRepository buildingRepository) {
        this.playerRepository = playerRepository;
        this.transportRepository = transportRepository;
        this.buildingRepository = buildingRepository;
    }

    public boolean execute(int transportId) {
        Transport transport = transportRepository.getById(transportId);
        if (transport == null || transport.getState() != TransportState.BROKEN) return false;

        long repairCost = calculateRepairCost(transport);
        Player player = playerRepository.getPlayer();
        if (!player.canAfford(repairCost)) return false;

        player.spend(repairCost);
        transport.repair();

        playerRepository.savePlayer(player);
        transportRepository.save(transport);
        return true;
    }

    private long calculateRepairCost(Transport transport) {
        long baseCost = transport.getFuelCostPerDelivery() * 5;
        List<Building> gasStations = buildingRepository.getByType(BuildingType.GAS_STATION);
        if (!gasStations.isEmpty()) {
            GasStation gs = (GasStation) gasStations.get(0);
            return gs.applyDiscount(baseCost);
        }
        return baseCost;
    }
}
