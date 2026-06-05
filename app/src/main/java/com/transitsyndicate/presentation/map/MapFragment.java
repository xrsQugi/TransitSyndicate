package com.transitsyndicate.presentation.map;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.transitsyndicate.R;
import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;
import com.transitsyndicate.presentation.game.GameViewModel;
import com.transitsyndicate.presentation.game.GameViewModelFactory;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.List;

public class MapFragment extends Fragment {

    private GameViewModel    vm;
    private MapView          mapView;
    private DistrictOverlay  districtOverlay;
    private DeliveryOverlay  deliveryOverlay;

    private float   globalAnim = 0f;
    private boolean running    = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable animTick = new Runnable() {
        @Override public void run() {
            if (!running) return;
            globalAnim = (globalAnim + 0.025f) % 1f;
            districtOverlay.setGlobalAnim(globalAnim);
            deliveryOverlay.advanceAnimation();
            mapView.invalidate();
            handler.postDelayed(this, 33); 
        }
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity(),
                new GameViewModelFactory(requireActivity().getApplication()))
                .get(GameViewModel.class);

        mapView = view.findViewById(R.id.map_view);
        setupMap();

        vm.activeOrders.observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                deliveryOverlay.updateDeliveries(orders);
                districtOverlay.setActiveCount(buildActiveCounts(orders));
            }
        });

        vm.player.observe(getViewLifecycleOwner(), player -> {
            if (player != null) districtOverlay.setPlayerLevel(player.getLevel());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        running = true;
        handler.post(animTick);
    }

    @Override
    public void onPause() {
        super.onPause();
        running = false;
        handler.removeCallbacks(animTick);
        mapView.onPause();
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);           

        mapView.getController().setZoom(12.5);
        mapView.getController().setCenter(new GeoPoint(51.755, 19.455));

        districtOverlay = new DistrictOverlay();
        deliveryOverlay = new DeliveryOverlay();

        districtOverlay.setClickListener((districtId, name, locked, unlockLevel) -> {
            if (isAdded()) showDistrictDialog(districtId, name, locked, unlockLevel);
        });

        mapView.getOverlays().add(districtOverlay);
        mapView.getOverlays().add(deliveryOverlay);
    }

    private int[] buildActiveCounts(List<Order> orders) {
        int[] counts = new int[4];
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.IN_PROGRESS) {
                int idx = o.getFromDistrictId() - 1;
                if (idx >= 0 && idx < 4) counts[idx]++;
            }
        }
        return counts;
    }

    private void showDistrictDialog(int districtId, String name,
                                    boolean locked, int unlockLevel) {
        List<Order> orders = vm.activeOrders.getValue();
        int pending = 0, inProgress = 0, completed = 0;
        if (orders != null) {
            for (Order o : orders) {
                if (o.getFromDistrictId() == districtId || o.getToDistrictId() == districtId) {
                    switch (o.getStatus()) {
                        case PENDING:     pending++;     break;
                        case IN_PROGRESS: inProgress++;  break;
                        case COMPLETED:   completed++;   break;
                    }
                }
            }
        }

        String title = districtEmoji(districtId) + " " + name;
        String body  = locked
                ? "🔒 Locked district\n\nReach Level " + unlockLevel
                  + " to unlock.\n\nHigher districts offer bigger rewards\n"
                  + "but require better vehicles and staff."
                : districtDesc(districtId)
                  + "\n\n📊 Current activity:"
                  + "\n  ⏳ Pending:    " + pending
                  + "\n  ▶ Delivering: " + inProgress
                  + "\n  ✅ Completed: " + completed;

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton("Close", null)
                .show();
    }

    private static String districtEmoji(int id) {
        switch (id) {
            case 1: return "🏘";
            case 2: return "🏢";
            case 3: return "🏭";
            case 4: return "🌍";
            default: return "🗺";
        }
    }

    private static String districtDesc(int id) {
        switch (id) {
            case 1: return "🏘 Bałuty — Residential\nHousing estates & courtyards.\nFood delivery, short runs.";
            case 2: return "🏢 Centrum — Business\nPiotrkowska St. office district.\nExpress parcels, high demand.";
            case 3: return "🏭 Widzew — Industrial\nFormer textile factories & warehouses.\nHeavy freight only. Big rewards.";
            case 4: return "🌍 Lotnisko — Intercity\nŁódź–Chopina Airport logistics hub.\nLong haul to Warsaw. Huge payouts.";
            default: return "";
        }
    }
}
