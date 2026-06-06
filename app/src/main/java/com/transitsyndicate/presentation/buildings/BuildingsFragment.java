package com.transitsyndicate.presentation.buildings;

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

import com.transitsyndicate.R;
import com.transitsyndicate.domain.entity.infrastructure.BuildingType;
import com.transitsyndicate.presentation.game.GameViewModel;
import com.transitsyndicate.presentation.game.GameViewModelFactory;

public class BuildingsFragment extends Fragment {

    private GameViewModel vm;
    private BuildingAdapter adapter;
    private TextView tvEmpty;
    private RecyclerView rv;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buildings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity(),
                new GameViewModelFactory(requireActivity().getApplication()))
                .get(GameViewModel.class);

        tvEmpty = view.findViewById(R.id.tv_empty_buildings);
        rv = view.findViewById(R.id.rv_buildings);

        adapter = new BuildingAdapter(building -> vm.upgradeBuilding(building));
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        vm.buildings.observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            boolean empty = list == null || list.isEmpty();
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            rv.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        view.findViewById(R.id.btn_build_garage)
                .setOnClickListener(v -> vm.buildBuilding(BuildingType.GARAGE));
        view.findViewById(R.id.btn_build_sorting)
                .setOnClickListener(v -> vm.buildBuilding(BuildingType.SORTING_CENTER));
        view.findViewById(R.id.btn_build_gas)
                .setOnClickListener(v -> vm.buildBuilding(BuildingType.GAS_STATION));
        view.findViewById(R.id.btn_build_farm)
                .setOnClickListener(v -> vm.buildBuilding(BuildingType.FARM));
        view.findViewById(R.id.btn_build_mill)
                .setOnClickListener(v -> vm.buildBuilding(BuildingType.MILL));
        view.findViewById(R.id.btn_build_bakery)
                .setOnClickListener(v -> vm.buildBuilding(BuildingType.BAKERY));
        view.findViewById(R.id.btn_build_oil_depot)
                .setOnClickListener(v -> vm.buildBuilding(BuildingType.OIL_DEPOT));
        view.findViewById(R.id.btn_build_cold_storage)
                .setOnClickListener(v -> vm.buildBuilding(BuildingType.COLD_STORAGE));
    }
}
