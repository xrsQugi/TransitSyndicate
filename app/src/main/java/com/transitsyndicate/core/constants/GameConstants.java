package com.transitsyndicate.core.constants;

public final class GameConstants {

    private GameConstants() {}

    public static final int WALKING_COURIER_SLOTS = 1;
    public static final int SCOOTER_SLOTS = 2;
    public static final int LARGUS_SLOTS = 10;
    public static final int GAZEL_SLOTS = 5;
    public static final int SEMI_SLOTS = 20;
    public static final int SPECIAL_SLOTS = 15;

    public static final float WALKING_SPEED = 0.5f;
    public static final float SCOOTER_SPEED = 1.5f;
    public static final float CAR_SPEED = 1.0f;
    public static final float TRUCK_SPEED = 0.8f;
    public static final float SEMI_SPEED = 0.6f;

    public static final long SCOOTER_PRICE = 500L;
    public static final long LARGUS_PRICE = 5_000L;
    public static final long GAZEL_PRICE = 15_000L;
    public static final long SEMI_PRICE = 80_000L;
    public static final long REFRIGERATOR_PRICE = 35_000L;
    public static final long TANKER_PRICE = 40_000L;

    public static final long SCOOTER_MAINTENANCE = 5L;
    public static final long LARGUS_FUEL_PER_DELIVERY = 20L;
    public static final long GAZEL_FUEL_PER_DELIVERY = 40L;
    public static final long SEMI_FUEL_PER_DELIVERY = 100L;
    public static final long SPECIAL_FUEL_PER_DELIVERY = 50L;

    public static final int DEFAULT_LOADING_TIME_TICKS = 10;
    public static final int LOADER_LOADING_TIME_TICKS = 2;

    public static final long NOVICE_COURIER_SALARY = 50L;
    public static final long EXPERIENCED_DRIVER_SALARY = 200L;
    public static final long LOADER_SALARY = 100L;
    public static final long DISPATCHER_SALARY = 300L;

    public static final float NOVICE_RELIABILITY = 0.70f;
    public static final float EXPERIENCED_RELIABILITY = 0.97f;

    public static final int HIRING_COST_WEEKS = 2;

    public static final long GARAGE_PRICE = 2_000L;
    public static final long SORTING_CENTER_PRICE = 10_000L;
    public static final long GAS_STATION_PRICE = 25_000L;
    public static final long FARM_PRICE = 50_000L;
    public static final long BAKERY_PRICE = 40_000L;

    public static final int GARAGE_BASE_VEHICLE_CAPACITY = 3;
    public static final int GARAGE_CAPACITY_PER_LEVEL = 2;
    public static final int GARAGE_MAX_LEVEL = 5;

    public static final int SORTING_CENTER_BASE_SLOTS = 5;
    public static final int SORTING_CENTER_MAX_LEVEL = 3;

    public static final float GAS_STATION_DISCOUNT_PER_LEVEL = 0.20f;
    public static final int GAS_STATION_MAX_LEVEL = 3;

    public static final int FARM_MAX_LEVEL = 5;
    public static final long MILL_PRICE = 30_000L;
    public static final int MILL_MAX_LEVEL = 5;
    public static final int MILL_OUTPUT_PER_LEVEL = 2;
    public static final int BAKERY_MAX_LEVEL = 5;
    public static final int BAKERY_OUTPUT_PER_LEVEL = 3;
    public static final long OIL_DEPOT_PRICE = 60_000L;
    public static final int OIL_DEPOT_MAX_LEVEL = 3;
    public static final long COLD_STORAGE_PRICE = 45_000L;
    public static final int COLD_STORAGE_MAX_LEVEL = 3;

    public static final int BUSINESS_DISTRICT_UNLOCK_LEVEL = 3;
    public static final int INDUSTRIAL_ZONE_UNLOCK_LEVEL = 7;
    public static final int GLOBAL_MAP_UNLOCK_LEVEL = 15;

    public static final int FATIGUE_MAX = 100;
    public static final int FATIGUE_PER_HOUR = 10;
    public static final int FATIGUE_REST_THRESHOLD = 80;

    public static final long RESIDENTIAL_BASE_REWARD = 30L;
    public static final long BUSINESS_BASE_REWARD = 150L;
    public static final long INDUSTRIAL_BASE_REWARD = 300L;
    public static final long INTERCITY_BASE_REWARD = 800L;

    public static final long RESIDENTIAL_DEADLINE_TICKS = 600L;
    public static final long BUSINESS_DEADLINE_TICKS    = 450L;
    public static final long INDUSTRIAL_DEADLINE_TICKS  = 900L;
    public static final long INTERCITY_DEADLINE_TICKS   = 1800L;

    public static final double EXPERIENCE_PER_COIN = 0.1;

    public static final long STARTING_MONEY = 200L;

    public static final int STAMINA_UPGRADE_AMOUNT = 20;
    public static final float RUN_SPEED_UPGRADE_AMOUNT = 0.1f;
    public static final int NEGOTIATION_UPGRADE_AMOUNT = 1;
    public static final float NEGOTIATION_BONUS_PER_LEVEL = 0.05f;

    public static final long GAME_TICK_MS = 1_000L;
    public static final int ORDER_GENERATION_INTERVAL_TICKS = 15;

   
    public static final int DELIVERY_MIN_TICKS_LIGHT  = 60;   
    public static final int DELIVERY_MIN_TICKS_MEDIUM = 120; 
    public static final int DELIVERY_MIN_TICKS_HEAVY  = 180; 
}
