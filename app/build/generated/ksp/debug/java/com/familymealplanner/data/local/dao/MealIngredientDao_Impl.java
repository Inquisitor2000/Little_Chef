package com.familymealplanner.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.familymealplanner.data.local.entity.MealIngredientEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class MealIngredientDao_Impl implements MealIngredientDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MealIngredientEntity> __insertionAdapterOfMealIngredientEntity;

  private final EntityDeletionOrUpdateAdapter<MealIngredientEntity> __deletionAdapterOfMealIngredientEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMealId;

  public MealIngredientDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMealIngredientEntity = new EntityInsertionAdapter<MealIngredientEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `meal_ingredients` (`id`,`meal_id`,`ingredient_id`,`quantity`,`unit`,`is_star_ingredient`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealIngredientEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getMealId());
        statement.bindString(3, entity.getIngredientId());
        statement.bindDouble(4, entity.getQuantity());
        if (entity.getUnit() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getUnit());
        }
        final int _tmp = entity.isStarIngredient() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__deletionAdapterOfMealIngredientEntity = new EntityDeletionOrUpdateAdapter<MealIngredientEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `meal_ingredients` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealIngredientEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByMealId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM meal_ingredients WHERE meal_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MealIngredientEntity mealIngredient,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMealIngredientEntity.insert(mealIngredient);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<MealIngredientEntity> mealIngredients,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMealIngredientEntity.insert(mealIngredients);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MealIngredientEntity mealIngredient,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMealIngredientEntity.handle(mealIngredient);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByMealId(final String mealId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByMealId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, mealId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByMealId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getByMealId(final String mealId,
      final Continuation<? super List<MealIngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_ingredients WHERE meal_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, mealId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealIngredientEntity>>() {
      @Override
      @NonNull
      public List<MealIngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfIsStarIngredient = CursorUtil.getColumnIndexOrThrow(_cursor, "is_star_ingredient");
          final List<MealIngredientEntity> _result = new ArrayList<MealIngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealIngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final boolean _tmpIsStarIngredient;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarIngredient);
            _tmpIsStarIngredient = _tmp != 0;
            _item = new MealIngredientEntity(_tmpId,_tmpMealId,_tmpIngredientId,_tmpQuantity,_tmpUnit,_tmpIsStarIngredient);
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
  public Flow<List<MealIngredientEntity>> observeByMealId(final String mealId) {
    final String _sql = "SELECT * FROM meal_ingredients WHERE meal_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, mealId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meal_ingredients"}, new Callable<List<MealIngredientEntity>>() {
      @Override
      @NonNull
      public List<MealIngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfIsStarIngredient = CursorUtil.getColumnIndexOrThrow(_cursor, "is_star_ingredient");
          final List<MealIngredientEntity> _result = new ArrayList<MealIngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealIngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final boolean _tmpIsStarIngredient;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarIngredient);
            _tmpIsStarIngredient = _tmp != 0;
            _item = new MealIngredientEntity(_tmpId,_tmpMealId,_tmpIngredientId,_tmpQuantity,_tmpUnit,_tmpIsStarIngredient);
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
  public Object getByIngredientId(final String ingredientId,
      final Continuation<? super List<MealIngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_ingredients WHERE ingredient_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealIngredientEntity>>() {
      @Override
      @NonNull
      public List<MealIngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfIsStarIngredient = CursorUtil.getColumnIndexOrThrow(_cursor, "is_star_ingredient");
          final List<MealIngredientEntity> _result = new ArrayList<MealIngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealIngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final boolean _tmpIsStarIngredient;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarIngredient);
            _tmpIsStarIngredient = _tmp != 0;
            _item = new MealIngredientEntity(_tmpId,_tmpMealId,_tmpIngredientId,_tmpQuantity,_tmpUnit,_tmpIsStarIngredient);
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
  public Object countByIngredientId(final String ingredientId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM meal_ingredients WHERE ingredient_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ingredientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getAll(final Continuation<? super List<MealIngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_ingredients";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealIngredientEntity>>() {
      @Override
      @NonNull
      public List<MealIngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfIsStarIngredient = CursorUtil.getColumnIndexOrThrow(_cursor, "is_star_ingredient");
          final List<MealIngredientEntity> _result = new ArrayList<MealIngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealIngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final boolean _tmpIsStarIngredient;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarIngredient);
            _tmpIsStarIngredient = _tmp != 0;
            _item = new MealIngredientEntity(_tmpId,_tmpMealId,_tmpIngredientId,_tmpQuantity,_tmpUnit,_tmpIsStarIngredient);
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
  public Object getChangedSince(final long timestamp,
      final Continuation<? super List<MealIngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM meal_ingredients WHERE id > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealIngredientEntity>>() {
      @Override
      @NonNull
      public List<MealIngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealId = CursorUtil.getColumnIndexOrThrow(_cursor, "meal_id");
          final int _cursorIndexOfIngredientId = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredient_id");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfIsStarIngredient = CursorUtil.getColumnIndexOrThrow(_cursor, "is_star_ingredient");
          final List<MealIngredientEntity> _result = new ArrayList<MealIngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealIngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMealId;
            _tmpMealId = _cursor.getString(_cursorIndexOfMealId);
            final String _tmpIngredientId;
            _tmpIngredientId = _cursor.getString(_cursorIndexOfIngredientId);
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final boolean _tmpIsStarIngredient;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStarIngredient);
            _tmpIsStarIngredient = _tmp != 0;
            _item = new MealIngredientEntity(_tmpId,_tmpMealId,_tmpIngredientId,_tmpQuantity,_tmpUnit,_tmpIsStarIngredient);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
