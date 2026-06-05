package com.transitsyndicate.presentation.game;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.transitsyndicate.R;
import com.transitsyndicate.TransitSyndicateApp;
import com.transitsyndicate.core.utils.GameNotification;
import com.transitsyndicate.core.base.BaseActivity;
import com.transitsyndicate.core.utils.MoneyFormatter;
import com.transitsyndicate.data.local.preferences.GamePreferences;
import com.transitsyndicate.presentation.buildings.BuildingsFragment;
import com.transitsyndicate.presentation.fleet.FleetFragment;
import com.transitsyndicate.presentation.map.MapFragment;
import com.transitsyndicate.presentation.orders.OrdersFragment;
import com.transitsyndicate.presentation.personnel.PersonnelFragment;

public class MainActivity extends BaseActivity {

    private GameViewModel viewModel;
    private FrameLayout notificationHost;

    private TextView tvMoney;
    private TextView tvLevel;
    private TextView tvXp;
    private TextView tvTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMoney = findViewById(R.id.tv_hud_money);
        tvLevel = findViewById(R.id.tv_hud_level);
        tvXp    = findViewById(R.id.tv_hud_xp);
        tvTick  = findViewById(R.id.tv_hud_tick);
        notificationHost = findViewById(R.id.notification_host);

        applyWindowInsets();

        viewModel = new ViewModelProvider(this, new GameViewModelFactory(getApplication()))
                .get(GameViewModel.class);

        viewModel.player.observe(this, p -> updateHud());
        viewModel.currentTick.observe(this, t -> updateHud());

        viewModel.toast.observe(this, msg -> {
            if (msg == null || msg.isEmpty()) return;
            boolean isError = msg.contains("not enough") || msg.contains("not available") || msg.contains("failed");
            GameNotification.Type type = isError ? GameNotification.Type.ERROR : GameNotification.Type.INFO;
            GameNotification.show(notificationHost, msg, type);
            viewModel.toast.setValue(null);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment;
            if      (id == R.id.nav_map)       fragment = new MapFragment();
            else if (id == R.id.nav_orders)    fragment = new OrdersFragment();
            else if (id == R.id.nav_fleet)     fragment = new FleetFragment();
            else if (id == R.id.nav_staff)     fragment = new PersonnelFragment();
            else if (id == R.id.nav_buildings) fragment = new BuildingsFragment();
            else return false;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_map);
        }

        viewModel.startGame();

        showOnboardingIfNeeded();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.stopGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewModel != null) viewModel.startGame();
    }

    private void applyWindowInsets() {
        float density = getResources().getDisplayMetrics().density;
        int dp10 = Math.round(10 * density);
        int dp16 = Math.round(16 * density);

        LinearLayout hudInner = findViewById(R.id.hud_inner_layout);
        ViewCompat.setOnApplyWindowInsetsListener(hudInner, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(dp16, bars.top + dp10, dp16, dp10);
            return insets;
        });

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        ViewCompat.setOnApplyWindowInsetsListener(nav, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });
    }

    private void updateHud() {
        com.transitsyndicate.domain.entity.player.Player p = viewModel.player.getValue();
        Long tick = viewModel.currentTick.getValue();
        if (p == null) return;

        tvMoney.setText("💰 " + MoneyFormatter.formatWithSymbol(p.getMoney()));
        tvLevel.setText("Lv." + p.getLevel());
        tvXp.setText("XP " + p.getExperience());
        tvTick.setText("T:" + (tick != null ? tick : 0));
    }

    private void showOnboardingIfNeeded() {
        GamePreferences prefs = ((TransitSyndicateApp) getApplication()).container.prefs;
        if (prefs.isOnboardingDone()) return;

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.onboarding_title))
                .setMessage(getString(R.string.onboarding_body))
                .setPositiveButton(getString(R.string.onboarding_btn), (d, w) ->
                        prefs.setOnboardingDone())
                .setCancelable(false)
                .show();
    }
}
