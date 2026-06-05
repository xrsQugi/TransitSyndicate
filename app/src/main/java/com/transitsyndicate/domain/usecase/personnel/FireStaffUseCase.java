package com.transitsyndicate.domain.usecase.personnel;

import com.transitsyndicate.domain.entity.personnel.Staff;
import com.transitsyndicate.domain.repository.StaffRepository;

public class FireStaffUseCase {

    private final StaffRepository staffRepository;

    public FireStaffUseCase(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public boolean execute(int staffId) {
        Staff staff = staffRepository.getById(staffId);
        if (staff == null) return false;
        staffRepository.fire(staffId);
        return true;
    }
}
