package com.transitsyndicate.core.di;

import android.content.Context;

import com.transitsyndicate.data.local.database.GameDatabase;
import com.transitsyndicate.data.local.preferences.GamePreferences;
import com.transitsyndicate.data.repository.BuildingRepositoryImpl;
import com.transitsyndicate.data.repository.MapRepositoryImpl;
import com.transitsyndicate.data.repository.OrderRepositoryImpl;
import com.transitsyndicate.data.repository.PlayerRepositoryImpl;
import com.transitsyndicate.data.repository.StaffRepositoryImpl;
import com.transitsyndicate.data.repository.TransportRepositoryImpl;
import com.transitsyndicate.domain.repository.BuildingRepository;
import com.transitsyndicate.domain.repository.MapRepository;
import com.transitsyndicate.domain.repository.OrderRepository;
import com.transitsyndicate.domain.repository.PlayerRepository;
import com.transitsyndicate.domain.repository.StaffRepository;
import com.transitsyndicate.domain.repository.TransportRepository;
import com.transitsyndicate.domain.usecase.infrastructure.ConstructBuildingUseCase;
import com.transitsyndicate.domain.usecase.infrastructure.UpgradeBuildingUseCase;
import com.transitsyndicate.domain.usecase.map.UnlockDistrictUseCase;
import com.transitsyndicate.domain.usecase.order.AcceptOrderUseCase;
import com.transitsyndicate.domain.usecase.order.CompleteOrderUseCase;
import com.transitsyndicate.domain.usecase.order.CreateSupplyChainUseCase;
import com.transitsyndicate.domain.usecase.order.GenerateOrderUseCase;
import com.transitsyndicate.domain.entity.personnel.Dispatcher;
import com.transitsyndicate.domain.entity.personnel.StaffType;
import com.transitsyndicate.domain.entity.transport.TransportType;
import com.transitsyndicate.domain.entity.transport.WalkingCourier;
import com.transitsyndicate.domain.usecase.personnel.AutoDispatchUseCase;
import com.transitsyndicate.domain.usecase.personnel.FireStaffUseCase;
import com.transitsyndicate.domain.usecase.personnel.HireStaffUseCase;
import com.transitsyndicate.domain.usecase.player.GetPlayerUseCase;
import com.transitsyndicate.domain.usecase.player.UpgradePlayerSkillUseCase;
import com.transitsyndicate.domain.usecase.transport.AssignTransportUseCase;
import com.transitsyndicate.domain.usecase.transport.PurchaseTransportUseCase;
import com.transitsyndicate.domain.usecase.transport.RepairTransportUseCase;

public class AppContainer {

    public final GamePreferences prefs;
    public final PlayerRepository playerRepository;
    public final TransportRepository transportRepository;
    public final OrderRepository orderRepository;
    public final StaffRepository staffRepository;
    public final BuildingRepository buildingRepository;
    public final MapRepository mapRepository;

    public final GetPlayerUseCase getPlayer;
    public final UpgradePlayerSkillUseCase upgradePlayerSkill;
    public final PurchaseTransportUseCase purchaseTransport;
    public final AssignTransportUseCase assignTransport;
    public final RepairTransportUseCase repairTransport;
    public final GenerateOrderUseCase generateOrder;
    public final AcceptOrderUseCase acceptOrder;
    public final CompleteOrderUseCase completeOrder;
    public final CreateSupplyChainUseCase createSupplyChain;
    public final HireStaffUseCase hireStaff;
    public final FireStaffUseCase fireStaff;
    public final AutoDispatchUseCase autoDispatch;
    public final ConstructBuildingUseCase constructBuilding;
    public final UpgradeBuildingUseCase upgradeBuilding;
    public final UnlockDistrictUseCase unlockDistrict;

    public AppContainer(Context context) {
        GameDatabase db = GameDatabase.getInstance(context);
        this.prefs = new GamePreferences(context);

        playerRepository = new PlayerRepositoryImpl(db.playerDao(), this.prefs);
        transportRepository = new TransportRepositoryImpl(db.transportDao());
        orderRepository = new OrderRepositoryImpl(db.orderDao());
        staffRepository = new StaffRepositoryImpl(db.staffDao());
        buildingRepository = new BuildingRepositoryImpl(db.buildingDao());
        mapRepository = new MapRepositoryImpl(db.districtDao());

        getPlayer = new GetPlayerUseCase(playerRepository);
        upgradePlayerSkill = new UpgradePlayerSkillUseCase(playerRepository);
        purchaseTransport = new PurchaseTransportUseCase(playerRepository, transportRepository);
        assignTransport = new AssignTransportUseCase(transportRepository, orderRepository, staffRepository);
        repairTransport = new RepairTransportUseCase(playerRepository, transportRepository, buildingRepository);
        generateOrder = new GenerateOrderUseCase(orderRepository, mapRepository);
        acceptOrder = new AcceptOrderUseCase(orderRepository);
        completeOrder = new CompleteOrderUseCase(orderRepository, playerRepository, transportRepository, staffRepository);
        createSupplyChain = new CreateSupplyChainUseCase(buildingRepository);
        hireStaff = new HireStaffUseCase(playerRepository, staffRepository);
        fireStaff = new FireStaffUseCase(staffRepository);
        autoDispatch = new AutoDispatchUseCase(orderRepository, transportRepository, staffRepository);
        constructBuilding = new ConstructBuildingUseCase(playerRepository, buildingRepository);
        upgradeBuilding = new UpgradeBuildingUseCase(playerRepository, buildingRepository);
        unlockDistrict = new UnlockDistrictUseCase(playerRepository, mapRepository);

        if (this.prefs.isFirstLaunch()) {
            seedFirstLaunch();
            this.prefs.setFirstLaunchDone();
        } else {
            ensureStarterDispatcher();
            ensureCourierTransports();
        }
    }

    private void seedFirstLaunch() {
        playerRepository.getPlayer();
        com.transitsyndicate.domain.entity.transport.WalkingCourier wc =
                new com.transitsyndicate.domain.entity.transport.WalkingCourier(0);
        transportRepository.save(wc);
        com.transitsyndicate.domain.entity.personnel.NoviceCourier courier =
                new com.transitsyndicate.domain.entity.personnel.NoviceCourier(0, "Courier #1");
        staffRepository.save(courier);
        Dispatcher sys = new Dispatcher(0, "System");
        sys.assignToDistrict(1);
        staffRepository.save(sys);
    }

    private void ensureStarterDispatcher() {
        if (staffRepository.getByType(StaffType.DISPATCHER).isEmpty()) {
            Dispatcher sys = new Dispatcher(0, "System");
            sys.assignToDistrict(1);
            staffRepository.save(sys);
        }
    }

    private void ensureCourierTransports() {
        int couriers = staffRepository.getByType(StaffType.COURIER).size();
        int walking  = transportRepository.getByType(TransportType.WALKING_COURIER).size();
        for (int i = walking; i < couriers; i++) {
            transportRepository.save(new WalkingCourier(0));
        }
    }
}
