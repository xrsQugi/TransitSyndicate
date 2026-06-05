package com.transitsyndicate.data.repository;

import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.data.local.database.dao.DistrictDao;
import com.transitsyndicate.data.local.database.entity.DistrictEntity;
import com.transitsyndicate.domain.entity.map.City;
import com.transitsyndicate.domain.entity.map.District;
import com.transitsyndicate.domain.entity.map.DistrictType;
import com.transitsyndicate.domain.entity.map.Route;
import com.transitsyndicate.domain.entity.transport.TransportType;
import com.transitsyndicate.domain.repository.MapRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapRepositoryImpl implements MapRepository {

    private static final int DISTRICT_RESIDENTIAL = 1;
    private static final int DISTRICT_BUSINESS = 2;
    private static final int DISTRICT_INDUSTRIAL = 3;
    private static final int DISTRICT_GLOBAL = 4;

    private final DistrictDao dao;

    public MapRepositoryImpl(DistrictDao dao) {
        this.dao = dao;
        seedIfEmpty();
    }

    @Override
    public List<City> getAllCities() {
        List<District> all = new ArrayList<>();
        for (DistrictEntity e : dao.getAll()) all.add(toDomain(e));
        return Collections.singletonList(new City(1, "district_residential", all));
    }

    @Override
    public City getCityById(int id) {
        return getAllCities().get(0);
    }

    @Override
    public District getDistrictById(int id) {
        DistrictEntity e = dao.getById(id);
        return e != null ? toDomain(e) : null;
    }

    @Override
    public List<District> getUnlockedDistricts() {
        List<District> result = new ArrayList<>();
        for (DistrictEntity e : dao.getUnlocked()) result.add(toDomain(e));
        return result;
    }

    @Override
    public List<Route> getRoutesFrom(int districtId) {
        return buildStaticRoutes(districtId);
    }

    @Override
    public void saveDistrict(District district) {
        DistrictEntity e = toEntity(district);
        dao.update(e);
    }

    private void seedIfEmpty() {
        if (!dao.getAll().isEmpty()) return;
        dao.insert(buildEntity(DISTRICT_RESIDENTIAL, "district_residential",
                DistrictType.RESIDENTIAL, 1, 1, true));
        dao.insert(buildEntity(DISTRICT_BUSINESS, "district_business",
                DistrictType.BUSINESS, 4, GameConstants.BUSINESS_DISTRICT_UNLOCK_LEVEL, false));
        dao.insert(buildEntity(DISTRICT_INDUSTRIAL, "district_industrial",
                DistrictType.INDUSTRIAL, 2, GameConstants.INDUSTRIAL_ZONE_UNLOCK_LEVEL, false));
        dao.insert(buildEntity(DISTRICT_GLOBAL, "district_global",
                DistrictType.GLOBAL, 1, GameConstants.GLOBAL_MAP_UNLOCK_LEVEL, false));
    }

    private DistrictEntity buildEntity(int id, String nameKey, DistrictType type,
                                       int traffic, int unlockLevel, boolean unlocked) {
        DistrictEntity e = new DistrictEntity();
        e.id = id;
        e.nameKey = nameKey;
        e.districtType = type.name();
        e.trafficLevel = traffic;
        e.unlockRequiredLevel = unlockLevel;
        e.unlocked = unlocked;
        return e;
    }

    private District toDomain(DistrictEntity e) {
        return new District(e.id, e.nameKey, DistrictType.valueOf(e.districtType),
                e.trafficLevel, e.unlockRequiredLevel, e.unlocked);
    }

    private DistrictEntity toEntity(District d) {
        DistrictEntity e = new DistrictEntity();
        e.id = d.getId();
        e.nameKey = d.getNameKey();
        e.districtType = d.getType().name();
        e.trafficLevel = d.getTrafficLevel();
        e.unlockRequiredLevel = d.getUnlockRequiredLevel();
        e.unlocked = d.isUnlocked();
        return e;
    }

    private List<Route> buildStaticRoutes(int fromId) {
        List<TransportType> all = Arrays.asList(TransportType.values());
        List<TransportType> noSemi = Arrays.asList(
                TransportType.WALKING_COURIER, TransportType.SCOOTER,
                TransportType.LARGUS, TransportType.GAZEL_TRUCK);
        List<TransportType> truckOnly = Arrays.asList(
                TransportType.GAZEL_TRUCK, TransportType.SEMI_TRAILER,
                TransportType.REFRIGERATOR, TransportType.TANKER);

        List<Route> routes = new ArrayList<>();
        switch (fromId) {
            case DISTRICT_RESIDENTIAL:
                routes.add(new Route(DISTRICT_RESIDENTIAL, DISTRICT_BUSINESS, 3f, noSemi, false));
                routes.add(new Route(DISTRICT_RESIDENTIAL, DISTRICT_INDUSTRIAL, 5f, truckOnly, false));
                break;
            case DISTRICT_BUSINESS:
                routes.add(new Route(DISTRICT_BUSINESS, DISTRICT_RESIDENTIAL, 3f, noSemi, false));
                routes.add(new Route(DISTRICT_BUSINESS, DISTRICT_INDUSTRIAL, 4f, truckOnly, false));
                break;
            case DISTRICT_INDUSTRIAL:
                routes.add(new Route(DISTRICT_INDUSTRIAL, DISTRICT_RESIDENTIAL, 5f, truckOnly, false));
                routes.add(new Route(DISTRICT_INDUSTRIAL, DISTRICT_GLOBAL, 50f, all, true));
                break;
            case DISTRICT_GLOBAL:
                routes.add(new Route(DISTRICT_GLOBAL, DISTRICT_INDUSTRIAL, 50f, all, true));
                break;
        }
        return routes;
    }
}
