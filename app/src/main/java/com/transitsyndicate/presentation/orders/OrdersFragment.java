package com.transitsyndicate.presentation.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.transitsyndicate.R;
import com.transitsyndicate.domain.entity.cargo.CargoType;
import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.presentation.game.GameViewModel;
import com.transitsyndicate.presentation.game.GameViewModelFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrdersFragment extends Fragment {

    private GameViewModel vm;
    private OrderAdapter adapter;
    private TextView tvEmpty;
    private RecyclerView rv;

    private List<Order> lastOrders = new ArrayList<>();
    private final Set<CargoType> activeFilters = new HashSet<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity(),
                new GameViewModelFactory(requireActivity().getApplication()))
                .get(GameViewModel.class);

        tvEmpty = view.findViewById(R.id.tv_empty_orders);
        rv = view.findViewById(R.id.rv_orders);

        adapter = new OrderAdapter(order -> vm.acceptOrder(order));
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        setupFilterChips(view);

        vm.activeOrders.observe(getViewLifecycleOwner(), list -> {
            lastOrders = list != null ? list : new ArrayList<>();
            applyFilters();
        });

        vm.currentTick.observe(getViewLifecycleOwner(), tick -> {
            if (tick != null) adapter.setCurrentTick(tick);
        });

        vm.deliveryProgress.observe(getViewLifecycleOwner(), progress -> {
            if (progress != null) adapter.setDeliveryProgress(progress);
        });
    }

    private void setupFilterChips(View view) {
        bindChip(view, R.id.chip_filter_heavy, CargoType.HEAVY);
        bindChip(view, R.id.chip_filter_food,  CargoType.FOOD);
        bindChip(view, R.id.chip_filter_docs,  CargoType.DOCUMENTS);
        bindChip(view, R.id.chip_filter_grain, CargoType.GRAIN);
        bindChip(view, R.id.chip_filter_flour, CargoType.FLOUR);
        bindChip(view, R.id.chip_filter_bread, CargoType.BREAD);
        bindChip(view, R.id.chip_filter_perish, CargoType.PERISHABLE);
        bindChip(view, R.id.chip_filter_fuel,  CargoType.FUEL);
    }

    private void bindChip(View root, int chipId, CargoType type) {
        Chip chip = root.findViewById(chipId);
        chip.setOnCheckedChangeListener((c, checked) -> {
            if (checked) activeFilters.add(type);
            else activeFilters.remove(type);
            applyFilters();
        });
    }

    private void applyFilters() {
        List<Order> result;
        if (activeFilters.isEmpty()) {
            result = lastOrders;
        } else {
            result = new ArrayList<>();
            for (Order o : lastOrders) {
                if (activeFilters.contains(o.getCargoType())) result.add(o);
            }
        }
        adapter.submitList(result);
        boolean empty = result.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
