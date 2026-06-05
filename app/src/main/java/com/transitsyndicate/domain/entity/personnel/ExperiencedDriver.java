package com.transitsyndicate.domain.entity.personnel;

import com.transitsyndicate.core.constants.GameConstants;

public class ExperiencedDriver extends Staff {

    public ExperiencedDriver(int id, String name) {
        super(id, name, StaffType.DRIVER,
                GameConstants.EXPERIENCED_DRIVER_SALARY,
                5, GameConstants.EXPERIENCED_RELIABILITY);
    }
}
