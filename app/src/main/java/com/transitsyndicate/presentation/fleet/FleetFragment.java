package com.transitsyndicate.presentation.fleet;

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

import com.transitsyndicate.R;
import com.transitsyndicate.core.constants.GameConstants;
import com.transitsyndicate.core.utils.GameNotification;
import com.transitsyndicate.core.utils.MoneyFormatter;
import com.transitsyndicate.domain.entity.transport.TransportType;
import com.transitsyndicate.presentation.game.GameViewModel;
import com.transitsyndicate.presentation.game.GameViewModelFactory;

public class FleetFragment extends Fragment {

    private GameViewModel vm;
    private TransportAdapter adapter;
    private TextView tvEmpty;
    private RecyclerView rv;

    private void showPrice(String label, long price) {
        FrameLayout host = requireActivity().findViewById(R.id.notification_host);
        GameNotification.info(host, label + ": " + MoneyFormatter.formatWithSymbol(price));
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fleet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity(),
                new GameViewModelFactory(requireActivity().getApplication()))
                .get(GameViewModel.class);

        tvEmpty = view.findViewById(R.id.tv_empty_fleet);
        rv = view.findViewById(R.id.rv_fleet);

        adapter = new TransportAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        vm.transports.observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            boolean empty = list == null || list.isEmpty();
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            rv.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        view.findViewById(R.id.btn_buy_scooter)
                .setOnClickListener(v -> vm.buyTransport(TransportType.SCOOTER));
        view.findViewById(R.id.btn_buy_scooter)
                .setOnLongClickListener(v -> {
                    showPrice("🛵 Scooter", GameConstants.SCOOTER_PRICE);
                    return true;
                });

        view.findViewById(R.id.btn_buy_largus)
                .setOnClickListener(v -> vm.buyTransport(TransportType.LARGUS));
        view.findViewById(R.id.btn_buy_largus)
                .setOnLongClickListener(v -> {
                    showPrice("🚐 Largus", GameConstants.LARGUS_PRICE);
                    return true;
                });

        view.findViewById(R.id.btn_buy_gazel)
                .setOnClickListener(v -> vm.buyTransport(TransportType.GAZEL_TRUCK));
        view.findViewById(R.id.btn_buy_gazel)
                .setOnLongClickListener(v -> {
                    showPrice("🚛 Gazel", GameConstants.GAZEL_PRICE);
                    return true;
                });

        view.findViewById(R.id.btn_buy_semi)
                .setOnClickListener(v -> vm.buyTransport(TransportType.SEMI_TRAILER));
        view.findViewById(R.id.btn_buy_semi)
                .setOnLongClickListener(v -> {
                    showPrice("🚚 Semi Trailer", GameConstants.SEMI_PRICE);
                    return true;
                });
    }
}
