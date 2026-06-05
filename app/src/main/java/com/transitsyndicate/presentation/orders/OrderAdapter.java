package com.transitsyndicate.presentation.orders;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.transitsyndicate.R;
import com.transitsyndicate.core.utils.BadgeUtils;
import com.transitsyndicate.core.utils.MoneyFormatter;
import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public interface OnAccept { void onAccept(Order order); }

    private final List<Order> items = new ArrayList<>();
    private final OnAccept listener;
    private long currentTick = 0;
    private Map<Integer, int[]> deliveryProgress = new HashMap<>();

    public OrderAdapter(OnAccept listener) { this.listener = listener; }

    public void submitList(List<Order> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public void setCurrentTick(long tick) {
        this.currentTick = tick;
        notifyDataSetChanged();
    }

    public void setDeliveryProgress(Map<Integer, int[]> progress) {
        if (progress != null) this.deliveryProgress = progress;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Order o = items.get(pos);

        h.tvRoute.setText(districtName(o.getFromDistrictId())
                + " → " + districtName(o.getToDistrictId()));
        h.tvCargo.setText(cargoLabel(o.getCargoType().name()));
        h.tvReward.setText(MoneyFormatter.formatWithSymbol(o.getReward()));

        bindStatusBadge(h.tvStatus, o.getStatus());
        bindDeadline(h, o);

        boolean canAccept = o.getStatus() == OrderStatus.PENDING;
        h.btnAccept.setVisibility(canAccept ? View.VISIBLE : View.GONE);
        if (canAccept) h.btnAccept.setOnClickListener(v -> listener.onAccept(o));
    }

    private static void bindStatusBadge(TextView tv, OrderStatus status) {
        switch (status) {
            case PENDING:
                BadgeUtils.set(tv, "⏳ Pending",
                        Color.parseColor("#818CF8"), 0xFF0F0F2A);
                break;
            case IN_PROGRESS:
                BadgeUtils.set(tv, "▶ Delivering",
                        Color.parseColor("#6EE7B7"), 0xFF0B3328);
                break;
            case ASSIGNED:
                BadgeUtils.set(tv, "✓ Assigned",
                        Color.parseColor("#A78BFA"), 0xFF1E1040);
                break;
            case COMPLETED:
                BadgeUtils.set(tv, "✅ Done",
                        Color.parseColor("#6EE7B7"), 0xFF0B3328);
                break;
            default:
                BadgeUtils.set(tv, "❌ Failed",
                        Color.parseColor("#FCA5A5"), 0xFF2D0A0A);
                break;
        }
    }

    private void bindDeadline(ViewHolder h, Order o) {
        long total     = o.getDeadlineTick() - o.getCreatedAtTick();
        long remaining = o.getDeadlineTick() - currentTick;

        if (o.getStatus() == OrderStatus.PENDING && total > 0) {
            int pct = (int) Math.max(0, Math.min(100, remaining * 100 / total));
            h.pbDeadline.setProgress(pct);

            int tint = pct < 30 ? Color.parseColor("#EF4444")
                                : Color.parseColor("#8B5CF6");
            h.pbDeadline.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(tint));

            if (remaining <= 0) {
                h.tvDeadline.setText("🔴 Expired!");
                h.tvDeadline.setTextColor(Color.parseColor("#EF4444"));
            } else if (remaining < 120) {
                h.tvDeadline.setText("🔴 " + formatTicks(remaining) + " left");
                h.tvDeadline.setTextColor(Color.parseColor("#EF4444"));
            } else {
                h.tvDeadline.setText("⏱ " + formatTicks(remaining));
                h.tvDeadline.setTextColor(Color.parseColor("#F59E0B"));
            }
        } else if (o.getStatus() == OrderStatus.IN_PROGRESS) {
            int[] prog = deliveryProgress.get(o.getId());
            int pct = 0;
            if (prog != null && prog[1] > 0) {
                int elapsed = prog[1] - prog[0];
                pct = (int) Math.max(0, Math.min(100, elapsed * 100f / prog[1]));
            }
            h.pbDeadline.setProgress(pct);
            h.pbDeadline.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(deliveryColor(pct)));
            h.tvDeadline.setText("🚚 " + pct + "%");
            h.tvDeadline.setTextColor(deliveryColor(pct));
        } else {
            h.tvDeadline.setText("");
            h.pbDeadline.setProgress(0);
        }
    }

    private static String formatTicks(long ticks) {
        long mins = ticks / 60;
        long secs = ticks % 60;
        if (mins > 0) return mins + "m " + secs + "s";
        return secs + "s";
    }

    private static String districtName(int id) {
        switch (id) {
            case 1: return "🏘 Residential";
            case 2: return "🏢 Business";
            case 3: return "🏭 Industrial";
            case 4: return "🌍 Global";
            default: return "District " + id;
        }
    }

    private static String cargoLabel(String raw) {
        switch (raw) {
            case "FOOD":       return "🍕 Food";
            case "DOCUMENTS":  return "📄 Documents";
            case "HEAVY":      return "⚙️ Heavy";
            case "PERISHABLE": return "❄️ Perishable";
            case "FUEL":       return "⛽ Fuel";
            case "GRAIN":      return "🌾 Grain";
            case "FLOUR":      return "🌾 Flour";
            case "BREAD":      return "🍞 Bread";
            default:           return raw;
        }
    }

    private static int deliveryColor(int pct) {
        float t = pct / 100f;
        int r = (int) (0xEF + (0x6E - 0xEF) * t);
        int g = (int) (0x44 + (0xE7 - 0x44) * t);
        int b = (int) (0x44 + (0xB7 - 0x44) * t);
        return Color.rgb(r, g, b);
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvCargo, tvStatus, tvReward, tvDeadline;
        ProgressBar pbDeadline;
        MaterialButton btnAccept;

        ViewHolder(View v) {
            super(v);
            tvRoute    = v.findViewById(R.id.tv_route);
            tvCargo    = v.findViewById(R.id.tv_cargo);
            tvStatus   = v.findViewById(R.id.tv_status);
            tvReward   = v.findViewById(R.id.tv_reward);
            tvDeadline = v.findViewById(R.id.tv_deadline);
            pbDeadline = v.findViewById(R.id.pb_deadline);
            btnAccept  = v.findViewById(R.id.btn_accept);
        }
    }
}
