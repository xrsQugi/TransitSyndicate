package com.transitsyndicate.domain.repository;

import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.entity.transport.TransportType;

import java.util.List;

public interface TransportRepository {
    List<Transport> getAllTransports();
    Transport getById(int id);
    void save(Transport transport);
    void delete(int id);
    List<Transport> getIdleTransports();
    List<Transport> getByType(TransportType type);
}
