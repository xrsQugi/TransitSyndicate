package com.transitsyndicate.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.transitsyndicate.data.local.database.entity.OrderEntity;

import java.util.List;

@Dao
public interface OrderDao {
    @Query("SELECT * FROM orders")
    List<OrderEntity> getAll();

    @Query("SELECT * FROM orders WHERE id = :id")
    OrderEntity getById(int id);

    @Query("SELECT * FROM orders WHERE status = :status")
    List<OrderEntity> getByStatus(String status);

    @Query("SELECT * FROM orders WHERE status NOT IN ('COMPLETED', 'FAILED')")
    List<OrderEntity> getActive();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(OrderEntity entity);

    @Update
    void update(OrderEntity entity);

    @Query("DELETE FROM orders WHERE id = :id")
    void delete(int id);
}
