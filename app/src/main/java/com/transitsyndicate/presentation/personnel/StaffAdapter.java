package com.transitsyndicate.presentation.personnel;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.transitsyndicate.R;
import com.transitsyndicate.core.utils.BadgeUtils;
import com.transitsyndicate.core.utils.MoneyFormatter;
import com.transitsyndicate.domain.entity.personnel.Staff;

import java.util.ArrayList;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {

    public interface OnFire { void onFire(Staff staff); }

    private final List<Staff> items = new ArrayList<>();
    private final OnFire listener;

    public StaffAdapter(OnFire listener) { this.listener = listener; }

    public void submitList(List<Staff> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_staff, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Staff s = items.get(pos);

        h.tvName.setText(s.getName());
        h.tvType.setText(roleEmoji(s.getType().name()) + "  " + roleName(s.getType().name()));

        int reliabilityPct = (int)(s.getReliabilityRate() * 100);
        h.tvReliability.setText("⭐ " + reliabilityPct + "% reliability"
                + "   Lv." + s.getExperienceLevel());
        h.tvSalary.setText(MoneyFormatter.formatWithSymbol(s.getSalary()) + "/wk");

        if (s.isAvailable()) {
            BadgeUtils.set(h.tvAvailability, "✅ Available",
                    Color.parseColor("#6EE7B7"), 0xFF0B3328);
        } else {
            BadgeUtils.set(h.tvAvailability, "⏳ On route",
                    Color.parseColor("#818CF8"), 0xFF0F0F2A);
        }

        h.btnFire.setOnClickListener(v -> listener.onFire(s));
    }

    private static String roleEmoji(String type) {
        switch (type) {
            case "COURIER":    return "🧍";
            case "DRIVER":     return "🧑‍✈️";
            case "LOADER":     return "💪";
            case "DISPATCHER": return "📋";
            default:           return "👤";
        }
    }

    private static String roleName(String type) {
        switch (type) {
            case "COURIER":    return "Novice Courier";
            case "DRIVER":     return "Experienced Driver";
            case "LOADER":     return "Loader";
            case "DISPATCHER": return "Dispatcher";
            default:           return type;
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvReliability, tvSalary, tvAvailability;
        MaterialButton btnFire;

        ViewHolder(View v) {
            super(v);
            tvName         = v.findViewById(R.id.tv_staff_name);
            tvType         = v.findViewById(R.id.tv_staff_type);
            tvReliability  = v.findViewById(R.id.tv_staff_reliability);
            tvSalary       = v.findViewById(R.id.tv_staff_salary);
            tvAvailability = v.findViewById(R.id.tv_staff_availability);
            btnFire        = v.findViewById(R.id.btn_fire);
        }
    }
}
