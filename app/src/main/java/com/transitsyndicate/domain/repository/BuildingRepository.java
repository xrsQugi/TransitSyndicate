package com.transitsyndicate.domain.repository;

import com.transitsyndicate.domain.entity.infrastructure.Building;
import com.transitsyndicate.domain.entity.infrastructure.BuildingType;

import java.util.List;

public interface BuildingRepository {
    List<Building> getAllBuildings();
    Building getById(int id);
    void save(Building building);
    void delete(int id);
    List<Building> getByType(BuildingType type);
    List<Building> getByDistrict(int districtId);
}
