package com.familymealplanner.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.familymealplanner.data.local.entity.InventoryTransactionEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class InventoryTransactionDao_Impl implements InventoryTransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<InventoryTransactionEntity> __insertionAdapterOfInventoryTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<InventoryTransactionEntity> __deletionAdapterOfInventoryTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<InventoryTransactionEntity> __updateAdapterOfInventoryTransactionEntity;

  public InventoryTransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInventoryTransactionEntity = new EntityInsertionAdapter<InventoryTransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `inventory_transactions` (`id`,`ingredient_id`,`quantity_change`,`status`,`reason`,`meal_plan_id`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InventoryTransactionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getIngredientId());
        statement.bindDouble(3, entity.getQuantityChange());
        statement.bindString(4, entity.getStatus());
        statement.bindString(5, entity.getReason());
        if (entity.getMealPlanId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMealPlanId());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfInventoryTransactionEntity = new EntityDeletionOrUpdateAdapter<InventoryTransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `inventory_transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InventoryTransactionEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfInventoryTransactionEntity = new EntityDeletionOrUpdateAdapter<InventoryTransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `inventory_transactions` SET `id` = ?,`ingredient_id` = ?,`quantity_change` = ?,`status` = ?,`reason` = ?,`meal_plan_id` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InventoryTransactionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getIngredientId());
        statement.bindDouble(3, entity.getQuantityChange());
        statement.bindString(4, entity.getStatus());
        statement.bindString(5, entity.getReason());
        if (entity.getMealPlanId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMealPlanId());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        statement.bindString(9, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final InventoryTransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfInventoryTransactionEntity.insert(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<InventoryTransactionEntity> transactions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfInventoryTransactionEntity.insert(transactions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final InventoryTransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfInventoryTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final InventoryTransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfInventoryTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id,
      final Continuation<? super InventoryTransactionEntity> $completion) {
    final String _sql = "SELECT * FROM inventory_transactions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<InventoryTransactionEntity>() {
      @Override
      @Nullable
      public InventoryTransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantityChange = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_change");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfMealPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_plan_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final InventoryTransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantityChange;
            _tmpQuantityChange = _cursor.getDouble(_cursorIndexOfQuantityChange);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpMealPlanId;
            if (_cursor.isNull(_cursorIndexOfMealPlanId)) {
              _tmpMealPlanId = null;
            } else {
              _tmpMealPlanId = _cursor.getString(_cursorIndexOfMealPlanId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new InventoryTransactionEntity(_tmpId,_tmpIngredientId,_tmpQuantityChange,_tmpStatus,_tmpReason,_tmpMealPlanId,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByIngredientId(final String ingredientId,
      final Continuation<? super List<InventoryTransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM inventory_transactions WHERE ingredient_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InventoryTransactionEntity>>() {
      @Override
      @NonNull
      public List<InventoryTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantityChange = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_change");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfMealPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_plan_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InventoryTransactionEntity> _result = new ArrayList<InventoryTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InventoryTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantityChange;
            _tmpQuantityChange = _cursor.getDouble(_cursorIndexOfQuantityChange);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpMealPlanId;
            if (_cursor.isNull(_cursorIndexOfMealPlanId)) {
              _tmpMealPlanId = null;
            } else {
              _tmpMealPlanId = _cursor.getString(_cursorIndexOfMealPlanId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new InventoryTransactionEntity(_tmpId,_tmpIngredientId,_tmpQuantityChange,_tmpStatus,_tmpReason,_tmpMealPlanId,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<InventoryTransactionEntity>> observeByIngredientId(final String ingredientId) {
    final String _sql = "SELECT * FROM inventory_transactions WHERE ingredient_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"inventory_transactions"}, new Callable<List<InventoryTransactionEntity>>() {
      @Override
      @NonNull
      public List<InventoryTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantityChange = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_change");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfMealPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_plan_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InventoryTransactionEntity> _result = new ArrayList<InventoryTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InventoryTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantityChange;
            _tmpQuantityChange = _cursor.getDouble(_cursorIndexOfQuantityChange);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpMealPlanId;
            if (_cursor.isNull(_cursorIndexOfMealPlanId)) {
              _tmpMealPlanId = null;
            } else {
              _tmpMealPlanId = _cursor.getString(_cursorIndexOfMealPlanId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new InventoryTransactionEntity(_tmpId,_tmpIngredientId,_tmpQuantityChange,_tmpStatus,_tmpReason,_tmpMealPlanId,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getByMealPlanId(final String mealPlanId,
      final Continuation<? super List<InventoryTransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM inventory_transactions WHERE meal_plan_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, mealPlanId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InventoryTransactionEntity>>() {
      @Override
      @NonNull
      public List<InventoryTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantityChange = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_change");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfMealPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_plan_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InventoryTransactionEntity> _result = new ArrayList<InventoryTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InventoryTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantityChange;
            _tmpQuantityChange = _cursor.getDouble(_cursorIndexOfQuantityChange);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpMealPlanId;
            if (_cursor.isNull(_cursorIndexOfMealPlanId)) {
              _tmpMealPlanId = null;
            } else {
              _tmpMealPlanId = _cursor.getString(_cursorIndexOfMealPlanId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new InventoryTransactionEntity(_tmpId,_tmpIngredientId,_tmpQuantityChange,_tmpStatus,_tmpReason,_tmpMealPlanId,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByIngredientIdAndStatus(final String ingredientId, final String status,
      final Continuation<? super List<InventoryTransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM inventory_transactions WHERE ingredient_id = ? AND status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    _argIndex = 2;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InventoryTransactionEntity>>() {
      @Override
      @NonNull
      public List<InventoryTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantityChange = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_change");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfMealPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_plan_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InventoryTransactionEntity> _result = new ArrayList<InventoryTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InventoryTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantityChange;
            _tmpQuantityChange = _cursor.getDouble(_cursorIndexOfQuantityChange);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpMealPlanId;
            if (_cursor.isNull(_cursorIndexOfMealPlanId)) {
              _tmpMealPlanId = null;
            } else {
              _tmpMealPlanId = _cursor.getString(_cursorIndexOfMealPlanId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new InventoryTransactionEntity(_tmpId,_tmpIngredientId,_tmpQuantityChange,_tmpStatus,_tmpReason,_tmpMealPlanId,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCommittedQuantityByIngredientId(final String ingredientId,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(quantity_change) FROM inventory_transactions WHERE ingredient_id = ? AND status = 'COMMITTED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getReservedQuantityByIngredientId(final String ingredientId,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(quantity_change) FROM inventory_transactions WHERE ingredient_id = ? AND status = 'RESERVED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCreatedSince(final long timestamp,
      final Continuation<? super List<InventoryTransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM inventory_transactions WHERE created_at > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InventoryTransactionEntity>>() {
      @Override
      @NonNull
      public List<InventoryTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantityChange = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_change");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfMealPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_plan_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InventoryTransactionEntity> _result = new ArrayList<InventoryTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InventoryTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantityChange;
            _tmpQuantityChange = _cursor.getDouble(_cursorIndexOfQuantityChange);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpMealPlanId;
            if (_cursor.isNull(_cursorIndexOfMealPlanId)) {
              _tmpMealPlanId = null;
            } else {
              _tmpMealPlanId = _cursor.getString(_cursorIndexOfMealPlanId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new InventoryTransactionEntity(_tmpId,_tmpIngredientId,_tmpQuantityChange,_tmpStatus,_tmpReason,_tmpMealPlanId,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<InventoryTransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM inventory_transactions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InventoryTransactionEntity>>() {
      @Override
      @NonNull
      public List<InventoryTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantityChange = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_change");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfMealPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_plan_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InventoryTransactionEntity> _result = new ArrayList<InventoryTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InventoryTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantityChange;
            _tmpQuantityChange = _cursor.getDouble(_cursorIndexOfQuantityChange);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpMealPlanId;
            if (_cursor.isNull(_cursorIndexOfMealPlanId)) {
              _tmpMealPlanId = null;
            } else {
              _tmpMealPlanId = _cursor.getString(_cursorIndexOfMealPlanId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new InventoryTransactionEntity(_tmpId,_tmpIngredientId,_tmpQuantityChange,_tmpStatus,_tmpReason,_tmpMealPlanId,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<InventoryTransactionEntity>> observeAll() {
    final String _sql = "SELECT * FROM inventory_transactions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"inventory_transactions"}, new Callable<List<InventoryTransactionEntity>>() {
      @Override
      @NonNull
      public List<InventoryTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantityChange = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity_change");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfMealPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_plan_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InventoryTransactionEntity> _result = new ArrayList<InventoryTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InventoryTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantityChange;
            _tmpQuantityChange = _cursor.getDouble(_cursorIndexOfQuantityChange);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpMealPlanId;
            if (_cursor.isNull(_cursorIndexOfMealPlanId)) {
              _tmpMealPlanId = null;
            } else {
              _tmpMealPlanId = _cursor.getString(_cursorIndexOfMealPlanId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new InventoryTransactionEntity(_tmpId,_tmpIngredientId,_tmpQuantityChange,_tmpStatus,_tmpReason,_tmpMealPlanId,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
