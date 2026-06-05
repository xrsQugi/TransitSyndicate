package com.transitsyndicate.domain.entity.personnel;

import com.transitsyndicate.core.constants.GameConstants;

public class NoviceCourier extends Staff {

    public NoviceCourier(int id, String name) {
        super(id, name, StaffType.COURIER,
                GameConstants.NOVICE_COURIER_SALARY,
                1, GameConstants.NOVICE_RELIABILITY);
    }
}
