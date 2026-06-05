package com.transitsyndicate.domain.entity.player;

import com.transitsyndicate.core.constants.GameConstants;

public class Player {

    private final int id;
    private String name;
    private long money;
    private long experience;
    private int level;
    private int stamina;
    private float runSpeedMultiplier;
    private int negotiationSkill;
    private int availableSkillPoints;

    public Player(int id, String name, long money, long experience, int level,
                  int stamina, float runSpeedMultiplier, int negotiationSkill,
                  int availableSkillPoints) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.experience = experience;
        this.level = level;
        this.stamina = stamina;
        this.runSpeedMultiplier = runSpeedMultiplier;
        this.negotiationSkill = negotiationSkill;
        this.availableSkillPoints = availableSkillPoints;
    }

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.money = GameConstants.STARTING_MONEY;
        this.experience = 0;
        this.level = 1;
        this.stamina = 100;
        this.runSpeedMultiplier = 1.0f;
        this.negotiationSkill = 0;
        this.availableSkillPoints = 0;
    }

    public boolean canAfford(long amount) {
        return money >= amount;
    }

    public void spend(long amount) {
        if (!canAfford(amount)) throw new IllegalStateException("Not enough money");
        money -= amount;
    }

    public void earn(long amount) {
        money += amount;
    }

    public void addExperience(long amount) {
        experience += amount;
        checkLevelUp();
    }

    public float getNegotiationMultiplier() {
        return 1.0f + (negotiationSkill * GameConstants.NEGOTIATION_BONUS_PER_LEVEL);
    }

    public void increaseStamina(int amount) {
        stamina = Math.min(stamina + amount, 200);
    }

    public void increaseRunSpeed(float amount) {
        runSpeedMultiplier = Math.min(runSpeedMultiplier + amount, 3.0f);
    }

    public void increaseNegotiationSkill(int amount) {
        negotiationSkill = Math.min(negotiationSkill + amount, 10);
    }

    public void spendSkillPoint() {
        if (availableSkillPoints <= 0) throw new IllegalStateException("No skill points available");
        availableSkillPoints--;
    }

    private void checkLevelUp() {
        int newLevel = calculateLevel(experience);
        if (newLevel > level) {
            availableSkillPoints += (newLevel - level);
            level = newLevel;
        }
    }

    private int calculateLevel(long exp) {
        return 1 + (int) Math.sqrt(exp * GameConstants.EXPERIENCE_PER_COIN);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public long getMoney() { return money; }
    public long getExperience() { return experience; }
    public int getLevel() { return level; }
    public int getStamina() { return stamina; }
    public float getRunSpeedMultiplier() { return runSpeedMultiplier; }
    public int getNegotiationSkill() { return negotiationSkill; }
    public int getAvailableSkillPoints() { return availableSkillPoints; }
    public void setName(String name) { this.name = name; }
}
