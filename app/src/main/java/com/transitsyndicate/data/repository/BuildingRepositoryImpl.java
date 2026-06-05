package com.transitsyndicate.data.repository;

import com.transitsyndicate.data.local.database.dao.BuildingDao;
import com.transitsyndicate.data.local.database.entity.BuildingEntity;
import com.transitsyndicate.domain.entity.infrastructure.Bakery;
import com.transitsyndicate.domain.entity.infrastructure.Building;
import com.transitsyndicate.domain.entity.infrastructure.BuildingType;
import com.transitsyndicate.domain.entity.infrastructure.Farm;
import com.transitsyndicate.domain.entity.infrastructure.Garage;
import com.transitsyndicate.domain.entity.infrastructure.GasStation;
import com.transitsyndicate.domain.entity.infrastructure.SortingCenter;
import com.transitsyndicate.domain.repository.BuildingRepository;

import java.util.ArrayList;
import java.util.List;

public class BuildingRepositoryImpl implements BuildingRepository {

    private final BuildingDao dao;

    public BuildingRepositoryImpl(BuildingDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Building> getAllBuildings() {
        return toList(dao.getAll());
    }

    @Override
    public Building getById(int id) {
        BuildingEntity e = dao.getById(id);
        return e != null ? toDomain(e) : null;
    }

    @Override
    public void save(Building building) {
        BuildingEntity e = toEntity(building);
        if (e.id == 0) {
            building.setId((int) dao.insert(e));
        } else {
            dao.update(e);
        }
    }

    @Override
    public void delete(int id) {
        dao.delete(id);
    }

    @Override
    public List<Building> getByType(BuildingType type) {
        return toList(dao.getByType(type.name()));
    }

    @Override
    public List<Building> getByDistrict(int districtId) {
        return toList(dao.getByDistrict(districtId));
    }

    private List<Building> toList(List<BuildingEntity> entities) {
        List<Building> result = new ArrayList<>();
        for (BuildingEntity e : entities) result.add(toDomain(e));
        return result;
    }

    private Building toDomain(BuildingEntity e) {
        BuildingType type = BuildingType.valueOf(e.buildingType);
        Building b;
        switch (type) {
            case SORTING_CENTER: b = new SortingCenter(e.id, e.districtId); break;
            case GAS_STATION: b = new GasStation(e.id, e.districtId); break;
            case FARM: b = new Farm(e.id, e.districtId); break;
            case BAKERY: b = new Bakery(e.id, e.districtId); break;
            default: b = new Garage(e.id, e.districtId); break;
        }
        for (int i = 1; i < e.level; i++) b.upgrade();
        return b;
    }

    private BuildingEntity toEntity(Building b) {
        BuildingEntity e = new BuildingEntity();
        e.id = b.getId();
        e.districtId = b.getDistrictId();
        e.buildingType = b.getType().name();
        e.level = b.getLevel();
        return e;
    }
}
