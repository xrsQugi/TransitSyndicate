package com.transitsyndicate.presentation.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameView extends View {
    private static final String[] DISTRICT_NAMES  = {"Residential", "Business", "Industrial", "Global"};
    private static final int[]    DISTRICT_COLORS = {0xFF4CAF50, 0xFF2196F3, 0xFFFF9800, 0xFF9C27B0};
    private static final int[]    UNLOCK_LEVELS   = {0, 3, 7, 15};
    private static final String[] DISTRICT_EMOJIS = {"🏘", "🏢", "🏭", "🌍"};

    private final Paint tilePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint subPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lockPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint badgeTxt   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int playerLevel = 1;
    private final RectF[] districtRects = new RectF[4];

    private final Map<Integer, int[]> activeDeliveries = new HashMap<>();
    private final Map<Integer, Float> deliveryPhases = new HashMap<>();
    private final int[] districtActiveCount = new int[4];

    private float globalAnim = 0f;         
    private final Runnable animTick = new Runnable() {
        @Override public void run() {
            globalAnim = (globalAnim + 0.025f) % 1f;
            for (Integer id : new ArrayList<>(deliveryPhases.keySet())) {
                Float cur = deliveryPhases.get(id);
                float ph = (cur != null ? cur : 0f) + 0.012f;
                if (ph >= 1f) ph = 0f;
                deliveryPhases.put(id, ph);
            }
            invalidate();
            postDelayed(this, 33); 
        }
    };

    public interface OnDistrictClickListener {
        void onDistrictClick(int districtId, String name, boolean locked, int unlockLevel);
    }
    private OnDistrictClickListener clickListener;

    public GameView(Context context) { super(context); init(); }
    public GameView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        subPaint.setColor(0xFFB0BEC5);
        subPaint.setTextSize(22f);
        subPaint.setTextAlign(Paint.Align.CENTER);

        lockPaint.setColor(0xCC000000);

        badgePaint.setColor(0xFFFF5252);
        badgeTxt.setColor(Color.WHITE);
        badgeTxt.setTextSize(22f);
        badgeTxt.setTextAlign(Paint.Align.CENTER);
        badgeTxt.setFakeBoldText(true);

        setBackgroundColor(0xFF1A1A2E);
        for (int i = 0; i < 4; i++) districtRects[i] = new RectF();
    }

    public void setOnDistrictClickListener(OnDistrictClickListener l) { this.clickListener = l; }

    public void setPlayerLevel(int level) {
        this.playerLevel = level;
        invalidate();
    }

    public void updateDeliveries(List<Order> orders) {
        activeDeliveries.clear();
        for (int i = 0; i < 4; i++) districtActiveCount[i] = 0;

        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.IN_PROGRESS) {
                int fromIdx = o.getFromDistrictId() - 1;
                int toIdx   = o.getToDistrictId()   - 1;
                if (fromIdx >= 0 && fromIdx < 4 && toIdx >= 0 && toIdx < 4) {
                    activeDeliveries.put(o.getId(), new int[]{fromIdx, toIdx});
                    districtActiveCount[fromIdx]++;
                    if (!deliveryPhases.containsKey(o.getId())) {
                        deliveryPhases.put(o.getId(), (float) Math.random());
                    }
                }
            }
        }
        deliveryPhases.keySet().retainAll(activeDeliveries.keySet());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(animTick);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(animTick);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        float pad = 14f, gap = 10f;
        float cellW = (w - pad * 2 - gap) / 2f;
        float cellH = (h - pad * 2 - gap) / 2f;

        for (int i = 0; i < 4; i++) {
            int col = i % 2, row = i / 2;
            float l = pad + col * (cellW + gap);
            float t = pad + row * (cellH + gap);
            districtRects[i].set(l, t, l + cellW, t + cellH);
        }

        for (int i = 0; i < 4; i++) drawDistrict(canvas, i);

        drawDeliveryDots(canvas);
    }

    private void drawDistrict(Canvas canvas, int idx) {
        RectF r = districtRects[idx];
        boolean locked = playerLevel < UNLOCK_LEVELS[idx];

        int baseColor = DISTRICT_COLORS[idx];
        tilePaint.setColor(baseColor);
        tilePaint.setAlpha(locked ? 80 : 200);
        canvas.drawRoundRect(r, 20f, 20f, tilePaint);

        if (!locked && districtActiveCount[idx] > 0) {
            float pulse = 0.5f + 0.5f * (float) Math.sin(globalAnim * Math.PI * 2);
            glowPaint.setColor(baseColor);
            glowPaint.setAlpha((int)(80 + 120 * pulse));
            glowPaint.setStyle(Paint.Style.STROKE);
            glowPaint.setStrokeWidth(4f);
            canvas.drawRoundRect(r, 20f, 20f, glowPaint);
            glowPaint.setStyle(Paint.Style.FILL);
        }

        float cx = r.centerX(), cy = r.centerY();

        if (locked) {
            lockPaint.setColor(0xAA000000);
            canvas.drawRoundRect(r, 20f, 20f, lockPaint);
            textPaint.setAlpha(160);
            canvas.drawText("🔒", cx, cy - 16f, textPaint);
            subPaint.setAlpha(200);
            canvas.drawText("Level " + UNLOCK_LEVELS[idx], cx, cy + 28f, subPaint);
            textPaint.setAlpha(255);
            subPaint.setAlpha(255);
        } else {
            textPaint.setTextSize(32f);
            canvas.drawText(DISTRICT_EMOJIS[idx], cx, cy - 28f, textPaint);

            textPaint.setTextSize(28f);
            canvas.drawText(DISTRICT_NAMES[idx], cx, cy + 10f, textPaint);

            if (districtActiveCount[idx] > 0) {
                subPaint.setColor(0xFFFFD700);
                canvas.drawText("▶ " + districtActiveCount[idx] + " delivering",
                        cx, cy + 36f, subPaint);
                subPaint.setColor(0xFFB0BEC5);
            } else {
                subPaint.setColor(0xFF6B7F9E);
                canvas.drawText("No activity", cx, cy + 36f, subPaint);
                subPaint.setColor(0xFFB0BEC5);
            }
        }
    }

    private void drawDeliveryDots(Canvas canvas) {
        for (Map.Entry<Integer, int[]> entry : activeDeliveries.entrySet()) {
            int orderId = entry.getKey();
            int fromIdx = entry.getValue()[0];
            int toIdx   = entry.getValue()[1];

            Float phVal = deliveryPhases.get(orderId);
            float phase = phVal != null ? phVal : 0f;

            RectF from = districtRects[fromIdx];
            RectF to   = districtRects[toIdx];

            float t = easeInOut(phase);
            float dotX = lerp(from.centerX(), to.centerX(), t);
            float dotY = lerp(from.centerY(), to.centerY(), t)
                    - (float) Math.sin(phase * Math.PI) * 60f;

            dotPaint.setColor(0x44000000);
            canvas.drawCircle(dotX + 3, dotY + 3, 14f, dotPaint);

            dotPaint.setColor(DISTRICT_COLORS[fromIdx]);
            dotPaint.setAlpha(220);
            canvas.drawCircle(dotX, dotY, 14f, dotPaint);

            dotPaint.setColor(Color.WHITE);
            dotPaint.setAlpha(180);
            canvas.drawCircle(dotX, dotY, 6f, dotPaint);
            dotPaint.setAlpha(255);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP && clickListener != null) {
            float x = e.getX(), y = e.getY();
            for (int i = 0; i < 4; i++) {
                if (districtRects[i].contains(x, y)) {
                    boolean locked = playerLevel < UNLOCK_LEVELS[i];
                    clickListener.onDistrictClick(i + 1, DISTRICT_NAMES[i],
                            locked, UNLOCK_LEVELS[i]);
                    return true;
                }
            }
        }
        return true;
    }

    private static float lerp(float a, float b, float t) { return a + (b - a) * t; }

    private static float easeInOut(float t) {
        return t < 0.5f ? 2f * t * t : -1f + (4f - 2f * t) * t;
    }
}
