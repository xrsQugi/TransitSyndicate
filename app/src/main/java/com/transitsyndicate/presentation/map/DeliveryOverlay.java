package com.transitsyndicate.presentation.map;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.transitsyndicate.domain.entity.order.Order;
import com.transitsyndicate.domain.entity.order.OrderStatus;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DeliveryOverlay extends Overlay {

    private static final int[]  COLORS    = DistrictOverlay.COLORS;
    private static final float  PHASE_STEP = 0.007f;
    private static final int    MAX_TRAIL  = 40;

    private static final GeoPoint[][] STREETS = {
        {
            new GeoPoint(51.805, 19.383),
            new GeoPoint(51.797, 19.402),
            new GeoPoint(51.787, 19.393),
            new GeoPoint(51.773, 19.432),
            new GeoPoint(51.765, 19.418),
        },
        {
            new GeoPoint(51.805, 19.480),
            new GeoPoint(51.795, 19.510),
            new GeoPoint(51.787, 19.465),
            new GeoPoint(51.775, 19.478),
            new GeoPoint(51.765, 19.540),
        },
        {
            new GeoPoint(51.750, 19.490),
            new GeoPoint(51.743, 19.510),
            new GeoPoint(51.733, 19.520),
            new GeoPoint(51.723, 19.505),
            new GeoPoint(51.717, 19.480),
        },
        {
            new GeoPoint(51.750, 19.405),
            new GeoPoint(51.742, 19.415),
            new GeoPoint(51.733, 19.395),
            new GeoPoint(51.725, 19.388),
            new GeoPoint(51.718, 19.378),
        },
    };

   
    private static final String[][] STREET_NAMES = {
        {"ul. Zgierska", "ul. Bałucki Rynek", "al. Włókniarzy", "ul. Limanowskiego", "ul. Srebrzyńska"},
        {"ul. Piotrkowska", "al. Mickiewicza", "ul. Kilińskiego", "Pl. Wolności", "ul. Narutowicza"},
        {"ul. Przybyszewskiego", "ul. Milionowa", "ul. Brzezińska", "ul. Widzewska", "ul. Rokicińska"},
        {"ul. Rzgowska", "ul. Pabianicka", "ul. Łagiewnicka", "al. Lotnicza", "ul. Chojny"},
    };

    private static class OrderRoute {
        final GeoPoint pickup;
        final GeoPoint dropoff;
        final GeoPoint control; 
        final int      color;
        final String   pickupLabel;
        final String   dropoffLabel;

        OrderRoute(GeoPoint pickup, GeoPoint dropoff, GeoPoint control,
                   int color, String pickupLabel, String dropoffLabel) {
            this.pickup       = pickup;
            this.dropoff      = dropoff;
            this.control      = control;
            this.color        = color;
            this.pickupLabel  = pickupLabel;
            this.dropoffLabel = dropoffLabel;
        }
    }

    private final Map<Integer, int[]>             routes      = new HashMap<>(); 
    private final Map<Integer, Float>             phases      = new HashMap<>();
    private final Map<Integer, OrderRoute>        orderRoutes = new HashMap<>();
    private final Map<Integer, LinkedList<Float>> trailPhases = new HashMap<>();

    private final Paint routePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trailPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path  routePath   = new Path();

    private final Point ptPickup  = new Point();
    private final Point ptControl = new Point();
    private final Point ptDropoff = new Point();

    public DeliveryOverlay() {
        routePaint.setStyle(Paint.Style.STROKE);
        routePaint.setStrokeWidth(3f);
        routePaint.setPathEffect(new DashPathEffect(new float[]{14f, 9f}, 0f));

        trailPaint.setStyle(Paint.Style.STROKE);
        trailPaint.setStrokeCap(Paint.Cap.ROUND);
        trailPaint.setStrokeWidth(5f);

        markerPaint.setStyle(Paint.Style.STROKE);
        markerPaint.setStrokeWidth(2.5f);

        labelPaint.setColor(0xFFFFFFFF);
        labelPaint.setTextSize(20f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void updateDeliveries(List<Order> orders) {
        routes.clear();
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.IN_PROGRESS) {
                int from = o.getFromDistrictId() - 1;
                int to   = o.getToDistrictId()   - 1;
                if (from >= 0 && from < 4 && to >= 0 && to < 4) {
                    routes.put(o.getId(), new int[]{from, to});
                    if (!phases.containsKey(o.getId())) {
                        phases.put(o.getId(), 0f);
                        orderRoutes.put(o.getId(), buildRoute(o.getId(), from, to));
                        trailPhases.put(o.getId(), new LinkedList<>());
                    }
                }
            }
        }
        phases.keySet().retainAll(routes.keySet());
        orderRoutes.keySet().retainAll(routes.keySet());
        trailPhases.keySet().retainAll(routes.keySet());
    }

    private OrderRoute buildRoute(int orderId, int fromIdx, int toIdx) {
        Random rng = new Random(orderId);

        GeoPoint pickup  = STREETS[fromIdx][rng.nextInt(STREETS[fromIdx].length)];
        GeoPoint dropoff = STREETS[toIdx  ][rng.nextInt(STREETS[toIdx  ].length)];

        double ctrlLat = (pickup.getLatitude()  + dropoff.getLatitude())  / 2.0
                + (rng.nextDouble() - 0.5) * 0.010;
        double ctrlLon = (pickup.getLongitude() + dropoff.getLongitude()) / 2.0
                + (rng.nextDouble() - 0.5) * 0.010;

        String pLabel = STREET_NAMES[fromIdx][rng.nextInt(STREET_NAMES[fromIdx].length)];
        String dLabel = STREET_NAMES[toIdx  ][rng.nextInt(STREET_NAMES[toIdx  ].length)];

        return new OrderRoute(pickup, dropoff,
                new GeoPoint(ctrlLat, ctrlLon),
                COLORS[fromIdx], pLabel, dLabel);
    }

    public void advanceAnimation() {
        for (Integer id : new ArrayList<>(phases.keySet())) {
            Float cur = phases.get(id);
            float prev = (cur != null ? cur : 0f);
            float ph   = prev + PHASE_STEP;
            boolean looped = ph >= 1f;
            if (looped) ph -= 1f;
            phases.put(id, ph);

            LinkedList<Float> trail = trailPhases.get(id);
            if (trail != null) {
                if (looped) trail.clear(); 
                trail.addLast(ph);
                while (trail.size() > MAX_TRAIL) trail.removeFirst();
            }
        }
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow || routes.isEmpty()) return;
        Projection proj = mapView.getProjection();

        for (Map.Entry<Integer, int[]> entry : routes.entrySet()) {
            int orderId = entry.getKey();
            OrderRoute or = orderRoutes.get(orderId);
            Float phVal  = phases.get(orderId);
            if (or == null || phVal == null) continue;

            proj.toPixels(or.pickup,  ptPickup);
            proj.toPixels(or.control, ptControl);
            proj.toPixels(or.dropoff, ptDropoff);

            routePath.reset();
            routePath.moveTo(ptPickup.x, ptPickup.y);
            routePath.quadTo(ptControl.x, ptControl.y, ptDropoff.x, ptDropoff.y);
            routePaint.setColor(or.color);
            routePaint.setAlpha(90);
            canvas.drawPath(routePath, routePaint);

            markerPaint.setColor(or.color);
            markerPaint.setAlpha(200);
            canvas.drawCircle(ptPickup.x, ptPickup.y, 9f, markerPaint);
            labelPaint.setColor(or.color);
            labelPaint.setAlpha(220);
            canvas.drawText(or.pickupLabel, ptPickup.x, ptPickup.y - 14f, labelPaint);

            dotPaint.setColor(or.color);
            dotPaint.setAlpha(200);
            canvas.drawCircle(ptDropoff.x, ptDropoff.y, 9f, dotPaint);
            dotPaint.setColor(0xFFFFFFFF);
            dotPaint.setAlpha(200);
            canvas.drawCircle(ptDropoff.x, ptDropoff.y, 4f, dotPaint);
            labelPaint.setColor(0xFFFFFFFF);
            labelPaint.setAlpha(200);
            canvas.drawText(or.dropoffLabel, ptDropoff.x, ptDropoff.y - 14f, labelPaint);

            LinkedList<Float> trail = trailPhases.get(orderId);
            if (trail != null && trail.size() > 1) {
                drawTrail(canvas, trail, or);
            }

            float t    = easeInOut(phVal);
            float[] cp = bezier(ptPickup, ptControl, ptDropoff, t);

            dotPaint.setColor(0x55000000);
            dotPaint.setAlpha(0x55);
            canvas.drawCircle(cp[0] + 3f, cp[1] + 3f, 14f, dotPaint);

            dotPaint.setColor(or.color);
            dotPaint.setAlpha(235);
            canvas.drawCircle(cp[0], cp[1], 14f, dotPaint);

            dotPaint.setColor(0xFFFFFFFF);
            dotPaint.setAlpha(210);
            canvas.drawCircle(cp[0], cp[1], 6f, dotPaint);
            dotPaint.setAlpha(255);
        }
    }

    private void drawTrail(Canvas canvas, LinkedList<Float> trail, OrderRoute or) {
        Float[] arr = trail.toArray(new Float[0]);
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            float alpha = (float)(i + 1) / n; 
            float[] a = bezier(ptPickup, ptControl, ptDropoff, easeInOut(arr[i]));
            float[] b = bezier(ptPickup, ptControl, ptDropoff, easeInOut(arr[i + 1]));
            trailPaint.setColor(or.color);
            trailPaint.setAlpha((int)(alpha * 190));
            canvas.drawLine(a[0], a[1], b[0], b[1], trailPaint);
        }
    }

    private static float[] bezier(Point p0, Point p1, Point p2, float t) {
        float mt = 1f - t;
        return new float[]{
            mt * mt * p0.x + 2f * mt * t * p1.x + t * t * p2.x,
            mt * mt * p0.y + 2f * mt * t * p1.y + t * t * p2.y,
        };
    }

    private static float easeInOut(float t) {
        return t < 0.5f ? 2f * t * t : -1f + (4f - 2f * t) * t;
    }
}
