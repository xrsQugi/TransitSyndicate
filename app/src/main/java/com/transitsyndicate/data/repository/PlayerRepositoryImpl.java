package com.transitsyndicate.data.repository;

import com.transitsyndicate.data.local.database.dao.PlayerDao;
import com.transitsyndicate.data.local.database.entity.PlayerEntity;
import com.transitsyndicate.data.local.preferences.GamePreferences;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.repository.PlayerRepository;

public class PlayerRepositoryImpl implements PlayerRepository {

    private final PlayerDao dao;
    private final GamePreferences prefs;

    public PlayerRepositoryImpl(PlayerDao dao, GamePreferences prefs) {
        this.dao = dao;
        this.prefs = prefs;
    }

    @Override
    public Player getPlayer() {
        PlayerEntity e = dao.get();
        if (e == null) {
            Player defaultPlayer = new Player(1, "Player");
            savePlayer(defaultPlayer);
            return defaultPlayer;
        }
        return new Player(e.id, e.name, e.money, e.experience, e.level,
                e.stamina, e.runSpeedMultiplier, e.negotiationSkill, e.availableSkillPoints);
    }

    @Override
    public void savePlayer(Player player) {
        PlayerEntity e = new PlayerEntity();
        e.id = player.getId();
        e.name = player.getName();
        e.money = player.getMoney();
        e.experience = player.getExperience();
        e.level = player.getLevel();
        e.stamina = player.getStamina();
        e.runSpeedMultiplier = player.getRunSpeedMultiplier();
        e.negotiationSkill = player.getNegotiationSkill();
        e.availableSkillPoints = player.getAvailableSkillPoints();
        dao.insert(e);
    }
}
