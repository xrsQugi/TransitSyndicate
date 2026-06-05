package com.transitsyndicate.domain.entity.personnel;

import com.transitsyndicate.core.constants.GameConstants;

public class Loader extends Staff {

    public Loader(int id, String name) {
        super(id, name, StaffType.LOADER,
                GameConstants.LOADER_SALARY,
                1, 0.95f);
    }

    public int getLoadingTimeTicks() {
        return GameConstants.LOADER_LOADING_TIME_TICKS;
    }
}
