package com.transitsyndicate.presentation.game;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.transitsyndicate.R;
import com.transitsyndicate.TransitSyndicateApp;
import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.core.di.AppContainer;
import com.transitsyndicate.core.utils.MoneyFormatter;
import com.transitsyndicate.domain.entity.cargo.CargoType;
import com.transitsyndicate.domain.entity.map.Route;
import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;
import com.transitsyndicate.domain.entity.personnel.Staff;
import com.transitsyndicate.domain.entity.player.Player;
import com.transitsyndicate.domain.entity.infrastructure.Building;
import com.transitsyndicate.domain.entity.infrastructure.BuildingType;
import com.transitsyndicate.domain.entity.infrastructure.Garage;
import com.transitsyndicate.domain.entity.infrastructure.GasStation;
import com.transitsyndicate.domain.entity.infrastructure.SortingCenter;
import com.transitsyndicate.domain.entity.infrastructure.Bakery;
import com.transitsyndicate.domain.entity.infrastructure.ColdStorage;
import com.transitsyndicate.domain.entity.infrastructure.Farm;
import com.transitsyndicate.domain.entity.infrastructure.Mill;
import com.transitsyndicate.domain.entity.infrastructure.OilDepot;
import com.transitsyndicate.domain.entity.transport.SpecialVehicle;
import com.transitsyndicate.domain.entity.transport.SpecialVehicleType;
import com.transitsyndicate.domain.entity.personnel.Dispatcher;
import com.transitsyndicate.domain.entity.personnel.ExperiencedDriver;
import com.transitsyndicate.domain.entity.personnel.Loader;
import com.transitsyndicate.domain.entity.personnel.NoviceCourier;
import com.transitsyndicate.domain.entity.personnel.StaffType;
import com.transitsyndicate.domain.entity.transport.GazelTruck;
import com.transitsyndicate.domain.entity.transport.Largus;
import com.transitsyndicate.domain.entity.transport.Scooter;
import com.transitsyndicate.domain.entity.transport.SemiTrailerTruck;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.entity.transport.TransportType;
import com.transitsyndicate.domain.entity.transport.WalkingCourier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameViewModel extends AndroidViewModel {

    public final MutableLiveData<Player> player = new MutableLiveData<>();
    public final MutableLiveData<List<Order>> activeOrders = new MutableLiveData<>();
    public final MutableLiveData<List<Transport>> transports = new MutableLiveData<>();
    public final MutableLiveData<List<com.transitsyndicate.domain.entity.personnel.Staff>> staffList = new MutableLiveData<>();
    public final MutableLiveData<List<Building>> buildings = new MutableLiveData<>();
    public final MutableLiveData<Long> currentTick = new MutableLiveData<>(0L);
    public final MutableLiveData<String> toast = new MutableLiveData<>();
    public final MutableLiveData<Boolean> autoDispatchEnabled = new MutableLiveData<>();
    public final MutableLiveData<Map<Integer, int[]>> deliveryProgress = new MutableLiveData<>(new HashMap<>());

    private final AppContainer c;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tickRunnable = this::onTick;
    private boolean running = false;

    private final Map<Integer, Integer> deliveryCountdowns = new HashMap<>();
    private final Map<Integer, Integer> deliveryTotalTicks = new HashMap<>();

    public GameViewModel(Application app) {
        super(app);
        c = ((TransitSyndicateApp) app).container;
    }

    public void startGame() {
        if (running) return;
        running = true;
        long saved = c.prefs.getGameTick();
        currentTick.setValue(saved);
        autoDispatchEnabled.setValue(c.prefs.isAutoDispatchEnabled());
        restoreDeliveryCountdowns();
        refreshAll();
        handler.postDelayed(tickRunnable, GameConstants.GAME_TICK_MS);
    }

    public void stopGame() {
        running = false;
        handler.removeCallbacks(tickRunnable);
    }

    private void onTick() {
        if (!running) return;
        long tick = currentTick.getValue() + 1;
        currentTick.setValue(tick);
        c.prefs.saveGameTick(tick);

        if (tick % GameConstants.ORDER_GENERATION_INTERVAL_TICKS == 0) {
            c.generateOrder.execute(tick);
        }

        if (Boolean.TRUE.equals(autoDispatchEnabled.getValue())) {
            c.autoDispatch.execute(getAutoDispatchCargo());
        }
        advanceDeliveries(tick);
        tryUnlockDistricts();
        refreshAll();

        handler.postDelayed(tickRunnable, GameConstants.GAME_TICK_MS);
    }

    private void advanceDeliveries(long tick) {
        for (Order order : c.orderRepository.getActiveOrders()) {
            if (order.getStatus() == OrderStatus.IN_PROGRESS) {
                int id = order.getId();
                int remaining = deliveryCountdowns.containsKey(id)
                        ? deliveryCountdowns.get(id)
                        : calculateDeliveryTicks(order);
                remaining--;
                if (remaining <= 0) {
                    deliveryCountdowns.remove(id);
                    deliveryTotalTicks.remove(id);
                    long reward = c.completeOrder.execute(id);
                    if (reward > 0) {
                        toast.setValue(getApplication()
                                .getString(R.string.msg_order_completed,
                                        MoneyFormatter.format(reward)));
                    }
                } else {
                    deliveryCountdowns.put(id, remaining);
                }
            } else if (order.getStatus() == OrderStatus.PENDING && order.isExpired(tick)) {
                order.fail();
                c.orderRepository.save(order);
                toast.setValue(getApplication().getString(R.string.msg_order_failed));
            }
        }
        refreshDeliveryProgress();
    }

    private void refreshDeliveryProgress() {
        Map<Integer, int[]> map = new HashMap<>();
        for (Map.Entry<Integer, Integer> e : deliveryCountdowns.entrySet()) {
            int id = e.getKey();
            int remaining = e.getValue();
            int total = deliveryTotalTicks.containsKey(id) ? deliveryTotalTicks.get(id) : remaining;
            map.put(id, new int[]{remaining, total});
        }
        deliveryProgress.setValue(map);
    }

    private int calculateDeliveryTicks(Order order) {
        float speed = 1.0f;
        if (order.getAssignedTransportId() != null) {
            Transport t = c.transportRepository.getById(order.getAssignedTransportId());
            if (t != null) speed = t.getSpeedMultiplier();
        }
        float distance = 3f;
        for (Route r : c.mapRepository.getRoutesFrom(order.getFromDistrictId())) {
            if (r.getToDistrictId() == order.getToDistrictId()) {
                distance = r.getDistanceKm();
                break;
            }
        }
        int ticks = Math.max(1, (int) (distance / speed));
        return Math.max(ticks, minDeliveryTicks(order.getCargoType()));
    }

    private static int minDeliveryTicks(CargoType cargoType) {
        switch (cargoType) {
            case HEAVY:
            case FUEL:
                return GameConstants.DELIVERY_MIN_TICKS_HEAVY;
            case GRAIN:
            case FLOUR:
            case PERISHABLE:
                return GameConstants.DELIVERY_MIN_TICKS_MEDIUM;
            default:
                return GameConstants.DELIVERY_MIN_TICKS_LIGHT;
        }
    }

    private void restoreDeliveryCountdowns() {
        for (Order o : c.orderRepository.getActiveOrders()) {
            if (o.getStatus() == OrderStatus.IN_PROGRESS) {
                int ticks = calculateDeliveryTicks(o);
                deliveryCountdowns.put(o.getId(), ticks);
                deliveryTotalTicks.put(o.getId(), ticks);
            }
        }
        refreshDeliveryProgress();
    }

    private void tryUnlockDistricts() {
        c.unlockDistrict.execute(2);
        c.unlockDistrict.execute(3);
        c.unlockDistrict.execute(4);
    }

    public void refreshAll() {
        player.setValue(c.getPlayer.execute());
        activeOrders.setValue(c.orderRepository.getActiveOrders());
        transports.setValue(c.transportRepository.getAllTransports());
        staffList.setValue(c.staffRepository.getAllStaff());
        buildings.setValue(c.buildingRepository.getAllBuildings());
    }

    public void takeFirstOrder() {
        List<Order> pending = c.orderRepository.getByStatus(OrderStatus.PENDING);
        if (pending.isEmpty()) {
            toast.setValue(getApplication().getString(R.string.order_status_pending) + ": 0");
            return;
        }
        List<Transport> idle = c.transportRepository.getIdleTransports();
        List<Staff> available = c.staffRepository.getAvailableStaff();
        Order order = pending.get(0);
        Transport transport = findSuitableTransport(order, idle);
        Staff driver = transport != null ? findMatchingDriver(transport, available) : null;
        if (transport == null || driver == null) {
            toast.setValue(getApplication().getString(R.string.error_no_available_transport));
            return;
        }
        boolean ok = c.assignTransport.execute(transport.getId(), order.getId(), driver.getId());
        if (ok) {
            int ticks = calculateDeliveryTicks(c.orderRepository.getById(order.getId()));
            deliveryCountdowns.put(order.getId(), ticks);
            deliveryTotalTicks.put(order.getId(), ticks);
            refreshDeliveryProgress();
            refreshAll();
        }
    }

    public void buyScooter() {
        boolean ok = c.purchaseTransport.execute(new Scooter(0));
        if (ok) refreshAll();
        else toast.setValue(getApplication().getString(R.string.error_not_enough_money));
    }

    public void acceptOrder(Order order) {
        List<Transport> idle = c.transportRepository.getIdleTransports();
        List<Staff> available = c.staffRepository.getAvailableStaff();
        Transport transport = findSuitableTransport(order, idle);
        Staff driver = transport != null ? findMatchingDriver(transport, available) : null;
        if (transport == null || driver == null) {
            toast.setValue(getApplication().getString(R.string.error_no_available_transport));
            return;
        }
        boolean ok = c.assignTransport.execute(transport.getId(), order.getId(), driver.getId());
        if (ok) {
            int ticks = calculateDeliveryTicks(c.orderRepository.getById(order.getId()));
            deliveryCountdowns.put(order.getId(), ticks);
            deliveryTotalTicks.put(order.getId(), ticks);
            refreshDeliveryProgress();
            refreshAll();
        } else {
            toast.setValue(getApplication().getString(R.string.error_no_available_transport));
        }
    }

    private Transport findSuitableTransport(Order order, List<Transport> idle) {
        for (Transport t : idle) {
            if (t.isAvailable() && t.canCarry(order.getCargoType())) return t;
        }
        return null;
    }

    private Staff findMatchingDriver(Transport transport, List<Staff> available) {
        boolean needsCourier = transport.getType() == TransportType.WALKING_COURIER;
        for (Staff s : available) {
            if (!s.isAvailable()) continue;
            if (needsCourier && s.getType() == StaffType.COURIER) return s;
            if (!needsCourier && s.getType() == StaffType.DRIVER) return s;
        }
        return null;
    }

    public void buyTransport(TransportType type) {
        Transport t;
        switch (type) {
            case SCOOTER:       t = new Scooter(0); break;
            case LARGUS:        t = new Largus(0); break;
            case GAZEL_TRUCK:   t = new GazelTruck(0); break;
            case SEMI_TRAILER:  t = new SemiTrailerTruck(0); break;
            case REFRIGERATOR:  t = new SpecialVehicle(0, SpecialVehicleType.REFRIGERATOR); break;
            case TANKER:        t = new SpecialVehicle(0, SpecialVehicleType.TANKER); break;
            default:
                toast.setValue(getApplication().getString(R.string.error_not_enough_money));
                return;
        }
        boolean ok = c.purchaseTransport.execute(t);
        if (ok) refreshAll();
        else toast.setValue(getApplication().getString(R.string.error_not_enough_money));
    }

    public void buyTransport(Transport t) {
        boolean ok = c.purchaseTransport.execute(t);
        if (ok) refreshAll();
        else toast.setValue(getApplication().getString(R.string.error_not_enough_money));
    }

    private int nextStaffNameIndex(StaffType type) {
        return c.staffRepository.getByType(type).size() + 1;
    }

    public void hireStaff(StaffType type) {
        Staff s;
        int idx = nextStaffNameIndex(type);
        switch (type) {
            case COURIER:    s = new NoviceCourier(0, "Courier #" + idx); break;
            case DRIVER:     s = new ExperiencedDriver(0, "Driver #" + idx); break;
            case LOADER:     s = new Loader(0, "Loader #" + idx); break;
            case DISPATCHER: s = new Dispatcher(0, "Dispatcher #" + idx); break;
            default: return;
        }
        if (!c.hireStaff.execute(s)) {
            toast.setValue(getApplication().getString(R.string.error_not_enough_money));
        } else {
            if (type == StaffType.DISPATCHER) {
                ((Dispatcher) s).assignToDistrict(1);
                c.staffRepository.save(s);
            }
            if (type == StaffType.COURIER) {
                c.transportRepository.save(new WalkingCourier(0));
            }
            refreshAll();
        }
    }

    public void fireStaff(Staff staff) {
        c.fireStaff.execute(staff.getId());
        refreshAll();
    }

    public void fireStaff(int staffId) {
        c.fireStaff.execute(staffId);
        refreshAll();
    }

    public void toggleAutoDispatch(boolean enabled) {
        c.prefs.setAutoDispatchEnabled(enabled);
        autoDispatchEnabled.setValue(enabled);
    }

    public void setAutoDispatchCargo(Set<CargoType> allowed) {
        Set<String> names = new HashSet<>();
        for (CargoType ct : allowed) names.add(ct.name());
        c.prefs.setAutoDispatchCargo(names);
    }

    public Set<CargoType> getAutoDispatchCargo() {
        Set<String> names = c.prefs.getAutoDispatchCargo();
        if (names.isEmpty()) return new HashSet<>(Arrays.asList(CargoType.values()));
        Set<CargoType> result = new HashSet<>();
        for (String name : names) {
            try { result.add(CargoType.valueOf(name)); } catch (IllegalArgumentException ignored) {}
        }
        return result;
    }

    public void buildBuilding(BuildingType type) {
        int districtId = 1;
        Building b;
        switch (type) {
            case GARAGE:         b = new Garage(0, districtId); break;
            case SORTING_CENTER: b = new SortingCenter(0, districtId); break;
            case GAS_STATION:    b = new GasStation(0, districtId); break;
            case FARM:           b = new Farm(0, districtId); break;
            case MILL:           b = new Mill(0, districtId); break;
            case BAKERY:         b = new Bakery(0, districtId); break;
            case OIL_DEPOT:      b = new OilDepot(0, districtId); break;
            case COLD_STORAGE:   b = new ColdStorage(0, districtId); break;
            default: return;
        }
        boolean ok = c.constructBuilding.execute(b);
        if (ok) refreshAll();
        else toast.setValue(getApplication().getString(R.string.error_not_enough_money));
    }

    public void upgradeBuilding(Building building) {
        boolean ok = c.upgradeBuilding.execute(building.getId());
        if (!ok) toast.setValue(getApplication().getString(R.string.error_not_enough_money));
        else refreshAll();
    }

    public void upgradeBuilding(int buildingId) {
        boolean ok = c.upgradeBuilding.execute(buildingId);
        if (!ok) toast.setValue(getApplication().getString(R.string.error_not_enough_money));
        else refreshAll();
    }

    public void constructBuilding(Building building) {
        boolean ok = c.constructBuilding.execute(building);
        if (ok) refreshAll();
        else toast.setValue(getApplication().getString(R.string.error_not_enough_money));
    }

    @Override
    protected void onCleared() {
        stopGame();
    }
}
