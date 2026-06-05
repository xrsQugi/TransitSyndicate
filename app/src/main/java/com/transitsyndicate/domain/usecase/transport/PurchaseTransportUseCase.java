package com.transitsyndicate.domain.usecase.transport;

import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.repository.PlayerRepository;
import com.transitsyndicate.domain.repository.TransportRepository;

public class PurchaseTransportUseCase {

    private final PlayerRepository playerRepository;
    private final TransportRepository transportRepository;

    public PurchaseTransportUseCase(PlayerRepository playerRepository,
                                    TransportRepository transportRepository) {
        this.playerRepository = playerRepository;
        this.transportRepository = transportRepository;
    }

    public boolean execute(Transport transport) {
        Player player = playerRepository.getPlayer();
        if (!player.canAfford(transport.getPurchasePrice())) return false;

        player.spend(transport.getPurchasePrice());
        transportRepository.save(transport);
        playerRepository.savePlayer(player);
        return true;
    }
}
