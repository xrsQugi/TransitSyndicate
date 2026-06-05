package com.transitsyndicate.domain.usecase.player;

import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.repository.PlayerRepository;

public class GetPlayerUseCase {

    private final PlayerRepository repository;

    public GetPlayerUseCase(PlayerRepository repository) {
        this.repository = repository;
    }

    public Player execute() {
        return repository.getPlayer();
    }
}
