package com.transitsyndicate.domain.usecase.map;

import com.transitsyndicate.domain.entity.map.District;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.repository.MapRepository;
import com.transitsyndicate.domain.repository.PlayerRepository;

public class UnlockDistrictUseCase {

    private final PlayerRepository playerRepository;
    private final MapRepository mapRepository;

    public UnlockDistrictUseCase(PlayerRepository playerRepository, MapRepository mapRepository) {
        this.playerRepository = playerRepository;
        this.mapRepository = mapRepository;
    }

    public boolean execute(int districtId) {
        District district = mapRepository.getDistrictById(districtId);
        if (district == null || district.isUnlocked()) return false;

        Player player = playerRepository.getPlayer();
        if (player.getLevel() < district.getUnlockRequiredLevel()) return false;

        district.unlock();
        mapRepository.saveDistrict(district);
        return true;
    }
}
