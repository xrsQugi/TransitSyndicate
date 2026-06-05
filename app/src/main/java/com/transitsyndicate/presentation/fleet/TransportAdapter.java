package com.transitsyndicate.presentation.fleet;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transitsyndicate.R;
import com.transitsyndicate.core.utils.BadgeUtils;
import com.transitsyndicate.domain.entity.transport.Transport;
import com.transitsyndicate.domain.entity.transport.TransportState;

import java.util.ArrayList;
import java.util.List;

public class TransportAdapter extends RecyclerView.Adapter<TransportAdapter.ViewHolder> {

    private final List<Transport> items = new ArrayList<>();

    public void submitList(List<Transport> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transport, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Transport t = items.get(pos);

        h.tvName.setText(transportEmoji(t.getType().name()) + "  "
                + t.getType().name().replace("_", " "));
        h.tvSlots.setText("📦 " + t.getLoadedSlots() + " / " + t.getMaxSlots() + " slots");
        h.tvSpeed.setText("⚡ Speed ×" + t.getSpeedMultiplier()
                + "   ⛽ Fuel ×" + t.getFuelCostPerDelivery());

        TransportState state = t.getState();
        switch (state) {
            case IDLE:
                BadgeUtils.set(h.tvState, "● Idle",
                        Color.parseColor("#6EE7B7"), 0xFF0B3328);
                break;
            case DELIVERING:
                BadgeUtils.set(h.tvState, "▶ Delivering",
                        Color.parseColor("#818CF8"), 0xFF0F0F2A);
                break;
            case BROKEN:
                BadgeUtils.set(h.tvState, "✕ Broken",
                        Color.parseColor("#FCA5A5"), 0xFF2D0A0A);
                break;
            default:
                BadgeUtils.set(h.tvState, "⏳ " + state.name(),
                        Color.parseColor("#FCD34D"), 0xFF2C1A00);
                break;
        }
    }

    private static String transportEmoji(String type) {
        switch (type) {
            case "WALKING_COURIER": return "🚶";
            case "SCOOTER":         return "🛵";
            case "LARGUS":          return "🚐";
            case "GAZEL_TRUCK":     return "🚛";
            case "SEMI_TRAILER":    return "🚚";
            case "REFRIGERATOR":    return "❄️";
            case "TANKER":          return "🛢️";
            default:                return "🚗";
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSlots, tvSpeed, tvState;

        ViewHolder(View v) {
            super(v);
            tvName  = v.findViewById(R.id.tv_transport_name);
            tvSlots = v.findViewById(R.id.tv_transport_slots);
            tvSpeed = v.findViewById(R.id.tv_transport_speed);
            tvState = v.findViewById(R.id.tv_transport_state);
        }
    }
}
