package com.transitsyndicate.domain.usecase.personnel;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.personnel.Staff;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.repository.PlayerRepository;
import com.transitsyndicate.domain.repository.StaffRepository;

public class HireStaffUseCase {

    private final PlayerRepository playerRepository;
    private final StaffRepository staffRepository;

    public HireStaffUseCase(PlayerRepository playerRepository, StaffRepository staffRepository) {
        this.playerRepository = playerRepository;
        this.staffRepository = staffRepository;
    }

    public boolean execute(Staff staff) {
        Player player = playerRepository.getPlayer();
        long hiringCost = staff.getSalary() * GameConstants.HIRING_COST_WEEKS;
        if (!player.canAfford(hiringCost)) return false;

        player.spend(hiringCost);
        staffRepository.save(staff);
        playerRepository.savePlayer(player);
        return true;
    }
}
