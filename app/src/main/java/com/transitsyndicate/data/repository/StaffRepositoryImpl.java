package com.transitsyndicate.data.repository;

import com.transitsyndicate.data.local.database.dao.StaffDao;
import com.transitsyndicate.data.local.database.entity.StaffEntity;
import com.transitsyndicate.domain.entity.personnel.Dispatcher;
import com.transitsyndicate.domain.entity.personnel.ExperiencedDriver;
import com.transitsyndicate.domain.entity.personnel.Loader;
import com.transitsyndicate.domain.entity.personnel.NoviceCourier;
import com.transitsyndicate.domain.entity.personnel.Staff;
import com.transitsyndicate.domain.entity.personnel.StaffType;
import com.transitsyndicate.domain.repository.StaffRepository;

import java.util.ArrayList;
import java.util.List;

public class StaffRepositoryImpl implements StaffRepository {

    private final StaffDao dao;

    public StaffRepositoryImpl(StaffDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Staff> getAllStaff() {
        return toList(dao.getAll());
    }

    @Override
    public Staff getById(int id) {
        StaffEntity e = dao.getById(id);
        return e != null ? toDomain(e) : null;
    }

    @Override
    public void save(Staff staff) {
        StaffEntity e = toEntity(staff);
        if (e.id == 0) {
            staff.setId((int) dao.insert(e));
        } else {
            dao.update(e);
        }
    }

    @Override
    public void fire(int id) {
        dao.delete(id);
    }

    @Override
    public List<Staff> getByType(StaffType type) {
        return toList(dao.getByType(type.name()));
    }

    @Override
    public List<Staff> getAvailableStaff() {
        return toList(dao.getAvailable());
    }

    private List<Staff> toList(List<StaffEntity> entities) {
        List<Staff> result = new ArrayList<>();
        for (StaffEntity e : entities) result.add(toDomain(e));
        return result;
    }

    private Staff toDomain(StaffEntity e) {
        StaffType type = StaffType.valueOf(e.staffType);
        Staff s;
        switch (type) {
            case DRIVER: s = new ExperiencedDriver(e.id, e.name); break;
            case LOADER: s = new Loader(e.id, e.name); break;
            case DISPATCHER:
                Dispatcher d = new Dispatcher(e.id, e.name);
                if (e.assignedDistrictId != null) d.restoreDistrictAssignment(e.assignedDistrictId);
                s = d;
                break;
            default: s = new NoviceCourier(e.id, e.name); break;
        }
        s.restoreAssignment(e.assignedTransportId, e.available);
        return s;
    }

    private StaffEntity toEntity(Staff s) {
        StaffEntity e = new StaffEntity();
        e.id = s.getId();
        e.name = s.getName();
        e.staffType = s.getType().name();
        e.experienceLevel = s.getExperienceLevel();
        e.reliabilityRate = s.getReliabilityRate();
        e.available = s.isAvailable();
        e.assignedTransportId = s.getAssignedTransportId();
        if (s instanceof Dispatcher) {
            e.assignedDistrictId = ((Dispatcher) s).getAssignedDistrictId();
        }
        return e;
    }
}
