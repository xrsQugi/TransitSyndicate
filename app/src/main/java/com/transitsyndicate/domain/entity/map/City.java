package com.transitsyndicate.domain.entity.map;

import java.util.List;

public class City {

    private final int id;
    private final String nameKey;
    private final List<District> districts;

    public City(int id, String nameKey, List<District> districts) {
        this.id = id;
        this.nameKey = nameKey;
        this.districts = districts;
    }

    public District findDistrict(int districtId) {
        for (District d : districts) {
            if (d.getId() == districtId) return d;
        }
        return null;
    }

    public int getId() { return id; }
    public String getNameKey() { return nameKey; }
    public List<District> getDistricts() { return districts; }
}
