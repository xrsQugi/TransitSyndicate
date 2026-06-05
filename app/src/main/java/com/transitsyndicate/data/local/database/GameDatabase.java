package com.transitsyndicate.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.transitsyndicate.data.local.database.dao.BuildingDao;
import com.transitsyndicate.data.local.database.dao.DistrictDao;
import com.transitsyndicate.data.local.database.dao.OrderDao;
import com.transitsyndicate.data.local.database.dao.PlayerDao;
import com.transitsyndicate.data.local.database.dao.StaffDao;
import com.transitsyndicate.data.local.database.dao.TransportDao;
import com.transitsyndicate.data.local.database.entity.BuildingEntity;
import com.transitsyndicate.data.local.database.entity.DistrictEntity;
import com.transitsyndicate.data.local.database.entity.OrderEntity;
import com.transitsyndicate.data.local.database.entity.PlayerEntity;
import com.transitsyndicate.data.local.database.entity.StaffEntity;
import com.transitsyndicate.data.local.database.entity.TransportEntity;

@Database(
        entities = {
                PlayerEntity.class,
                TransportEntity.class,
                OrderEntity.class,
                StaffEntity.class,
                BuildingEntity.class,
                DistrictEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class GameDatabase extends RoomDatabase {

    private static volatile GameDatabase instance;

    public abstract PlayerDao playerDao();
    public abstract TransportDao transportDao();
    public abstract OrderDao orderDao();
    public abstract StaffDao staffDao();
    public abstract BuildingDao buildingDao();
    public abstract DistrictDao districtDao();

    public static GameDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (GameDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    GameDatabase.class,
                                    "transit_syndicate.db"
                            )
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
