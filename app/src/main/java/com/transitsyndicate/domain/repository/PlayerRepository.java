package com.transitsyndicate.domain.repository;

import com.transitsyndicate.domain.entity.player.Player;

public interface PlayerRepository {
    Player getPlayer();
    void savePlayer(Player player);
}
