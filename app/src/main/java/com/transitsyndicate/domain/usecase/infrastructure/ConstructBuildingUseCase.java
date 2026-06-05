package com.transitsyndicate.domain.usecase.infrastructure;

import com.transitsyndicate.domain.entity.infrastructure.Building;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.repository.BuildingRepository;
import com.transitsyndicate.domain.repository.PlayerRepository;

public class ConstructBuildingUseCase {

    private final PlayerRepository playerRepository;
    private final BuildingRepository buildingRepository;

    public ConstructBuildingUseCase(PlayerRepository playerRepository,
                                    BuildingRepository buildingRepository) {
        this.playerRepository = playerRepository;
        this.buildingRepository = buildingRepository;
    }

    public boolean execute(Building building) {
        Player player = playerRepository.getPlayer();
        if (!player.canAfford(building.getConstructionCost())) return false;

        player.spend(building.getConstructionCost());
        buildingRepository.save(building);
        playerRepository.savePlayer(player);
        return true;
    }
}
