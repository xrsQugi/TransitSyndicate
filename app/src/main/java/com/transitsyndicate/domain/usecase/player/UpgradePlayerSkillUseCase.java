package com.transitsyndicate.domain.usecase.player;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.entity.player.PlayerSkill;
import com.transitsyndicate.domain.repository.PlayerRepository;

public class UpgradePlayerSkillUseCase {

    private final PlayerRepository repository;

    public UpgradePlayerSkillUseCase(PlayerRepository repository) {
        this.repository = repository;
    }

    public boolean execute(PlayerSkill skill) {
        Player player = repository.getPlayer();
        if (player.getAvailableSkillPoints() <= 0) return false;

        switch (skill) {
            case STAMINA:
                player.increaseStamina(GameConstants.STAMINA_UPGRADE_AMOUNT);
                break;
            case RUN_SPEED:
                player.increaseRunSpeed(GameConstants.RUN_SPEED_UPGRADE_AMOUNT);
                break;
            case NEGOTIATION:
                player.increaseNegotiationSkill(GameConstants.NEGOTIATION_UPGRADE_AMOUNT);
                break;
        }

        player.spendSkillPoint();
        repository.savePlayer(player);
        return true;
    }
}
