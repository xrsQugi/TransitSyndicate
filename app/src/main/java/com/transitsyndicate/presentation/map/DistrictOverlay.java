package com.transitsyndicate.presentation.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.MotionEvent;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

public class DistrictOverlay extends Overlay {

    static final String[] NAMES         = {"Residential", "Business", "Industrial", "Global"};
    static final String[] EMOJIS        = {"🏘", "🏢", "🏭", "🌍"};
    static final int[]    COLORS        = {0xFF4CAF50, 0xFF2196F3, 0xFFFF9800, 0xFF9C27B0};
    static final int[]    UNLOCK_LEVELS = {0, 3, 7, 15};

    static final GeoPoint[][] POLYGONS = {
        {
            new GeoPoint(51.813, 19.373),
            new GeoPoint(51.813, 19.425),
            new GeoPoint(51.777, 19.425),
            new GeoPoint(51.777, 19.462),
            new GeoPoint(51.761, 19.462),
            new GeoPoint(51.761, 19.373),
        },
        {
            new GeoPoint(51.813, 19.431),
            new GeoPoint(51.813, 19.557),
            new GeoPoint(51.761, 19.557),
            new GeoPoint(51.761, 19.468),
            new GeoPoint(51.783, 19.468),
            new GeoPoint(51.783, 19.431),
        },
        {
            new GeoPoint(51.755, 19.481),
            new GeoPoint(51.755, 19.557),
            new GeoPoint(51.712, 19.557),
            new GeoPoint(51.712, 19.411),
        },
        {
            new GeoPoint(51.755, 19.373),
            new GeoPoint(51.755, 19.475),
            new GeoPoint(51.712, 19.405),
            new GeoPoint(51.712, 19.373),
        },
    };

    private int   playerLevel = 1;
    private int[] activeCount = new int[4];
    private float globalAnim  = 0f;

    private final Point[][] screenPoly = new Point[4][];

    private final Paint fillPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lockPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint subPaint      = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint badgePaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint badgeTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path  polyPath      = new Path();

    public interface DistrictClickListener {
        void onDistrictClick(int districtId, String name, boolean locked, int unlockLevel);
    }
    private DistrictClickListener clickListener;

    public DistrictOverlay() {
        strokePaint.setStyle(Paint.Style.STROKE);

        lockPaint.setColor(0xAA000000);

        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        subPaint.setTextSize(22f);
        subPaint.setTextAlign(Paint.Align.CENTER);

        badgePaint.setColor(0xFFFF5252);

        badgeTxtPaint.setColor(0xFFFFFFFF);
        badgeTxtPaint.setTextSize(20f);
        badgeTxtPaint.setTextAlign(Paint.Align.CENTER);
        badgeTxtPaint.setTypeface(Typeface.DEFAULT_BOLD);

        glowPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < 4; i++) {
            screenPoly[i] = new Point[POLYGONS[i].length];
            for (int j = 0; j < POLYGONS[i].length; j++) {
                screenPoly[i][j] = new Point();
            }
        }
    }

    public void setPlayerLevel(int level)                 { this.playerLevel = level; }
    public void setActiveCount(int[] counts)              { System.arraycopy(counts, 0, activeCount, 0, 4); }
    public void setGlobalAnim(float anim)                 { this.globalAnim = anim; }
    public void setClickListener(DistrictClickListener l) { this.clickListener = l; }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow) return;
        Projection proj = mapView.getProjection();

        for (int i = 0; i < 4; i++) {
            GeoPoint[] poly = POLYGONS[i];
            float cx = 0, cy = 0;
            for (int j = 0; j < poly.length; j++) {
                proj.toPixels(poly[j], screenPoly[i][j]);
                cx += screenPoly[i][j].x;
                cy += screenPoly[i][j].y;
            }
            cx /= poly.length;
            cy /= poly.length;

            drawDistrict(canvas, i, screenPoly[i], cx, cy);
        }
    }

    private void drawDistrict(Canvas canvas, int idx, Point[] pts, float cx, float cy) {
        boolean locked = playerLevel < UNLOCK_LEVELS[idx];
        int color = COLORS[idx];

        polyPath.reset();
        polyPath.moveTo(pts[0].x, pts[0].y);
        for (int j = 1; j < pts.length; j++) polyPath.lineTo(pts[j].x, pts[j].y);
        polyPath.close();

        fillPaint.setColor(color);
        fillPaint.setAlpha(locked ? 45 : 130);
        canvas.drawPath(polyPath, fillPaint);

        if (!locked && activeCount[idx] > 0) {
            float pulse = 0.5f + 0.5f * (float) Math.sin(globalAnim * Math.PI * 2);
            glowPaint.setColor(color);
            glowPaint.setAlpha((int) (100 + 155 * pulse));
            glowPaint.setStrokeWidth(5f + 7f * pulse);
            canvas.drawPath(polyPath, glowPaint);
        } else {
            strokePaint.setColor(color);
            strokePaint.setAlpha(locked ? 60 : 200);
            strokePaint.setStrokeWidth(2.5f);
            canvas.drawPath(polyPath, strokePaint);
        }

        if (locked) {
            lockPaint.setColor(0xAA000000);
            canvas.drawPath(polyPath, lockPaint);
            textPaint.setAlpha(180);
            canvas.drawText("🔒", cx, cy + 12f, textPaint);
            subPaint.setColor(0xFFB0BEC5);
            subPaint.setAlpha(200);
            canvas.drawText("Level " + UNLOCK_LEVELS[idx], cx, cy + 40f, subPaint);
            textPaint.setAlpha(255);
            subPaint.setAlpha(255);
        } else {
            textPaint.setTextSize(28f);
            canvas.drawText(EMOJIS[idx], cx, cy - 10f, textPaint);

            textPaint.setTextSize(24f);
            canvas.drawText(NAMES[idx], cx, cy + 18f, textPaint);

            if (activeCount[idx] > 0) {
                subPaint.setColor(0xFFFFD700);
                canvas.drawText("▶ " + activeCount[idx] + " active", cx, cy + 42f, subPaint);

                float bx = cx + 45f;
                float by = cy - 45f;
                canvas.drawCircle(bx, by, 15f, badgePaint);
                canvas.drawText(String.valueOf(activeCount[idx]), bx, by + 7f, badgeTxtPaint);
            } else {
                subPaint.setColor(0xFFB0BEC5);
                canvas.drawText("Idle", cx, cy + 42f, subPaint);
            }
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (clickListener == null) return false;
        float tx = e.getX(), ty = e.getY();
        for (int i = 0; i < 4; i++) {
            if (screenPoly[i] != null && pointInPolygon(tx, ty, screenPoly[i])) {
                boolean locked = playerLevel < UNLOCK_LEVELS[i];
                clickListener.onDistrictClick(i + 1, NAMES[i], locked, UNLOCK_LEVELS[i]);
                return true;
            }
        }
        return false;
    }

    private static boolean pointInPolygon(float px, float py, Point[] pts) {
        boolean inside = false;
        int n = pts.length;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            float xi = pts[i].x, yi = pts[i].y;
            float xj = pts[j].x, yj = pts[j].y;
            if ((yi > py) != (yj > py) && px < (xj - xi) * (py - yi) / (yj - yi) + xi) {
                inside = !inside;
            }
        }
        return inside;
    }
}
