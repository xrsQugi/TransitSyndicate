package com.transitsyndicate.domain.usecase.infrastructure;

import com.transitsyndicate.domain.entity.infrastructure.Building;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.repository.BuildingRepository;
import com.transitsyndicate.domain.repository.PlayerRepository;

public class UpgradeBuildingUseCase {

    private final PlayerRepository playerRepository;
    private final BuildingRepository buildingRepository;

    public UpgradeBuildingUseCase(PlayerRepository playerRepository,
                                  BuildingRepository buildingRepository) {
        this.playerRepository = playerRepository;
        this.buildingRepository = buildingRepository;
    }

    public boolean execute(int buildingId) {
        Building building = buildingRepository.getById(buildingId);
        if (building == null || building.isMaxLevel()) return false;

        Player player = playerRepository.getPlayer();
        if (!player.canAfford(building.getUpgradeCost())) return false;

        player.spend(building.getUpgradeCost());
        building.upgrade();

        playerRepository.savePlayer(player);
        buildingRepository.save(building);
        return true;
    }
}
