package com.transitsyndicate.data.repository;

import com.transitsyndicate.data.local.database.dao.TransportDao;
import com.transitsyndicate.data.local.database.entity.TransportEntity;
import com.transitsyndicate.domain.entity.transport.GazelTruck;
import com.transitsyndicate.domain.entity.transport.Largus;
import com.transitsyndicate.domain.entity.transport.Scooter;
import com.transitsyndicate.domain.entity.transport.SemiTrailerTruck;
import com.transitsyndicate.domain.entity.transport.SpecialVehicle;
import com.transitsyndicate.domain.entity.transport.SpecialVehicleType;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.entity.transport.TransportState;
import com.transitsyndicate.domain.entity.transport.TransportType;
import com.transitsyndicate.domain.entity.transport.WalkingCourier;
import com.transitsyndicate.domain.repository.TransportRepository;

import java.util.ArrayList;
import java.util.List;

public class TransportRepositoryImpl implements TransportRepository {

    private final TransportDao dao;

    public TransportRepositoryImpl(TransportDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Transport> getAllTransports() {
        return toList(dao.getAll());
    }

    @Override
    public Transport getById(int id) {
        TransportEntity e = dao.getById(id);
        return e != null ? toDomain(e) : null;
    }

    @Override
    public void save(Transport transport) {
        TransportEntity e = toEntity(transport);
        if (e.id == 0) {
            transport.setId((int) dao.insert(e));
        } else {
            dao.update(e);
        }
    }

    @Override
    public void delete(int id) {
        dao.delete(id);
    }

    @Override
    public List<Transport> getIdleTransports() {
        return toList(dao.getIdle());
    }

    @Override
    public List<Transport> getByType(TransportType type) {
        return toList(dao.getByType(type.name()));
    }

    private List<Transport> toList(List<TransportEntity> entities) {
        List<Transport> result = new ArrayList<>();
        for (TransportEntity e : entities) result.add(toDomain(e));
        return result;
    }

    private Transport toDomain(TransportEntity e) {
        TransportType type = TransportType.valueOf(e.transportType);
        Transport t;
        switch (type) {
            case SCOOTER: t = new Scooter(e.id); break;
            case LARGUS: t = new Largus(e.id); break;
            case GAZEL_TRUCK: t = new GazelTruck(e.id); break;
            case SEMI_TRAILER: t = new SemiTrailerTruck(e.id); break;
            case REFRIGERATOR:
                t = new SpecialVehicle(e.id, SpecialVehicleType.REFRIGERATOR); break;
            case TANKER:
                t = new SpecialVehicle(e.id, SpecialVehicleType.TANKER); break;
            default: t = new WalkingCourier(e.id); break;
        }
        t.restoreState(TransportState.valueOf(e.state), e.fatigueLevel);
        return t;
    }

    private TransportEntity toEntity(Transport t) {
        TransportEntity e = new TransportEntity();
        e.id = t.getId();
        e.transportType = t.getType().name();
        e.state = t.getState().name();
        e.fatigueLevel = t.getFatigueLevel();
        if (t instanceof SpecialVehicle) {
            e.specialVehicleType = ((SpecialVehicle) t).getSpecialType().name();
        }
        return e;
    }
}
