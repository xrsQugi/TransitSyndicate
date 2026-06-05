package com.transitsyndicate.presentation.buildings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.transitsyndicate.R;
import com.transitsyndicate.core.utils.MoneyFormatter;
import com.transitsyndicate.domain.entity.infrastructure.Building;

import java.util.ArrayList;
import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {

    public interface OnUpgrade { void onUpgrade(Building building); }

    private final List<Building> items = new ArrayList<>();
    private final OnUpgrade listener;

    public BuildingAdapter(OnUpgrade listener) { this.listener = listener; }

    public void submitList(List<Building> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_building, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Building b = items.get(pos);

        h.tvName.setText(buildingEmoji(b.getType().name()) + " "
                + b.getType().name().replace("_", " "));

        h.tvLevel.setText("Lv." + b.getLevel() + " / " + b.getMaxLevel());

        h.tvDesc.setText(buildingDesc(b.getType().name()));

        int levelPct = b.getMaxLevel() > 0
                ? (int)(b.getLevel() * 100f / b.getMaxLevel())
                : 100;
        h.pbLevel.setProgress(levelPct);

        if (b.isMaxLevel()) {
            h.tvCost.setText("★ MAX LEVEL");
            h.btnUpgrade.setEnabled(false);
            h.btnUpgrade.setAlpha(0.4f);
            h.btnUpgrade.setText("MAX");
        } else {
            h.tvCost.setText("⬆ " + MoneyFormatter.formatWithSymbol(b.getUpgradeCost()));
            h.btnUpgrade.setEnabled(true);
            h.btnUpgrade.setAlpha(1f);
            h.btnUpgrade.setText(R.string.label_upgrade);
            h.btnUpgrade.setOnClickListener(v -> listener.onUpgrade(b));
        }
    }

    private static String buildingEmoji(String type) {
        switch (type) {
            case "GARAGE":         return "🏚";
            case "SORTING_CENTER": return "📦";
            case "GAS_STATION":    return "⛽";
            case "FARM":           return "🌾";
            case "BAKERY":         return "🍞";
            default:               return "🏗";
        }
    }

    private static String buildingDesc(String type) {
        switch (type) {
            case "GARAGE":         return "More vehicle slots per level";
            case "SORTING_CENTER": return "Bundles small parcels for truck pickup";
            case "GAS_STATION":    return "Refuel and repair at cost price";
            case "FARM":           return "Produces grain for your supply chain";
            case "BAKERY":         return "Converts flour into bread for stores";
            default:               return "";
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLevel, tvDesc, tvCost;
        ProgressBar pbLevel;
        MaterialButton btnUpgrade;

        ViewHolder(View v) {
            super(v);
            tvName     = v.findViewById(R.id.tv_building_name);
            tvLevel    = v.findViewById(R.id.tv_building_level);
            tvDesc     = v.findViewById(R.id.tv_building_desc);
            tvCost     = v.findViewById(R.id.tv_upgrade_cost);
            pbLevel    = v.findViewById(R.id.pb_level);
            btnUpgrade = v.findViewById(R.id.btn_upgrade);
        }
    }
}
