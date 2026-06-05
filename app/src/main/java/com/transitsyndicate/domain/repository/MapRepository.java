package com.transitsyndicate.domain.repository;

import com.transitsyndicate.domain.entity.map.City;
import com.transitsyndicate.domain.entity.map.District;
import com.transitsyndicate.domain.entity.map.Route;

import java.util.List;

public interface MapRepository {
    List<City> getAllCities();
    City getCityById(int id);
    District getDistrictById(int id);
    List<District> getUnlockedDistricts();
    List<Route> getRoutesFrom(int districtId);
    void saveDistrict(District district);
}
