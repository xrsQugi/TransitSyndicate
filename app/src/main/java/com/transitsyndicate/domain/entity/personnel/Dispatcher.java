package com.transitsyndicate.domain.entity.personnel;

import com.transitsyndicate.core.constants.GameConstants;

public class Dispatcher extends Staff {

    private Integer assignedDistrictId;

    public Dispatcher(int id, String name) {
        super(id, name, StaffType.DISPATCHER,
                GameConstants.DISPATCHER_SALARY,
                3, 0.99f);
    }

    public void assignToDistrict(int districtId) {
        this.assignedDistrictId = districtId;
    }

    public void unassignFromDistrict() {
        this.assignedDistrictId = null;
    }

    public void restoreDistrictAssignment(Integer districtId) {
        this.assignedDistrictId = districtId;
    }

    public Integer getAssignedDistrictId() { return assignedDistrictId; }

    public boolean hasDistrict() { return assignedDistrictId != null; }
}
