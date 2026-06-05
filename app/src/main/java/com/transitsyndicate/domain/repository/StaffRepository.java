package com.transitsyndicate.domain.repository;

import com.transitsyndicate.domain.entity.personnel.Staff;
import com.transitsyndicate.domain.entity.personnel.StaffType;

import java.util.List;

public interface StaffRepository {
    List<Staff> getAllStaff();
    Staff getById(int id);
    void save(Staff staff);
    void fire(int id);
    List<Staff> getByType(StaffType type);
    List<Staff> getAvailableStaff();
}
