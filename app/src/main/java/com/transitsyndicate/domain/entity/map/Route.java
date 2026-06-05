package com.transitsyndicate.domain.entity.map;

import com.transitsyndicate.domain.entity.transport.TransportType;

import java.util.List;

public class Route {

    private final int fromDistrictId;
    private final int toDistrictId;
    private final float distanceKm;
    private final List<TransportType> allowedTransportTypes;
    private final boolean intercity;

    public Route(int fromDistrictId, int toDistrictId, float distanceKm,
                 List<TransportType> allowedTransportTypes, boolean intercity) {
        this.fromDistrictId = fromDistrictId;
        this.toDistrictId = toDistrictId;
        this.distanceKm = distanceKm;
        this.allowedTransportTypes = allowedTransportTypes;
        this.intercity = intercity;
    }

    public boolean allows(TransportType transportType) {
        return allowedTransportTypes.contains(transportType);
    }

    public int getFromDistrictId() { return fromDistrictId; }
    public int getToDistrictId() { return toDistrictId; }
    public float getDistanceKm() { return distanceKm; }
    public List<TransportType> getAllowedTransportTypes() { return allowedTransportTypes; }
    public boolean isIntercity() { return intercity; }
}
