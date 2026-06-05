package com.transitsyndicate.presentation.personnel;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.transitsyndicate.R;
import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.core.utils.GameNotification;
import com.transitsyndicate.core.utils.MoneyFormatter;
import com.transitsyndicate.domain.entity.cargo.CargoType;
import com.transitsyndicate.domain.entity.personnel.StaffType;
import com.transitsyndicate.presentation.game.GameViewModel;
import com.transitsyndicate.presentation.game.GameViewModelFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PersonnelFragment extends Fragment {

    private GameViewModel vm;
    private StaffAdapter adapter;
    private TextView tvEmpty;
    private RecyclerView rv;

    private static final CargoType[] CARGO_TYPES = CargoType.values();
    private static final String[] CARGO_LABELS = {
            "🍕 Food", "📄 Documents", "⚙️ Heavy Cargo",
            "❄️ Perishable", "⛽ Fuel", "🌾 Grain", "🌾 Flour", "🍞 Bread"
    };

    private void showHirePrice(String label, long salary) {
        long cost = salary * GameConstants.HIRING_COST_WEEKS;
        FrameLayout host = requireActivity().findViewById(R.id.notification_host);
        GameNotification.info(host, label + ": " + MoneyFormatter.formatWithSymbol(cost)
                + " (зарплата " + MoneyFormatter.formatWithSymbol(salary) + "/нед)");
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personnel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity(),
                new GameViewModelFactory(requireActivity().getApplication()))
                .get(GameViewModel.class);

        tvEmpty = view.findViewById(R.id.tv_empty_staff);
        rv = view.findViewById(R.id.rv_staff);

        adapter = new StaffAdapter(staff -> vm.fireStaff(staff));
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        vm.staffList.observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            boolean empty = list == null || list.isEmpty();
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            rv.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        SwitchMaterial swAutoDispatch = view.findViewById(R.id.sw_auto_dispatch);
        vm.autoDispatchEnabled.observe(getViewLifecycleOwner(), enabled -> {
            if (enabled != null) swAutoDispatch.setChecked(enabled);
        });
        swAutoDispatch.setOnCheckedChangeListener((btn, isChecked) ->
                vm.toggleAutoDispatch(isChecked));

        MaterialButton btnCargo = view.findViewById(R.id.btn_configure_cargo);
        btnCargo.setOnClickListener(v -> showCargoDialog());

        view.findViewById(R.id.btn_hire_courier)
                .setOnClickListener(v -> vm.hireStaff(StaffType.COURIER));
        view.findViewById(R.id.btn_hire_courier)
                .setOnLongClickListener(v -> {
                    showHirePrice("🚶 Courier", GameConstants.NOVICE_COURIER_SALARY);
                    return true;
                });

        view.findViewById(R.id.btn_hire_driver)
                .setOnClickListener(v -> vm.hireStaff(StaffType.DRIVER));
        view.findViewById(R.id.btn_hire_driver)
                .setOnLongClickListener(v -> {
                    showHirePrice("🚗 Driver", GameConstants.EXPERIENCED_DRIVER_SALARY);
                    return true;
                });

        view.findViewById(R.id.btn_hire_loader)
                .setOnClickListener(v -> vm.hireStaff(StaffType.LOADER));
        view.findViewById(R.id.btn_hire_loader)
                .setOnLongClickListener(v -> {
                    showHirePrice("💪 Loader", GameConstants.LOADER_SALARY);
                    return true;
                });

        view.findViewById(R.id.btn_hire_dispatcher)
                .setOnClickListener(v -> vm.hireStaff(StaffType.DISPATCHER));
        view.findViewById(R.id.btn_hire_dispatcher)
                .setOnLongClickListener(v -> {
                    showHirePrice("📋 Dispatcher", GameConstants.DISPATCHER_SALARY);
                    return true;
                });
    }

    private void showCargoDialog() {
        Set<CargoType> current = vm.getAutoDispatchCargo();
        boolean[] checked = new boolean[CARGO_TYPES.length];
        for (int i = 0; i < CARGO_TYPES.length; i++) {
            checked[i] = current.contains(CARGO_TYPES[i]);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_cargo_title)
                .setMessage(R.string.dialog_cargo_hint)
                .setMultiChoiceItems(CARGO_LABELS, checked,
                        (dialog, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    Set<CargoType> selected = new HashSet<>();
                    for (int i = 0; i < CARGO_TYPES.length; i++) {
                        if (checked[i]) selected.add(CARGO_TYPES[i]);
                    }
                    vm.setAutoDispatchCargo(selected);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
